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
    private Boolean yourTurn;
    private String status;
    private String side;

    public String getId() {
        return id;
    }

    public Board getBoard() {
        return board;
    }

    public Boolean getYourTurn() {
        return yourTurn;
    }

    public String getStatus() {
        return status;
    }

    public String getSide() {
        return side;
    }

    public Game(String player1, int size, int startingPieces) {
        this.id = UUID.randomUUID().toString();
        this.player1 = player1;
        this.nextPlayer = player1;
        this.board = new Board(size, startingPieces);
    }

    public boolean joinGame(String player2) {
        if(this.player2 != null || this.player1.equals(player2)) {
            return false;
        }
        this.player2 = player2;
        return true;
    }

    public boolean requestMove(String playerId, int pocket) {
        if(!hasStarted() || !nextPlayer.equals(playerId)) {
            return false;
        }

        // Try to make move and assign next player
        int moveResult = this.board.makeMove(getPlayerNumber(playerId), pocket);
        if(moveResult == 1) {
            nextPlayer = player1;
            return true;
        } else if (moveResult == -1) {
            nextPlayer = player2;
            return true;
        } else {
            return false;
        }
    }

    private int getPlayerNumber(String player) {
        if(player.equals(player1)) {
            return 1;
        } else if (player.equals(player2)) {
            return -1;
        } else {
            return 0;
        }
    }

    public boolean hasStarted() {
        return player1 != null && player2 != null;
    }

    public boolean hasPlayer(String playerId) {
        return playerId != null && (player1 != null && player1.equals(playerId) || player2 != null && player2.equals(playerId));
    }

    public void updateGameStatus(String playerId) {
        yourTurn = nextPlayer.equals(playerId);
        status = this.hasStarted() ? this.board.determineGameStatus() : "Waiting for opponent";
        side = player1.equals(playerId) ? "top" : "bottom";
    }
}
