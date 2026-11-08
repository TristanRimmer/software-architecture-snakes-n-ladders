package uk.ac.mmu.game.domain.pieces.positiontrackers;

import uk.ac.mmu.game.domain.util.BoardDimension;
import uk.ac.mmu.game.domain.util.GridPosition;

public sealed interface PositionTrackingConverter
        permits LowerLeftOrigin, LowerRightOrigin, UpperLeftOrigin, UpperRightOrigin {
    GridPosition displacementToGridPosition(
        int displacement, 
        BoardDimension gridWidth, 
        BoardDimension gridHeight);

    int gridPositionToDisplacement(
        GridPosition position, 
        BoardDimension gridWidth, 
        BoardDimension gridHeight);
}
