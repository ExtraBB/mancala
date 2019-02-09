package com.extrabb.mancala;

import java.util.concurrent.atomic.AtomicLong;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GameController {

    private final AtomicLong counter = new AtomicLong();

    @RequestMapping(path="/game", method= RequestMethod.GET)
    public Game game(@RequestParam(value="name", defaultValue="World") String name) {
        return new Game(counter.incrementAndGet(), name);
    }
}
