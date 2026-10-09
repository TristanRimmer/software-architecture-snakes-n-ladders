package uk.ac.mmu.game.domain.pieces.positiontrackers;

import uk.ac.mmu.game.domain.util.GridPosition;
import uk.ac.mmu.game.domain.util.PositiveIntegerAtleastFive;

public final class UpperLeftOrigin implements PositionTrackingConverter {
    // For ULtoLR, if the numFullRows % 2 == 0 then we are traversing right, and left overwise
    @Override
    public GridPosition displacementToGridPosition(int displacement, PositiveIntegerAtleastFive gridWidth, PositiveIntegerAtleastFive gridHeight) {
        int numFullRows = displacement / gridWidth.getValue();
        int spacesIntoCurrentRow = displacement % gridWidth.getValue();

        return new GridPosition(
            (numFullRows % 2 == 0 ?
                spacesIntoCurrentRow : gridWidth.getValue() - 1 - spacesIntoCurrentRow),
            gridHeight.getValue() - 1 - numFullRows
        );
    }

    @Override
    public int gridPositionToDisplacement(GridPosition position, PositiveIntegerAtleastFive gridWidth, PositiveIntegerAtleastFive gridHeight) {
        return ((gridHeight.getValue() - 1 - position.y()) * gridWidth.getValue()) 
            + ((gridHeight.getValue() - 1 - position.y()) % 2 == 0 ? position.x() : gridWidth.getValue() - 1 - position.x());
    }
}
