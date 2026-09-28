package uk.ac.mmu.game.domain.board;

import java.util.ArrayList;

import uk.ac.mmu.game.domain.shared.GridPosition;

// TODO: for saving and recreating, it might be good to implement a Serialise/Deserialise like serde
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
    public ArrayList<GridPosition> getListOfSpecialPositions() {
        ArrayList<GridPosition> positions = new ArrayList<>();

        positions.add(this.positionOne);
        positions.add(this.positionTwo);

        return positions;
    }
}
