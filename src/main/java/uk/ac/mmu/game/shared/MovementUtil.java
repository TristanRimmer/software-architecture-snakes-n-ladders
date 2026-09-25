package uk.ac.mmu.game.shared;

public class MovementUtil {
    public final record TraversalStatus (boolean wrapped, int placesLeftoverToMove) {}

    public static TraversalStatus traverseAlongLine(int currentPoint, int maxDim, int increment) {
        int sum = currentPoint + Math.abs(increment);

        boolean wrappedAround = sum >= maxDim;

        return wrappedAround ? new TraversalStatus(wrappedAround, sum - maxDim) : new TraversalStatus(
            wrappedAround, sum);
    }
}
