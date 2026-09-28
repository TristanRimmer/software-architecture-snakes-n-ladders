package uk.ac.mmu.game.domain.board.specialpositions;

import java.util.ArrayList;
import java.util.List;

import uk.ac.mmu.game.domain.util.GridPosition;


public class Teleporter implements SpecialLinkedPositions{
    GridPosition positionOne;
    GridPosition positionTwo;

    public Teleporter(GridPosition posA, GridPosition posB) {
        this.positionOne = posA;
        this.positionTwo = posB;
    }

    @Override
    public GridPosition getPositionAfterSpecialBehaviour(GridPosition landedPos) {
        if (landedPos.equals(this.positionOne)) {
            return this.positionTwo;
        }
        if (landedPos.equals(this.positionTwo)) {
            return this.positionOne;
        }
        // Not its concern
        return landedPos;
    }

    @Override
    public boolean hasLandedOnThis(GridPosition landedPos) {
        return landedPos.equals(this.positionOne) || landedPos.equals(this.positionTwo);
    }

    @Override
    public List<GridPosition> getListOfSpecialPositions() {
        ArrayList<GridPosition> positions = new ArrayList<>();

        positions.add(this.positionOne);
        positions.add(this.positionTwo);

        return positions;
    }
}
