package uk.ac.mmu.game.domain.wincondition;

import uk.ac.mmu.game.domain.board.BoardDimensions;
import uk.ac.mmu.game.domain.pieces.PieceService;

public class CrossTheFinishline implements WinEvaluationService {
    @Override
    public WinEvaluationStatus evaluateWinStatus(PieceService piece, BoardDimensions boardProperties) {
        int currentDisplacement = piece.getDisplacementAsScalar();
        int offset = currentDisplacement - boardProperties.getMinimumTravelDistance();  
        
        return offset >= 0 ? WinEvaluationStatus.WON : WinEvaluationStatus.CONTINUE;
    }
}
