package com.extrabb.mancala;

public class Game {
    private final long id;
    private final String name;

    public Game(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
