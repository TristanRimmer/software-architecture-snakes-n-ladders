package uk.ac.mmu.game.domain.pieces.positiontrackers;

import uk.ac.mmu.game.domain.util.BoardDimension;
import uk.ac.mmu.game.domain.util.GridPosition;

public final class LowerRightOrigin implements PositionTrackingConverter {
    @Override
    public GridPosition displacementToGridPosition(int displacement, BoardDimension gridWidth, BoardDimension gridHeight) {
        int numFullRows = displacement / gridWidth.getValue();
        int spacesIntoCurrentRow = displacement % gridWidth.getValue();

        return new GridPosition(
            (numFullRows % 2 != 0 ?
                spacesIntoCurrentRow : gridWidth.getValue() - 1 - spacesIntoCurrentRow),
            numFullRows
        );
    }

    @Override
    public int gridPositionToDisplacement(GridPosition position, BoardDimension gridWidth, BoardDimension gridHeight) {
        return (position.y() * gridWidth.getValue()) 
            + (position.y() % 2 != 0 ? position.x() : gridWidth.getValue() - 1 - position.x());
    }

}
