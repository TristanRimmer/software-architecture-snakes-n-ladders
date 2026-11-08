package uk.ac.mmu.game.domain.rules.wincondition;

import uk.ac.mmu.game.domain.board.Board;
import uk.ac.mmu.game.domain.pieces.Piece;

public final class CrossTheFinishline implements WinCondition {
    @Override
    public WinEvaluationStatus evaluateWinStatus(Piece piece, Board boardProperties) {
        int currentDisplacement = piece.getDisplacement();
        int offset = currentDisplacement - boardProperties.getMinimumTravelDistance();

        return offset >= 0 ? WinEvaluationStatus.WON : WinEvaluationStatus.CONTINUE;
    }
}
