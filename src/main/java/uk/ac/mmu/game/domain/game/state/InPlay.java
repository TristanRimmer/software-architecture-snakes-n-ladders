package uk.ac.mmu.game.domain.game.state;

import uk.ac.mmu.game.domain.board.Board;
import uk.ac.mmu.game.domain.dice.DiceRolling;
import uk.ac.mmu.game.domain.events.GameEventPublisher;
import uk.ac.mmu.game.domain.events.types.GameStateTransition;
import uk.ac.mmu.game.domain.events.types.PieceWon;
import uk.ac.mmu.game.domain.events.types.TurnChange;
import uk.ac.mmu.game.domain.game.Game;
import uk.ac.mmu.game.domain.game.state.gameturn.GameTurn;
import uk.ac.mmu.game.domain.pieces.Piece;
import uk.ac.mmu.game.domain.pieces.container.PieceContainer;
import uk.ac.mmu.game.domain.rules.hitcondition.CollisionCondition;
import uk.ac.mmu.game.domain.rules.wincondition.WinCondition;

public final class InPlay implements GameState {
    @Override
    public void execute(Game context) {
        // Local references to make the main loop neater
        final PieceContainer pieces = context.getPieces();
        final DiceRolling diceRoller = context.getConfig().diceRoller();
        final CollisionCondition collisionHandler = context.getConfig().pieceCollisionService();
        final WinCondition winEvaluator = context.getConfig().winEvaluator();
        final Board board = context.getConfig().board();
        final GameTurn turnSequence = context.getConfig().turnSequence();

        final GameEventPublisher publisher = context.getEventPublisher();
        
        publisher.publish(new GameStateTransition("In Play"));

        int numTurns = 0;
        
        boolean isGameWon;
        Piece currentPiece;
        int currentPieceNumber;

		do {
            /*
                Determine whose turn it is
             */
			currentPieceNumber = this.getNextPieceIndex(numTurns, pieces.getNumPieces());
            currentPiece = pieces.getNextPiece();
            numTurns++;

            publisher.publish(new TurnChange(currentPieceNumber));

            /*
                Run this piece's turn
            */
            isGameWon = turnSequence.didNextTurnWinGame(
                currentPiece, 
                pieces, 
                diceRoller, 
                collisionHandler, 
                winEvaluator, 
                board, 
                publisher
            );
		} while(!isGameWon);

        publisher.publish(new PieceWon(currentPiece, currentPieceNumber));
    }

    @Override
    public GameState progessState() {
        return new GameOver();
    }

    @Override
    public boolean isEndOfChain() {
        return false;
    }

    private int getNextPieceIndex(int numTurns, int numPieces) {
        return numTurns % numPieces;
    }
}
