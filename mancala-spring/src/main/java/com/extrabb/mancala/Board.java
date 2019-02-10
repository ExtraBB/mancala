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
        if(!isLegalMove(player, pocket)) {
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

        // Player can go again if he finishes in his own big pit
        if(nextPocket == BIG_PIT_1 || nextPocket == BIG_PIT_2) {
            return player;
        } else {
            return -player;
        }
    }

    public int[] getPockets() {
        return pockets;
    }

    public String determineGameStatus() {
        int player1Sum = Arrays.stream(this.pockets).limit(BOARD_SIZE / 2 - 1).sum();
        int player2Sum = Arrays.stream(this.pockets).skip(BOARD_SIZE / 2).limit(BOARD_SIZE / 2 - 1).sum();

        if(player1Sum == 0) {
            return String.format("Player 1 won with %d stones in his big pit", this.pockets[BOARD_SIZE / 2 - 1]);
        } else if(player2Sum == 0) {
            return String.format("Player 2 won with %d stones in his big pit", this.pockets[BOARD_SIZE - 1]);
        } else {
            return "Playing";
        }
    }
}
