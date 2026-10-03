package uk.ac.mmu.game.infrastructure.implmappers;

import uk.ac.mmu.game.domain.pieces.positiontrackers.LowerLeftOrigin;
import uk.ac.mmu.game.domain.pieces.positiontrackers.LowerRightOrigin;
import uk.ac.mmu.game.domain.pieces.positiontrackers.PositionTrackingConverter;
import uk.ac.mmu.game.domain.pieces.positiontrackers.UpperLeftOrigin;
import uk.ac.mmu.game.domain.pieces.positiontrackers.UpperRightOrigin;
import uk.ac.mmu.game.domain.util.ImplFactoryException;

public class PositionTrackingConverterMapper {
    public static final String LOWER_LEFT_ORIGIN = "LowerLeftOrigin";
    public static final String LOWER_RIGHT_ORIGIN = "LowerRightOrigin";
    public static final String UPPER_LEFT_ORIGIN = "UpperLeftOrigin";
    public static final String UPPER_RIGHT_ORIGIN = "UpperRightOrigin";

    public static PositionTrackingConverter getImplementationFromString(String string) throws ImplFactoryException {
        return switch (string) {
            case LOWER_LEFT_ORIGIN -> new LowerLeftOrigin();
            case LOWER_RIGHT_ORIGIN -> new LowerRightOrigin();
            case UPPER_LEFT_ORIGIN -> new UpperLeftOrigin();
            case UPPER_RIGHT_ORIGIN -> new UpperRightOrigin();
            default ->
                throw new ImplFactoryException("Invalid option when building PositionTrackingConverter: " + string);
        };
    }

    public static String getStringFromImplementation(PositionTrackingConverter impl) {
        return switch (impl) {
            case LowerLeftOrigin _unused -> LOWER_LEFT_ORIGIN;
            case LowerRightOrigin _unused -> LOWER_RIGHT_ORIGIN;
            case UpperLeftOrigin _unused -> UPPER_LEFT_ORIGIN;
            case UpperRightOrigin _unused -> UPPER_RIGHT_ORIGIN;
        };
    }
}
