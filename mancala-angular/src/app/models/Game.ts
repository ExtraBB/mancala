import Board from './Board';

export default interface Game {
    id: String;
    nextPlayer: String;
    board: Board;
    status: String;
}