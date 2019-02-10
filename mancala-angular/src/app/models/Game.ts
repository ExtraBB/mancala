import Board from './Board';

export default interface Game {
    id: String;
    nextPlayer: String;
    player1: String;
    player2: String;
    board: Board;
    status: String;
}