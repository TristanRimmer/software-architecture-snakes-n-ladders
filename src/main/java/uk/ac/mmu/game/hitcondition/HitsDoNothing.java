package uk.ac.mmu.game.hitcondition;

import java.util.ArrayList;

import uk.ac.mmu.game.shared.GridPosition;

public class HitsDoNothing implements PieceCollisionService {

    @Override
    public GridPosition canPieceOccupyNewSpace(ArrayList<GridPosition> allCurrentPositions, GridPosition old,
            GridPosition proposed) {
        return proposed;
    }
}
