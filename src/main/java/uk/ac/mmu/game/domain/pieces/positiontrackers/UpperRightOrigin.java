package uk.ac.mmu.game.domain.pieces.positiontrackers;

import uk.ac.mmu.game.domain.util.GridPosition;

public final class UpperRightOrigin implements PositionTrackingConverter {
    @Override
    public GridPosition displacementToGridPosition(int displacement, int gridWidth, int gridHeight) {
        int numFullRows = displacement / gridWidth;
        int spacesIntoCurrentRow = displacement % gridWidth;

        return new GridPosition(
            (numFullRows % 2 != 0 ?
                spacesIntoCurrentRow : gridWidth - 1 - spacesIntoCurrentRow),
            gridHeight - 1 - numFullRows
        );
    }

    @Override
    public int gridPositionToDisplacement(GridPosition position, int gridWidth, int gridHeight) {
        return ((gridHeight - 1 - position.y()) * gridWidth) 
            + (position.y() % 2 != 0 ? position.x() : gridWidth - 1 - position.x());
    }

}
