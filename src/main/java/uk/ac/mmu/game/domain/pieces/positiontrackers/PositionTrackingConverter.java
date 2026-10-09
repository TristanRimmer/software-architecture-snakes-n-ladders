package uk.ac.mmu.game.domain.pieces.positiontrackers;

import uk.ac.mmu.game.domain.util.GridPosition;
import uk.ac.mmu.game.domain.util.PositiveIntWithMinimum;

public sealed interface PositionTrackingConverter
        permits LowerLeftOrigin, LowerRightOrigin, UpperLeftOrigin, UpperRightOrigin {
    GridPosition displacementToGridPosition(
        int displacement, 
        PositiveIntWithMinimum gridWidth, 
        PositiveIntWithMinimum gridHeight);

    int gridPositionToDisplacement(
        GridPosition position, 
        PositiveIntWithMinimum gridWidth, 
        PositiveIntWithMinimum gridHeight);
}
