package uk.ac.mmu.game.domain.pieces.positiontrackers;

import uk.ac.mmu.game.domain.util.GridPosition;
import uk.ac.mmu.game.domain.util.ImplFactoryException;

public sealed interface PositionTrackingConverter
        permits LowerLeftOrigin, LowerRightOrigin, UpperLeftOrigin, UpperRightOrigin {
    GridPosition displacementToGridPosition(int displacement, int gridWidth, int gridHeight);

    int gridPositionToDisplacement(GridPosition position, int gridWidth, int gridHeight);

    static PositionTrackingConverter getImplementationFromString(String string) throws ImplFactoryException {
        switch (string.toLowerCase()) {
            case "lowerleft":
                return new LowerLeftOrigin();
            case "lowerright":
                return new LowerRightOrigin();
            case "upperleft":
                return new UpperLeftOrigin();
            case "upperright":
                return new UpperRightOrigin();
            default:
                throw new ImplFactoryException(
                        "The provided string does not map to any existing implementation");
        }
    }

    static String getStringFromImplementation(PositionTrackingConverter impl) {
        if (impl instanceof LowerLeftOrigin) {
            return "LowerLeft";
        } else if (impl instanceof LowerRightOrigin) {
            return "LowerRight";
        } else if (impl instanceof UpperLeftOrigin) {
            return "UpperLeft";
        } else {
            return "UpperRight";
        }
    }
}
