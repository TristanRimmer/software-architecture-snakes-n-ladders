package uk.ac.mmu.game.domain.game.state.gameturn;

import uk.ac.mmu.game.domain.board.Board;
import uk.ac.mmu.game.domain.dice.DiceRolling;
import uk.ac.mmu.game.domain.events.GameEventPublisher;
import uk.ac.mmu.game.domain.pieces.Piece;
import uk.ac.mmu.game.domain.pieces.container.PieceContainer;
import uk.ac.mmu.game.domain.rules.hitcondition.CollisionCondition;
import uk.ac.mmu.game.domain.rules.wincondition.WinCondition;

public interface GameTurn {
    boolean didNextTurnWinGame(Piece currentPiece,
        PieceContainer allPieces,
        DiceRolling diceRoller,
        CollisionCondition collisionHandler,
        WinCondition winEvaluator,
        Board board,
        GameEventPublisher publisher
    );
}
