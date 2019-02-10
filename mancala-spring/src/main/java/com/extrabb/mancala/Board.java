package com.extrabb.mancala;

//---------------------------------------------------//
//       |     |     |     |     |     |     |       //
//       |  5  |  4  |  3  |  2  |  1  |  0  |       //
//       |     |     |     |     |     |     |       //
//   6   |------------------------------------   1   //
//       |     |     |     |     |     |     |   3   //
//       |  7  |  8  |  9  |  1  |  1  |  1  |       //
//       |     |     |     |  0  |  1  |  2  |       //
//---------------------------------------------------//

import java.util.Arrays;

public class Board {

    private final int BOARD_SIZE;
    private final int BIG_PIT_1;
    private final int BIG_PIT_2;

    private int[] pockets;
    private boolean finished = false;

    public int[] getPockets() {
        return pockets;
    }

    public Board(int size, int startingPieces) {
        if(size <= 0 || size % 2 != 0) {
            throw new Error("Invalid board size");
        }
        this.BOARD_SIZE = size;
        this.BIG_PIT_1 = BOARD_SIZE / 2 - 1;
        this.BIG_PIT_2 = BOARD_SIZE - 1;
        this.pockets = new int[size];

        this.resetBoard(startingPieces);
    }

    private void resetBoard(int startingPieces) {
        for(int i = 0; i < this.BOARD_SIZE; i++) {
            if(i == this.BIG_PIT_1 || i == this.BIG_PIT_2) {
                this.pockets[i] = 0;
                continue;
            }
            this.pockets[i] = startingPieces;
        }
    }

    public boolean isLegalMove(int player, int pocket) {
        boolean incorrectPocket1 = player == 1 && (pocket > BOARD_SIZE / 2 - 2 || pocket < 0);
        boolean incorrectPocket2 = player == -1 && (pocket < BOARD_SIZE / 2 || pocket > BOARD_SIZE - 2);
        if(incorrectPocket1 || incorrectPocket2) {
            return false;
        } else {
            return this.pockets[pocket] > 0;
        }
    }

    public int makeMove(int player, int pocket) {
        if(finished || !isLegalMove(player, pocket)) {
            return 0;
        }

        int stones = this.pockets[pocket];
        this.pockets[pocket] = 0;

        // Sow stones
        int nextPocket = pocket;
        while(stones != 0) {
            nextPocket = (nextPocket + 1) % BOARD_SIZE;
            if(player == 1 && nextPocket == BIG_PIT_2 || player == -1 && nextPocket == BIG_PIT_1) {
                continue;
            }
            this.pockets[nextPocket]++;
            stones--;
        }

        // Capture stones
        this.tryCaptureStones(nextPocket, player);

        // Player can go again if he finishes in his own big pit
        if(nextPocket == BIG_PIT_1 || nextPocket == BIG_PIT_2) {
            return player;
        } else {
            return -player;
        }
    }

    private boolean pocketBelongsToPlayer(int pocket, int player) {
        if(player == 1) {
            return pocket > 0 && pocket < BOARD_SIZE / 2 - 1;
        } else {
            return pocket > BOARD_SIZE / 2 - 1 && pocket < BOARD_SIZE - 1;
        }
    }

    private void tryCaptureStones(int lastPocket, int player) {
        if(this.pockets[lastPocket] == 1 && this.pocketBelongsToPlayer(lastPocket, player)) {
            int oppositePit = lastPocket + 2 * (BOARD_SIZE / 2 - lastPocket - 1);
            this.pockets[lastPocket] += this.pockets[oppositePit];
            this.pockets[oppositePit] = 0;
        }
    }

    public String determineGameStatus() {
        int player1Sum = Arrays.stream(this.pockets).limit(BOARD_SIZE / 2 - 1).sum();
        int player2Sum = Arrays.stream(this.pockets).skip(BOARD_SIZE / 2).limit(BOARD_SIZE / 2 - 1).sum();

        if(player1Sum == 0 || player2Sum == 0) {
            finished = true;
            int player1Score = this.pockets[BOARD_SIZE / 2 - 1] + player1Sum;
            int player2Score = this.pockets[BOARD_SIZE - 1] + player2Sum;
            if(player1Score > player2Score) {
                return String.format("Player 1 won with %d stones in his big pit", player1Score);
            } else if (player1Score < player2Score) {
                return String.format("Player 2 won with %d stones in his big pit", player2Score);
            } else {
                return "It's a draw!";
            }
        } else {
            return "Playing";
        }
    }
}
