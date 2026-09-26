package uk.ac.mmu.game.pieces;

import uk.ac.mmu.game.shared.GridPosition;

public interface PieceMoveset {
    // Given an increment, it should propose the new position on the grid of the piece
    GridPosition getPositionFromMove(int increment);
    // Sets the position
    void setPosition(GridPosition newPos);
    // Gets the current position
    GridPosition getCurrentPosition();
}
