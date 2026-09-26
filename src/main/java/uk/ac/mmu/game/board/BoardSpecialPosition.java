package uk.ac.mmu.game.board;

import uk.ac.mmu.game.shared.GridPosition;

public interface BoardSpecialPosition {
    boolean pieceHasLandedOnSpecialSpot(GridPosition position);
    
    GridPosition getSpecialPositionBehaviour(GridPosition position);
}
