package uk.ac.mmu.game.domain.wincondition;

import uk.ac.mmu.game.domain.board.Board;
import uk.ac.mmu.game.domain.pieces.Piece;

public interface WinEvaluationService {
    WinEvaluationStatus evaluateWinStatus(Piece piece, Board boardProperties);
}
