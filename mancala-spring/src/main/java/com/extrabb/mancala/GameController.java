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

    private final SimpMessagingTemplate template;

    @Autowired
    GameController(SimpMessagingTemplate template) {
        this.template = template;
    }

    /**
     * The register route to set a cookie on the client with their player id
     * @param response used to set cookie
     * @return the player id
     */
    @RequestMapping(path="/register", method= RequestMethod.GET)
    public ResponseEntity register(HttpServletResponse response) {
        String playerId = UUID.randomUUID().toString();

        Cookie cookie = new Cookie("player-id", playerId);
        cookie.setMaxAge(60*60*24*7); // 1 week expiry
        cookie.setHttpOnly(false);
        response.addCookie(cookie);

        return ResponseEntity.status(HttpStatus.OK).body(playerId);
    }

    /**
     * The GET route for a game
     * @param playerId The player id retrieved from the cookie
     * @return the game that a user is currently in.
     */
    @RequestMapping(path="/game", method= RequestMethod.GET)
    public ResponseEntity getGame(@CookieValue(value="player-id") String playerId) {
        if(playerId != null && activeGamesByPlayer.containsKey(playerId)) {
            Game game = activeGamesByPlayer.get(playerId);
            game.updateGameStatus(playerId);
            return ResponseEntity.status(HttpStatus.OK).body(game);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Game was not found");
        }
    }

    /**
     * Get a list of all open game IDs
     * @param playerId The player id retrieved from the cookie
     * @return a list of game IDs
     */
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

    /**
     * Create a new game
     * @param playerId The player id retrieved from the cookie
     * @param size The size of the board (must be an even number)
     * @param pieces The number of pieces per square (at least 1)
     * @return the game
     */
    @RequestMapping(path="/game", method= RequestMethod.PUT)
    public Game createGame(@CookieValue("player-id") String playerId, @RequestParam(value="size") int size, @RequestParam(value="pieces") int pieces) {
        Game game = new Game(playerId, size, pieces);
        activeGamesByPlayer.put(playerId, game);
        activeGamesById.put(game.getId(), game);

        game.updateGameStatus(playerId);
        this.sendGame(game);
        return game;
    }

    /**
     * Join an open game
     * @param playerId The player id retrieved from the cookie
     * @param id the id of the game
     * @return The joined game
     */
    @RequestMapping(path="/game/join", method= RequestMethod.POST)
    public ResponseEntity joinGame(@CookieValue("player-id") String playerId, @RequestParam(value="id") String id) {
        if(activeGamesById.containsKey(id)) {
            Game game = activeGamesById.get(id);
            boolean result = game.joinGame(playerId);
            if(result) {
                activeGamesByPlayer.put(playerId, game);
                game.updateGameStatus(playerId);
                this.sendGame(game);
                return ResponseEntity.status(HttpStatus.OK).body(game);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Unable to join game");
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Game was not found");
        }
    }

    /**
     * Request to perform a move
     * @param playerId The player id retrieved from the cookie
     * @param pocket The pocket to perform the move for
     * @return the updated game
     */
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

    /**
     * Send the game to websocket subscribers for that game ID and player ID (of the next player)
     * @param game the game to send
     */
    private void sendGame(Game game) {
        this.template.convertAndSend("/game/" + game.getId() + "/" + game.getNextPlayer(), game);
    }
}
