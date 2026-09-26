package uk.ac.mmu.game.hitcondition;

import java.util.ArrayList;

import uk.ac.mmu.game.shared.GridPosition;

public class HitsForfeitTurn implements PieceCollisionService {

    @Override
    public GridPosition canPieceOccupyNewSpace(ArrayList<GridPosition> allCurrentPositions, GridPosition old,
            GridPosition proposed) {
        for (GridPosition p : allCurrentPositions) {
            if (p.equals(proposed))
                return old;
        }
        return proposed;
    }

}
