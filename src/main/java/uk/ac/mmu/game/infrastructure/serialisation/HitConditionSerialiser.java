package uk.ac.mmu.game.infrastructure.serialisation;

import uk.ac.mmu.game.domain.rules.hitcondition.CollisionCondition;
import uk.ac.mmu.game.domain.rules.hitcondition.HitsDoNothing;
import uk.ac.mmu.game.domain.rules.hitcondition.HitsForfeitTurn;
import uk.ac.mmu.game.domain.util.ImplFactoryException;

public final class HitConditionSerialiser {
    public static final String HITS_DO_NOTHING = "HitsDoNothing";
    public static final String HITS_FORFEIT_TURN = "HitsForfeitTurn";

    public static CollisionCondition getImplementationFromString(String string) throws ImplFactoryException {
        switch (string) {
            case HITS_DO_NOTHING:
                return new HitsDoNothing();
            case HITS_FORFEIT_TURN:
                return new HitsForfeitTurn();
            default:
                throw new ImplFactoryException(
                        "The provided string does not map to any existing implementation");
        }
    }

    public static String getStringFromImplementation(CollisionCondition impl) {
        return switch (impl) {
            case HitsDoNothing _unused -> HITS_DO_NOTHING;
            case HitsForfeitTurn _unused -> HITS_FORFEIT_TURN;
        };
    }
}
