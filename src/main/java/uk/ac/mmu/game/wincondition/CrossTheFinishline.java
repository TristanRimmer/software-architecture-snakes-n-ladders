package uk.ac.mmu.game.wincondition;

import uk.ac.mmu.game.board.BoardDimensions;
import uk.ac.mmu.game.pieces.PieceService;

public class CrossTheFinishline implements WinEvaluationService {
    @Override
    public boolean hasPieceWon(PieceService piece, BoardDimensions boardProperties) {
        int currentDisplacement = piece.getDisplacementAsScalar();
        int offset = currentDisplacement - boardProperties.getMinimumTravelDistance();

        return offset >= 0;
    }
}
