package uk.ac.mmu.game.domain.wincondition;

import uk.ac.mmu.game.domain.board.BoardDimensions;
import uk.ac.mmu.game.domain.pieces.Piece;

public class CrossTheFinishline implements WinEvaluationService {
    @Override
    public WinEvaluationStatus evaluateWinStatus(Piece piece, BoardDimensions boardProperties) {
        int currentDisplacement = piece.getDisplacement();
        int offset = currentDisplacement - boardProperties.getMinimumTravelDistance();  
        
        return offset >= 0 ? WinEvaluationStatus.WON : WinEvaluationStatus.CONTINUE;
    }
}
