package uk.ac.mmu.game.domain.board.specialpositions;

import java.util.ArrayList;
import java.util.List;

import uk.ac.mmu.game.domain.util.GridPosition;

public final class OneWayTeleporter implements SpecialLinkedPositions {
    private final GridPosition teleportsFromHere;
    private final GridPosition teleportsToHere;

    public OneWayTeleporter(GridPosition posA, GridPosition posB) {
        this.teleportsFromHere = posA;
        this.teleportsToHere = posB;
    }

    @Override
    public GridPosition getPositionAfterSpecialBehaviour(GridPosition landedPos) {
        if (landedPos.equals(this.teleportsFromHere)) {
            return this.teleportsToHere;
        }
        // Not its concern
        return landedPos;
    }

    @Override
    public boolean hasLandedOnThis(GridPosition landedPos) {
        return landedPos.equals(this.teleportsFromHere);
    }

    @Override
    public List<GridPosition> getListOfSpecialPositions() {
        ArrayList<GridPosition> positions = new ArrayList<>();

        positions.add(this.teleportsFromHere);
        positions.add(this.teleportsToHere);

        return positions;
    }
}
