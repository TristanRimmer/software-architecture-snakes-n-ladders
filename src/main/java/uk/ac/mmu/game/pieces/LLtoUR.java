package uk.ac.mmu.game.pieces;

import uk.ac.mmu.game.shared.GridPosition;

public final class LLtoUR implements DisplacementConversionService {
    // For LLtoUR, if the numFullRows % 2 == 0 then we are traversing right, and left overwise
    @Override
    public GridPosition displacementToGridPosition(int displacement, int gridWidth, int gridHeight) {
        int numFullRows = displacement / gridWidth;
        int spacesIntoCurrentRow = displacement % gridWidth;

        return new GridPosition(
            (numFullRows % 2 == 0 ?
                spacesIntoCurrentRow : gridWidth - 1 - spacesIntoCurrentRow),
            numFullRows
        );
    }

    @Override
    public int gridPositionToDisplacement(GridPosition position, int gridWidth, int gridHeight) {
        return (position.y() * gridWidth) 
            + (position.y() % 2 == 0 ? position.x() : gridWidth - 1 - position.x());
    }

}
