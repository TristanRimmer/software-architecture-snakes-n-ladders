package uk.ac.mmu.game.wincondition;

import uk.ac.mmu.game.board.BoardDimensions;
import uk.ac.mmu.game.pieces.PieceService;

public interface WinEvaluationService {
    // TODO: this should return a record that contains the status and any message it wishes to share
    WinEvaluationStatus evaluateWinStatus(PieceService piece, BoardDimensions boardProperties);
}
