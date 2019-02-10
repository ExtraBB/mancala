package com.extrabb.mancala;

import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@CrossOrigin(origins="http://localhost:4200", allowCredentials="true") //For debug only
@RestController
public class GameController {

    private Map<String, Game> activeGamesByPlayer = new HashMap<String, Game>();
    private Map<String, Game> activeGamesById = new HashMap<String, Game>();
    Logger logger = Logger.getLogger("debug");

    private final SimpMessagingTemplate template;

    @Autowired
    GameController(SimpMessagingTemplate template) {
        this.template = template;
    }

    @RequestMapping(path="/register", method= RequestMethod.GET)
    public ResponseEntity register(HttpServletResponse response) {
        String playerId = UUID.randomUUID().toString();
        logger.log(Level.INFO, "set cookie: " + playerId);

        Cookie cookie = new Cookie("player-id", playerId);
        cookie.setMaxAge(60*60*24*7); // 1 week expiry
        cookie.setHttpOnly(false);
        response.addCookie(cookie);

        return ResponseEntity.status(HttpStatus.OK).body(playerId);
    }

    @RequestMapping(path="/game", method= RequestMethod.GET)
    public ResponseEntity getGame(@CookieValue(value="player-id") String playerId, HttpServletResponse response) {
        logger.log(Level.INFO, playerId);
        if(playerId != null && activeGamesByPlayer.containsKey(playerId)) {
            Game game = activeGamesByPlayer.get(playerId);
            game.updateGameStatus(playerId);
            return ResponseEntity.status(HttpStatus.OK).body(game);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Game was not found");
        }
    }

    @RequestMapping(path="/games", method= RequestMethod.GET)
    public ResponseEntity getGames(@CookieValue(value="player-id") String playerId) {
        List<String> games = activeGamesById
                .values()
                .stream()
                .filter(game -> !game.hasStarted() && !game.hasPlayer(playerId))
                .map(game -> game.getId())
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(games);
    }

    @RequestMapping(path="/game", method= RequestMethod.PUT)
    public Game createGame(@CookieValue("player-id") String playerId, @RequestParam(value="size") int size, @RequestParam(value="pieces") int pieces, HttpServletResponse response) {
        Game game = new Game(playerId, size, pieces);
        activeGamesByPlayer.put(playerId, game);
        activeGamesById.put(game.getId(), game);

        game.updateGameStatus(playerId);
        this.sendGame(game);
        return game;
    }

    @RequestMapping(path="/game/join", method= RequestMethod.POST)
    public ResponseEntity joinGame(@CookieValue("player-id") String playerId, @RequestParam(value="id") String id, HttpServletResponse response) {
        if(activeGamesById.containsKey(id)) {
            Game game = activeGamesById.get(id);
            boolean result = game.joinGame(playerId);
            if(result) {
                activeGamesByPlayer.put(playerId, game);
                game.updateGameStatus(playerId);
                return ResponseEntity.status(HttpStatus.OK).body(game);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Unable to join game");
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Game was not found");
        }
    }

    @RequestMapping(path="/game/move", method= RequestMethod.POST)
    public ResponseEntity requestMove(@CookieValue("player-id") String playerId, @RequestParam(value="pocket") int pocket) {
        if(activeGamesByPlayer.containsKey(playerId)) {
            Game game = activeGamesByPlayer.get(playerId);
            boolean result = game.requestMove(playerId, pocket);
            if(result) {
                game.updateGameStatus(playerId);
                sendGame(game);
                return ResponseEntity.status(HttpStatus.OK).body(game);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid move");
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Game was not found");
        }
    }

    private void sendGame(Game game) {
        this.template.convertAndSend("/game/" + game.getId() + "/" + game.getNextPlayer(), game);
    }
}
