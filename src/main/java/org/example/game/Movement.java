package org.example.game;

public interface Movement {
    /*
    * when a piece moves from a starting position to end position
    * then the state of the board changes
    * */
    MoveState move(Board board, Position start, Position end);

    /*
    * Undo a move that is made by move to resotre the original
    * board state this is done to avoid copy board
    * */

    void undo(Board board, Position start, Position end, MoveState state);


    /*
    * a move is considered valid is it does not result in check for
    * the piece implementing this method additionly the piece should be inside bounds
    * */
    boolean isValidMove(Board board, Position current, Position end);

    // TODO: update board evaluation score when a piece is captured
    // will be implemented once engine evaluation algorithm is ready
    void capture(Board board, Position current, Position end);
}
