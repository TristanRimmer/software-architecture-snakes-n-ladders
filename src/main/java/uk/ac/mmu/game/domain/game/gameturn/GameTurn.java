package uk.ac.mmu.game.domain.game.gameturn;

import uk.ac.mmu.game.domain.board.Board;
import uk.ac.mmu.game.domain.dice.DiceRolling;
import uk.ac.mmu.game.domain.events.GameEventPublisher;
import uk.ac.mmu.game.domain.pieces.Piece;
import uk.ac.mmu.game.domain.pieces.container.PieceContainer;
import uk.ac.mmu.game.domain.rules.hitcondition.CollisionCondition;
import uk.ac.mmu.game.domain.rules.wincondition.WinCondition;

public sealed interface GameTurn permits ComprehensiveGameTurn {
    boolean didNextTurnWinGame(Piece currentPiece,
            PieceContainer allPieces,
            DiceRolling diceRoller,
            CollisionCondition collisionHandler,
            WinCondition winEvaluator,
            Board board,
            GameEventPublisher publisher);
}
