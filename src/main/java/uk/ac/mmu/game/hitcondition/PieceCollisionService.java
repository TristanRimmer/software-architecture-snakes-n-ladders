package uk.ac.mmu.game.hitcondition;

import java.util.ArrayList;

import uk.ac.mmu.game.shared.GridPosition;

public interface PieceCollisionService {
    GridPosition canPieceOccupyNewSpace(ArrayList<GridPosition> allCurrentPositions, GridPosition old, GridPosition proposed);
}
