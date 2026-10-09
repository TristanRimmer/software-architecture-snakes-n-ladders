package uk.ac.mmu.game.domain.util;

public class PositiveIntegerAtleastFive implements PositiveIntegerWithMinimum {
    class PositiveIntWithMinimumInvalidRuntimeException extends RuntimeException {
        public PositiveIntWithMinimumInvalidRuntimeException() {
            super("Created a PositiveIntWithMinimum class and one of the inputs were invalid");
        }
    }
    private int value;

    public PositiveIntegerAtleastFive(int value) {
        if (value < 5)
            throw new PositiveIntWithMinimumInvalidRuntimeException();

        this.value = value;
    }

    public int getMinimum() {
        return 5;
    };

    public int getValue() {
        return this.value;
    }
}
