package uk.ac.mmu.game.domain.rules.wincondition;

import uk.ac.mmu.game.domain.board.Board;
import uk.ac.mmu.game.domain.pieces.Piece;
import uk.ac.mmu.game.domain.util.ImplFactoryException;

public sealed interface WinCondition permits CrossTheFinishline, ExactHit {
    WinEvaluationStatus evaluateWinStatus(Piece piece, Board boardProperties);

    static WinCondition getImplementationFromString(String string) throws ImplFactoryException {
        switch (string.toLowerCase()) {
            case "crossthefinishline":
                return new CrossTheFinishline();
            case "exacthit":
                return new ExactHit();
            default:
                throw new ImplFactoryException(
                        "The provided string does not map to any existing implementation");
        }
    }

    static String getStringFromImplementation(WinCondition impl) {
        // Sealed interface means that there is no way to implement WinCondition without
        // modifying this file, so can safely do if-else
        if (impl instanceof CrossTheFinishline) {
            return "CrossTheFinishLine";
        } else {
            // impl instanceof ExactHit)
            return "ExactHit";
        }
    }
}
