package uk.ac.mmu.game.domain.board;

import java.util.List;

import uk.ac.mmu.game.domain.board.specialpositions.SpecialLinkedPositions;
import uk.ac.mmu.game.domain.util.GridPosition;
import uk.ac.mmu.game.domain.util.PositiveIntegerAtleastFive;

// TODO/IDEA: make the board contain references to an outside object? Eliminates the need for the getter and register
public interface Board {
    boolean pieceHasLandedOnSpecialSpot(GridPosition position);
    GridPosition getSpecialPositionBehaviour(GridPosition position);
    PositiveIntegerAtleastFive getBoardWidth();
    PositiveIntegerAtleastFive getBoardHeight();
    int getMinimumTravelDistance();
    boolean registerNewSpecialPosition(SpecialLinkedPositions newPosition);
    List<SpecialLinkedPositions> getSpecialPositions();
}
