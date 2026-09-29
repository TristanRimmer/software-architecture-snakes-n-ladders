package uk.ac.mmu.game.domain.rules.hitcondition;

import java.util.List;

import uk.ac.mmu.game.domain.pieces.Piece;
import uk.ac.mmu.game.domain.util.GridPosition;

public interface CollisionCondition {
    CollisionStatus evaluateCollisions(Piece piece, GridPosition oldPos, GridPosition proposedPos, List<GridPosition> allCurrentPositions);
}
