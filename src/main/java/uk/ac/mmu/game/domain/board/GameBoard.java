package uk.ac.mmu.game.domain.board;

import uk.ac.mmu.game.domain.util.PositiveIntegerAtleastFive;

public class GameBoard implements Board {
    private final PositiveIntegerAtleastFive width;
    private final PositiveIntegerAtleastFive height;

    public GameBoard(int dimensions) {
        PositiveIntegerAtleastFive validatedDimension = new PositiveIntegerAtleastFive(dimensions);
        this.width = validatedDimension;
        this.height = validatedDimension;
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
    public int getMinimumTravelDistance() {
        return this.width.getValue() * this.height.getValue() - 1;
    }
}
