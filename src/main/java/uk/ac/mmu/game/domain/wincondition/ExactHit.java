package uk.ac.mmu.game.domain.wincondition;

import uk.ac.mmu.game.domain.board.BoardDimensions;
import uk.ac.mmu.game.domain.pieces.Piece;

public class ExactHit implements WinEvaluationService {
    @Override
    public WinEvaluationStatus evaluateWinStatus(Piece piece, BoardDimensions boardProperties) {
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
