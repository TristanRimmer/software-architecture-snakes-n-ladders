package uk.ac.mmu.game.domain.util;

/**
 * Value Object to replace a primitive int for board dimensions
 */
public class BoardDimension {
    private final static int MINIMUM_SIZE= 5;

    public static BoardDimension create(int size) throws BoardDimensionInvalidException {
        if (size < BoardDimension.MINIMUM_SIZE) {
            throw new BoardDimensionInvalidException(); 
        }
        return new BoardDimension(MINIMUM_SIZE);
    }

    private final int value;

    private BoardDimension(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }
}
