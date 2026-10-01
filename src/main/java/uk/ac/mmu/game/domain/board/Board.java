package uk.ac.mmu.game.domain.board;

import uk.ac.mmu.game.domain.board.specialpositions.SpecialLinkedPositions;
import uk.ac.mmu.game.domain.util.GridPosition;

public interface Board {
    boolean pieceHasLandedOnSpecialSpot(GridPosition position);
    GridPosition getSpecialPositionBehaviour(GridPosition position);
    int getBoardWidth();
    int getBoardHeight();
    int getMinimumTravelDistance();
    void registerNewSpecialPosition(SpecialLinkedPositions newPosition);
}
