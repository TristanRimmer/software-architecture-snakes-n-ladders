package uk.ac.mmu.game.domain.board;

import uk.ac.mmu.game.domain.shared.GridPosition;

public interface BoardSpecialPosition {
    boolean pieceHasLandedOnSpecialSpot(GridPosition position);
    
    GridPosition getSpecialPositionBehaviour(GridPosition position);
}
