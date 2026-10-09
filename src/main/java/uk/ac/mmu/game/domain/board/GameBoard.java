package uk.ac.mmu.game.domain.board;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import uk.ac.mmu.game.domain.board.specialpositions.SpecialLinkedPositions;
import uk.ac.mmu.game.domain.util.GridPosition;
import uk.ac.mmu.game.domain.util.PositiveIntegerAtleastFive;

public class GameBoard implements Board {
    PositiveIntegerAtleastFive width;
    PositiveIntegerAtleastFive height;
    List<SpecialLinkedPositions> specialPositionsList;
    // Note: this works because GridPosition implements a proper equals()
    HashMap<GridPosition, Integer> specialPositionsMap = new HashMap<>();

    public GameBoard(int dimensions) {
        PositiveIntegerAtleastFive validatedDimension = new PositiveIntegerAtleastFive(dimensions);
        this.width = validatedDimension;
        this.height = validatedDimension;
        this.specialPositionsList = new ArrayList<>();
        /*
            This implementation of Board uses a hashmap of indexes to make the pieceHasLandedOnSpecialSpot more simplisitc
        */
        for (int y = 0; y < this.width.getValue(); y++) {
            for (int x = 0; x < this.height.getValue(); x++) {
                // -1 means it isnt special
                specialPositionsMap.put(new GridPosition(x, y), -1);
            }
        }
    }
    @Override
    public boolean registerNewSpecialPosition(SpecialLinkedPositions newPosition) {
        List<GridPosition> relevantPositions = newPosition.getEntryPoints();

        boolean anyPosOnCorner = relevantPositions
            .stream()
            .map(pos -> this.positionLandsOnWinSpot(pos))
            .reduce(false, (a,b) -> a || b);

        if (anyPosOnCorner)
            return false;

        this.specialPositionsList.add(newPosition);

        for (GridPosition pos : relevantPositions) {
            this.specialPositionsMap.put(pos, 
                specialPositionsList.lastIndexOf(specialPositionsList.getLast()));
        }

        return true;
    }

    @Override
    public PositiveIntegerAtleastFive getBoardWidth() {
        return this.width;
    }

    @Override
    public PositiveIntegerAtleastFive getBoardHeight() {
        return this.height;
    }

    @Override
    public boolean pieceHasLandedOnSpecialSpot(GridPosition position) {
        if (!this.specialPositionsMap.containsKey(position))
            return false;

        return this.specialPositionsMap.get(position) != -1;
    }

    @Override
    public GridPosition getSpecialPositionBehaviour(GridPosition position) {
        if (!this.pieceHasLandedOnSpecialSpot(position))
            return position;

        if (!this.specialPositionsMap.containsKey(position))
            return position;

        Integer indexInList = this.specialPositionsMap.get(position);

        // I suppose it doesnt hurt
        if (indexInList == -1 || indexInList < 0 || indexInList >= this.specialPositionsList.size())
            return position;

        return this.specialPositionsList.get(indexInList).getPositionAfterSpecialBehaviour(position);
    }

    @Override
    public int getMinimumTravelDistance() {
        return this.width.getValue() * this.height.getValue() - 1;
    }
    @Override
    public List<SpecialLinkedPositions> getSpecialPositions() {
       return this.specialPositionsList; 
    }

    private boolean positionLandsOnWinSpot(GridPosition pos) {
        GridPosition bottomLeft = new GridPosition(0, 0);
        GridPosition topLeft = new GridPosition(0, this.width.getValue() - 1);
        GridPosition bottomRight = new GridPosition(this.width.getValue() - 1, 0);
        GridPosition topRight = new GridPosition(this.width.getValue() - 1, this.height.getValue() - 1);

        return pos.equals(bottomLeft) || pos.equals(topLeft) || pos.equals(bottomRight) || pos.equals(topRight);
    }
}
