package uk.ac.mmu.game.domain.rules.hitcondition;

import java.util.List;

import uk.ac.mmu.game.domain.pieces.Piece;
import uk.ac.mmu.game.domain.util.GridPosition;
import uk.ac.mmu.game.domain.util.ImplFactoryException;

public sealed interface CollisionCondition permits HitsDoNothing, HitsForfeitTurn {
    CollisionStatus evaluateCollisions(Piece piece, GridPosition oldPos, GridPosition proposedPos,
            List<GridPosition> allCurrentPositions);

    static CollisionCondition getImplementationFromString(String string) throws ImplFactoryException {
        switch (string.toLowerCase()) {
            case "hitsdonothing":
                return new HitsDoNothing();
            case "hitsforfeitturn":
                return new HitsForfeitTurn();
            default:
                throw new ImplFactoryException(
                        "The provided string does not map to any existing implementation");
        }
    }

    static String getStringFromImplementation(CollisionCondition impl) {
        if (impl instanceof HitsDoNothing) {
            return "HitsDoNothing";
        } else {
            return "HitsForfeitTurn";
        }
    }
}
