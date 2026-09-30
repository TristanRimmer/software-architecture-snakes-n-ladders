package uk.ac.mmu.game.domain.board.specialpositions;

import java.util.List;

import uk.ac.mmu.game.domain.util.GridPosition;

public sealed interface SpecialLinkedPositions permits OneWayTeleporter, TwoWayTeleporter {
    boolean hasLandedOnThis(GridPosition landedPos);

    GridPosition getPositionAfterSpecialBehaviour(GridPosition landedPos);

    List<GridPosition> getListOfSpecialPositions();
}
