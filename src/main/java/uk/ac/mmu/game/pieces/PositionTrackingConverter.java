package uk.ac.mmu.game.pieces;

import uk.ac.mmu.game.shared.GridPosition;

public interface PositionTrackingConverter {
    GridPosition displacementToGridPosition(int displacement, int gridWidth, int gridHeight);

    int gridPositionToDisplacement(GridPosition position, int gridWidth, int gridHeight);
}
