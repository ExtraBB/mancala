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
    private String status;

    /**
     * Getter for the game id (used by JACKSON)
     * @return the game id
     */
    public String getId() {
        return id;
    }

    /**
     * Getter for the board (used by JACKSON)
     * @return the board
     */
    public Board getBoard() {
        return board;
    }

    /**
     * Getter for the next player (used by JACKSON)
     * @return the next player
     */
    public String getNextPlayer() {
        return nextPlayer;
    }

    /**
     * Getter for the player 1 (used by JACKSON)
     * @return the player 1
     */
    public String getPlayer1() {
        return player1;
    }

    /**
     * Getter for the player 2 (used by JACKSON)
     * @return the player 2
     */
    public String getPlayer2() {
        return player2;
    }

    /**
     * Getter for the game status(used by JACKSON)
     * @return the game status
     */
    public String getStatus() {
        return status;
    }

    public Game(String player1, int size, int startingPieces) {
        this.id = UUID.randomUUID().toString();
        this.player1 = player1;
        this.nextPlayer = player1;
        this.board = new Board(size, startingPieces);
    }

    /**
     * Join an existing game
     * @param player2 the id of the player that would likes to join
     * @return whether the join succeeded
     */
    public boolean joinGame(String player2) {
        if(this.player2 != null || this.player1.equals(player2)) {
            return false;
        }
        this.player2 = player2;
        return true;
    }

    /**
     * Request to make a move
     * @param playerId The player that wants to make the move
     * @param pocket The pocket to make the move on
     * @return Whether the move succeeded
     */
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

    /**
     * Get the number of a player (1 for player1 and -1 for player2)
     * @param player the id of the player
     * @return the player number
     */
    private int getPlayerNumber(String player) {
        if(player.equals(player1)) {
            return 1;
        } else if (player.equals(player2)) {
            return -1;
        } else {
            return 0;
        }
    }

    /**
     * Check whether the game has started.
     * @return whether the game has started
     */
    public boolean hasStarted() {
        return player1 != null && player2 != null;
    }

    /**
     * Check if a player is in a game
     * @param playerId the id of the player
     * @return whether the player is in the game
     */
    public boolean hasPlayer(String playerId) {
        return playerId != null && (player1 != null && player1.equals(playerId) || player2 != null && player2.equals(playerId));
    }

    /**
     * Update the status of the game (used when sending it back to the user
     * @param playerId the id of the active player.
     */
    public void updateGameStatus(String playerId) {
        status = this.hasStarted() ? this.board.determineGameStatus() : "Waiting for opponent";
    }
}
