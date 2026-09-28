package uk.ac.mmu.game.domain.board;

import java.util.ArrayList;

import uk.ac.mmu.game.domain.shared.GridPosition;

public interface SpecialLinkedGridPositionBehaviour {
    boolean hasLandedOnThis(GridPosition landedPos);
    ArrayList<GridPosition> getListOfSpecialPositions();
    GridPosition getPositionAfterSpecialBehaviour(GridPosition landedPos);
}
