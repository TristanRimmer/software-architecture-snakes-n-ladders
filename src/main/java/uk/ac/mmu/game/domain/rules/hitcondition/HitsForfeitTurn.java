package uk.ac.mmu.game.domain.rules.hitcondition;

import java.util.ArrayList;

import uk.ac.mmu.game.domain.pieces.Piece;
import uk.ac.mmu.game.domain.util.GridPosition;

public class HitsForfeitTurn implements CollisionCondition {
    @Override
    public CollisionStatus evaluateCollisions(Piece piece, GridPosition oldPos, GridPosition proposedPos,
            ArrayList<GridPosition> allCurrentPositions) {
        for (GridPosition p : allCurrentPositions) {
            if (p.equals(proposedPos)) {
                piece.setPosition(oldPos);
                return CollisionStatus.MOVEDIDNTHAPPEN;
            }
        }
        
        piece.setPosition(proposedPos);

        return CollisionStatus.MOVEHAPPENED;
    }
}
