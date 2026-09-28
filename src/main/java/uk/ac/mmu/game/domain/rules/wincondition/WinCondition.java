package uk.ac.mmu.game.domain.rules.wincondition;

import uk.ac.mmu.game.domain.board.Board;
import uk.ac.mmu.game.domain.pieces.Piece;

public interface WinCondition {
    WinEvaluationStatus evaluateWinStatus(Piece piece, Board boardProperties);
}
