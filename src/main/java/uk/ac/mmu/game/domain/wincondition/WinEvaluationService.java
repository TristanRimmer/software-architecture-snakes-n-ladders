package uk.ac.mmu.game.domain.wincondition;

import uk.ac.mmu.game.domain.board.BoardDimensions;
import uk.ac.mmu.game.domain.pieces.PieceService;

public interface WinEvaluationService {
    // TODO: this should return a record that contains the status and any message it wishes to share
    WinEvaluationStatus evaluateWinStatus(PieceService piece, BoardDimensions boardProperties);
}
