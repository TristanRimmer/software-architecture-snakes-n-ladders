package uk.ac.mmu.game.hitcondition;

import java.util.ArrayList;

import uk.ac.mmu.game.pieces.PieceMoveset;
import uk.ac.mmu.game.shared.GridPosition;

public class HitsDoNothing implements PieceCollisionService {

    @Override
    public GridPosition canPieceOccupyNewSpace(ArrayList<GridPosition> allCurrentPositions, GridPosition old,
            GridPosition proposed) {
        return proposed;
    }

    @Override
    public CollisionStatus evaluateCollisions(PieceMoveset piece, GridPosition oldPos, GridPosition proposedPos,
            ArrayList<GridPosition> allCurrentPositions) {
        piece.setPosition(proposedPos);

        return CollisionStatus.MOVEHAPPENED;
    }
}
