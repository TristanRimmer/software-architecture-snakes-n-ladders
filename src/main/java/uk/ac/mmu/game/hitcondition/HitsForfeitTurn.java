package uk.ac.mmu.game.hitcondition;

import java.util.ArrayList;

import uk.ac.mmu.game.pieces.PieceMoveset;
import uk.ac.mmu.game.shared.GridPosition;

public class HitsForfeitTurn implements PieceCollisionService {

    @Override
    public GridPosition canPieceOccupyNewSpace(ArrayList<GridPosition> allCurrentPositions, GridPosition old,
            GridPosition proposed) {
        for (GridPosition p : allCurrentPositions) {
            if (p.equals(proposed)) {
                System.out.println("Piece tried to occupy another piece at " + proposed + ", turn is forfeit!");
                return old;
            }
        }
        return proposed;
    }

    @Override
    public CollisionStatus evaluateCollisions(PieceMoveset piece, GridPosition oldPos, GridPosition proposedPos,
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
