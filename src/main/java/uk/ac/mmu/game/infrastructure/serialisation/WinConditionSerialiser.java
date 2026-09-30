package uk.ac.mmu.game.infrastructure.serialisation;

import uk.ac.mmu.game.domain.rules.wincondition.CrossTheFinishline;
import uk.ac.mmu.game.domain.rules.wincondition.ExactHit;
import uk.ac.mmu.game.domain.rules.wincondition.WinCondition;
import uk.ac.mmu.game.domain.util.ImplFactoryException;

public final class WinConditionSerialiser {
    public static final String CROSS_THE_FINISH_LINE = "CrossTheFinishLine";
    public static final String EXACT_HIT = "ExactHit";

    public static WinCondition getImplementationFromString(String string) throws ImplFactoryException {
        switch (string) {
            case CROSS_THE_FINISH_LINE:
                return new CrossTheFinishline();
            case EXACT_HIT:
                return new ExactHit();
            default:
                throw new ImplFactoryException(
                        "The provided string does not map to any existing implementation");
        }
    }

    public static String getStringFromImplementation(WinCondition impl) {
        return switch (impl) {
            case CrossTheFinishline _unused -> CROSS_THE_FINISH_LINE;
            case ExactHit _unused -> EXACT_HIT;
        };
    }
}
