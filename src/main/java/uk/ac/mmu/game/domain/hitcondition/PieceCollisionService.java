package uk.ac.mmu.game.domain.hitcondition;

import java.util.ArrayList;

import uk.ac.mmu.game.domain.pieces.PieceMoveset;
import uk.ac.mmu.game.domain.shared.GridPosition;

public interface PieceCollisionService {
    GridPosition canPieceOccupyNewSpace(ArrayList<GridPosition> allCurrentPositions, GridPosition old, GridPosition proposed);

    CollisionStatus evaluateCollisions(PieceMoveset piece, GridPosition oldPos, GridPosition proposedPos, ArrayList<GridPosition> allCurrentPositions);
}
