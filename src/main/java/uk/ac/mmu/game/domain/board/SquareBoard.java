package uk.ac.mmu.game.domain.board;

import uk.ac.mmu.game.domain.util.BoardDimension;

public class SquareBoard implements Board {
    private final BoardDimension widthAndHeight;

    public SquareBoard(BoardDimension dimensions) {
        System.out.println("BOARD CREATED: " + dimensions.getValue());
        this.widthAndHeight = dimensions;
    }

    @Override
    public BoardDimension getBoardWidth() {
        return this.widthAndHeight;
    }

    @Override
    public BoardDimension getBoardHeight() {
        return this.widthAndHeight;
    }

    @Override
    public int getMinimumTravelDistance() {
        return this.widthAndHeight.getValue() * this.widthAndHeight.getValue() - 1;
    }
}
