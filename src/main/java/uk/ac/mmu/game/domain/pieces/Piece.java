package uk.ac.mmu.game.domain.pieces;

import uk.ac.mmu.game.domain.pieces.positiontrackers.PositionTrackingConverter;
import uk.ac.mmu.game.domain.util.GridPosition;

public interface Piece {
    // Given an increment, it should propose the new position on the grid of the
    // piece
    GridPosition move(int increment);

    // Sets the position
    void setPosition(GridPosition newPos);

    // Gets the current position
    GridPosition getCurrentPosition();

    // Gets the displacement
    int getDisplacement();

    // TODO: consider a refactor -> is this getting too big? (ISP)
    PositionTrackingConverter getTrackingConverter();
}
