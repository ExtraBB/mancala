package com.extrabb.mancala;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class GameController {

    private Map<String, Game> activeGames = new HashMap<String, Game>();

    @RequestMapping(path="/game", method= RequestMethod.GET)
    public ResponseEntity getGame(@RequestParam(value="id") String id) {
        if(activeGames.containsKey(id)) {
            return ResponseEntity.status(HttpStatus.OK).body(activeGames.get(id));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Game was not found");
        }
    }

    @RequestMapping(path="/games", method= RequestMethod.GET)
    public ResponseEntity getGames() {
        List<String> gameIds = activeGames.values().stream().map(game -> game.getId()).collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(gameIds);
    }

    @RequestMapping(path="/game", method= RequestMethod.PUT)
    public Game createGame(HttpServletRequest request) {
        Game game = new Game(request.getSession().getId());
        activeGames.put(game.getId(), game);
        return game;
    }

    @RequestMapping(path="/game/join", method= RequestMethod.POST)
    public ResponseEntity createGame(@RequestParam(value="id") String id, HttpServletRequest request) {
        if(activeGames.containsKey(id)) {
            Game game = activeGames.get(id);
            boolean result = game.joinGame(request.getSession().getId());
            if(result) {
                return ResponseEntity.status(HttpStatus.OK).body(game);
            } else {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Game already in progress");
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Game was not found");
        }
    }
}
