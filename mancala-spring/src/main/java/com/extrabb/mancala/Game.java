package com.extrabb.mancala;

import java.util.UUID;

//---------------------------------------------------//
//       |     |     |     |     |     |     |       //
//       |  1  |  2  |  3  |  4  |  5  |  6  |       //
//       |     |     |     |     |     |     |       //
//   0   |------------------------------------   7   //
//       |     |     |     |     |     |     |       //
//       |  1  |  1  |  1  |  1  |  9  |  8  |       //
//       |  3  |  2  |  1  |  0  |     |     |       //
//---------------------------------------------------//

public class Game {
    // Game Metadata
    private final String id;
    private final String player1;
    private String player2;

    // Game state
    private int[] board;
    private String nextPlayer;

    public Game(String player1) {
        this.id = UUID.randomUUID().toString();
        this.player1 = player1;
    }

    private void initializeGame() {
        this.board = new int[14];
        for(int i = 0; i < 14; i++) {
            if(i % 7 == 0) {
                continue;
            }
            board[i] = 6;
        }
        this.nextPlayer = player1;
    }

    public boolean joinGame(String player2) {
        if(this.player2 != null) {
            return false;
        }
        this.player2 = player2;
        this.initializeGame();
        return true;
    }

    public String getId() {
        return id;
    }

    public int[] getBoard() {
        return board;
    }

    public String nextPlayer() {
        return nextPlayer;
    }
}
