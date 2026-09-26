package uk.ac.mmu.game.board;

import java.util.ArrayList;

import uk.ac.mmu.game.shared.GridPosition;

public interface SpecialLinkedGridPositionBehaviour {
    boolean hasLandedOnThis(GridPosition landedPos);
    ArrayList<GridPosition> getListOfSpecialPositions();
    GridPosition getPositionAfterSpecialBehaviour(GridPosition landedPos);
}
