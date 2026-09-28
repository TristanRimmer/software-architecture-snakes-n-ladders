package uk.ac.mmu.game.domain.hitcondition;

import java.util.ArrayList;

import uk.ac.mmu.game.domain.pieces.Piece;
import uk.ac.mmu.game.domain.util.GridPosition;

public interface PieceCollisionService {
    CollisionStatus evaluateCollisions(Piece piece, GridPosition oldPos, GridPosition proposedPos, ArrayList<GridPosition> allCurrentPositions);
}
