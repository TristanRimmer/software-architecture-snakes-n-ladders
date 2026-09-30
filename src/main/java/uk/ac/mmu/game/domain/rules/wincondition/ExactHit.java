package uk.ac.mmu.game.domain.rules.wincondition;

import uk.ac.mmu.game.domain.board.Board;
import uk.ac.mmu.game.domain.pieces.Piece;

public final class ExactHit implements WinCondition {
    @Override
    public WinEvaluationStatus evaluateWinStatus(Piece piece, Board boardProperties) {
        int currentDisplacement = piece.getDisplacement();
        int offset = currentDisplacement - boardProperties.getMinimumTravelDistance();

        if (offset == 0) {
            return WinEvaluationStatus.WON;
        } else if (offset > 0) {
            // Need to bounce it back
            piece.move(-2 * offset);

            return WinEvaluationStatus.CLOSECALL;
        }

        return WinEvaluationStatus.CONTINUE;
    }
}
