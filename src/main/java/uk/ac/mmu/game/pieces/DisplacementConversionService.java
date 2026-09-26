package uk.ac.mmu.game.pieces;

import uk.ac.mmu.game.shared.GridPosition;

public sealed interface DisplacementConversionService permits LLtoUR {
    GridPosition displacementToGridPosition(int displacement, int gridWidth, int gridHeight);

    int gridPositionToDisplacement(GridPosition position, int gridWidth, int gridHeight);
}
