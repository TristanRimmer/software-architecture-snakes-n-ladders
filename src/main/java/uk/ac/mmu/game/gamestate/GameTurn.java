package uk.ac.mmu.game.gamestate;

import java.util.List;

import uk.ac.mmu.game.board.BoardService;
import uk.ac.mmu.game.diceroller.DiceRollingService;
import uk.ac.mmu.game.hitcondition.PieceCollisionService;
import uk.ac.mmu.game.observer.GameEventPublisher;
import uk.ac.mmu.game.pieces.PieceService;
import uk.ac.mmu.game.wincondition.WinEvaluationService;

public interface GameTurn {
    boolean didNextTurnWinGame(PieceService currentPiece,
        List<PieceService> allPieces,
        DiceRollingService diceRoller,
        PieceCollisionService collisionHandler,
        WinEvaluationService winEvaluator,
        BoardService board,
        GameEventPublisher publisher
    );
}
