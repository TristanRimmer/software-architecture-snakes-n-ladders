package uk.ac.mmu.game.domain.wincondition;

import uk.ac.mmu.game.domain.board.BoardDimensions;
import uk.ac.mmu.game.domain.pieces.Piece;

public interface WinEvaluationService {
    // TODO: this should return a record that contains the status and any message it wishes to share
    WinEvaluationStatus evaluateWinStatus(Piece piece, BoardDimensions boardProperties);
}
