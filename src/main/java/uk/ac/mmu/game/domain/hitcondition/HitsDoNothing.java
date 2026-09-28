package uk.ac.mmu.game.domain.hitcondition;

import java.util.ArrayList;

import uk.ac.mmu.game.domain.pieces.PieceMoveset;
import uk.ac.mmu.game.domain.shared.GridPosition;

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
