package uk.ac.mmu.game.domain.gamestate;

import java.util.List;

import uk.ac.mmu.game.domain.board.Board;
import uk.ac.mmu.game.domain.dice.DiceRollingService;
import uk.ac.mmu.game.domain.events.GameEventPublisher;
import uk.ac.mmu.game.domain.hitcondition.PieceCollisionService;
import uk.ac.mmu.game.domain.pieces.Piece;
import uk.ac.mmu.game.domain.wincondition.WinEvaluationService;

public interface GameTurn {
    boolean didNextTurnWinGame(Piece currentPiece,
        List<Piece> allPieces,
        DiceRollingService diceRoller,
        PieceCollisionService collisionHandler,
        WinEvaluationService winEvaluator,
        Board board,
        GameEventPublisher publisher
    );
}
