package uk.ac.mmu.game.pieces;

import uk.ac.mmu.game.shared.GridPosition;

public final class LLtoUR implements PositionTrackingConverter {
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
        // On a 5x5 grid @ [0,3] this is reporting -9 when it should be -5
        return (position.y() * gridWidth) 
            + (position.y() % 2 == 0 ? position.x() : gridWidth - 1 - position.x());
    }

}
