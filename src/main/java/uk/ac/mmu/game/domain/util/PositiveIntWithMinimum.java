package uk.ac.mmu.game.domain.util;

public class PositiveIntWithMinimum {
    class PositiveIntWithMinimumInvalidRuntimeException extends RuntimeException {
        public PositiveIntWithMinimumInvalidRuntimeException() {
            super("Created a PositiveIntWithMinimum class and one of the inputs were invalid");
        }
    }

    private int minimum;
    private int value;

    public PositiveIntWithMinimum(int minimum, int value) {
        if (minimum <= 0 || value <= 0 || value < minimum)
            throw new PositiveIntWithMinimumInvalidRuntimeException();

        this.minimum = minimum;
        this.value = value;
    }

    public int getMinimum() {
        return this.minimum;
    };

    public int getValue() {
        return this.value;
    }
}
