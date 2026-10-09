package uk.ac.mmu.game.domain.board;

import java.util.List;

import uk.ac.mmu.game.domain.board.specialpositions.SpecialLinkedPositions;
import uk.ac.mmu.game.domain.util.GridPosition;
import uk.ac.mmu.game.domain.util.PositiveIntWithMinimum;

public interface Board {
    boolean pieceHasLandedOnSpecialSpot(GridPosition position);
    GridPosition getSpecialPositionBehaviour(GridPosition position);
    PositiveIntWithMinimum getBoardWidth();
    PositiveIntWithMinimum getBoardHeight();
    int getMinimumTravelDistance();
    boolean registerNewSpecialPosition(SpecialLinkedPositions newPosition);
    List<SpecialLinkedPositions> getSpecialPositions();
}
