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
        boolean incorrectPocket1 = player == 1 && (pocket > 5 || pocket < 0);
        boolean incorrectPocket2 = player == 2 && (pocket < 7 || pocket > 12);
        if(incorrectPocket1 || incorrectPocket2) {
            return false;
        } else {
            return this.pockets[pocket] > 0;
        }
    }

    public boolean makeMove(int player, int pocket) {
        if(!isLegalMove(player, pocket)) {
            return false;
        }

        int stones = this.pockets[pocket];
        this.pockets[pocket] = 0;

        int nextPocket = pocket;
        while(stones != 0) {
            nextPocket = (nextPocket + 1) % BOARD_SIZE;
            if(player == 1 && nextPocket == BIG_PIT_2 || player == 2 && nextPocket == BIG_PIT_1) {
                continue;
            }
            this.pockets[nextPocket]++;
            stones--;
        }
        return true;
    }

    public int[] getPockets() {
        return pockets;
    }
}
