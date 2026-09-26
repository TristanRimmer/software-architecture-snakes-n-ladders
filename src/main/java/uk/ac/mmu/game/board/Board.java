package uk.ac.mmu.game.board;

import java.util.ArrayList;
import java.util.HashMap;

import uk.ac.mmu.game.shared.GridPosition;

public class Board implements BoardService {
    int width;
    int height;
    ArrayList<SpecialPositionService> specialPositionsList;
    // Note: this works because GridPosition implements a proper equals()
    HashMap<GridPosition, Integer> specialPositionsMap = new HashMap<>();

    public Board(int width, int height, ArrayList<SpecialPositionService> specialPositions) {
        this.width = width;
        this.height = height;
        this.specialPositionsList = specialPositions;

        /*
            This implementation of Board uses a hashmap of indexes for more performant accessing of special positions
        */
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // -1 means it isnt special
                specialPositionsMap.put(new GridPosition(x, y), -1);
            }
        }

        int currentIndex = 0;

        for (SpecialPositionService specialPos : this.specialPositionsList) {
            ArrayList<GridPosition> relevantPositions = specialPos.getListOfSpecialPositions();

            for (GridPosition pos : relevantPositions) {
                this.specialPositionsMap.put(pos, currentIndex);
            }

            currentIndex++;
        }
    }

    @Override
    public int getBoardWidth() {
        return this.width;
    }

    @Override
    public int getBoardHeight() {
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
}
