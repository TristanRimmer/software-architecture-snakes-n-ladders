package uk.ac.mmu.game.shared;

public class MovementUtil {
    public final record TraversalStatus (boolean wrapped, int newPos) {}

    public static TraversalStatus traverseHorizontal(int currPos, int dimMax, int increment) {
        // TODO: enforce this better
        int sum = currPos + Math.abs(increment); 

        boolean wrappedAround = sum >= dimMax;

        // System.out.println("Current pos is " + currPos + ", increment is " + increment + ", sum is " + sum + ", wrapped around is " + wrappedAround + ", if its true then returned value is " + (sum - (dimMax - 1)));

        // If its less than zero it needs to be the number back it should go (TODO: Make negative increments a misuse)
        // On a wrap around case, its 'sum - (dimMax - 1)' because of indexing rules
        return wrappedAround ? new TraversalStatus(wrappedAround, sum - (dimMax - 1)) : new TraversalStatus(wrappedAround, sum);
    }
}
