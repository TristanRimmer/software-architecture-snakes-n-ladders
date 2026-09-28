package uk.ac.mmu.game.wincondition;

import uk.ac.mmu.game.board.BoardDimensions;
import uk.ac.mmu.game.pieces.PieceService;

public class ExactHit implements WinEvaluationService {
    @Override
    public WinEvaluationStatus evaluateWinStatus(PieceService piece, BoardDimensions boardProperties) {
        int currentDisplacement = piece.getDisplacementAsScalar();
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
