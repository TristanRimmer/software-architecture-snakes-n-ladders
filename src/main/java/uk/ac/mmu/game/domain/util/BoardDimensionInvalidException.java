package uk.ac.mmu.game.domain.util;

public class BoardDimensionInvalidException extends Exception {
    public BoardDimensionInvalidException() {
        super("Tried to create a BoardDimension Value Object with an invalid size");
    }
}
