package uk.ac.mmu.game.domain.hitcondition;

import java.util.ArrayList;

import uk.ac.mmu.game.domain.pieces.Piece;
import uk.ac.mmu.game.domain.shared.GridPosition;

public class HitsDoNothing implements PieceCollisionService {
    @Override
    public CollisionStatus evaluateCollisions(Piece piece, GridPosition oldPos, GridPosition proposedPos,
            ArrayList<GridPosition> allCurrentPositions) {
        piece.setPosition(proposedPos);

        return CollisionStatus.MOVEHAPPENED;
    }
}
