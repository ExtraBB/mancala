package com.extrabb.mancala;

import java.util.UUID;

public class Game {
    // Game Metadata
    private final String id;
    private final String player1;
    private String player2;

    // Game state
    private Board board;
    private String nextPlayer;

    public Game(String player1) {
        this.id = UUID.randomUUID().toString();
        this.player1 = player1;
    }

    private void initializeGame() {
        this.board = new Board(14, 6);
        this.nextPlayer = player1;
    }

    public boolean joinGame(String player2) {
        if(this.player2 != null || this.player1 == player2) {
            return false;
        }
        this.player2 = player2;
        this.initializeGame();
        return true;
    }

    public boolean requestMove(String playerId, int pocket) {
        if(nextPlayer != playerId) {
            return false;
        }
        return this.board.makeMove(getPlayerNumber(playerId), pocket);
    }

    private int getPlayerNumber(String player) {
        if(player == player1) {
            return 1;
        } else if (player == player2) {
            return 2;
        } else {
            return -1;
        }
    }

    public String getId() {
        return id;
    }

    public Board getBoard() {
        return board;
    }

    public String nextPlayer() {
        return nextPlayer;
    }
}
