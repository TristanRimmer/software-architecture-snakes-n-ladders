package uk.ac.mmu.game.domain.pieces.positiontrackers;

import uk.ac.mmu.game.domain.util.GridPosition;
import uk.ac.mmu.game.domain.util.PositiveIntegerAtleastFive;

public sealed interface PositionTrackingConverter
        permits LowerLeftOrigin, LowerRightOrigin, UpperLeftOrigin, UpperRightOrigin {
    GridPosition displacementToGridPosition(
        int displacement, 
        PositiveIntegerAtleastFive gridWidth, 
        PositiveIntegerAtleastFive gridHeight);

    int gridPositionToDisplacement(
        GridPosition position, 
        PositiveIntegerAtleastFive gridWidth, 
        PositiveIntegerAtleastFive gridHeight);
}
