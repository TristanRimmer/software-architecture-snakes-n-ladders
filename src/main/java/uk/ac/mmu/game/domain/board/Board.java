package uk.ac.mmu.game.domain.board;

import uk.ac.mmu.game.domain.shared.GridPosition;

public interface Board {
    boolean pieceHasLandedOnSpecialSpot(GridPosition position);
    GridPosition getSpecialPositionBehaviour(GridPosition position);
    int getBoardWidth();
    int getBoardHeight();
    int getMinimumTravelDistance();
}
