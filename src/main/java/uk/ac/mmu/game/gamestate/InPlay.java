package uk.ac.mmu.game.gamestate;

import java.util.List;

import uk.ac.mmu.game.board.BoardService;
import uk.ac.mmu.game.diceroller.DiceRollingService;
import uk.ac.mmu.game.hitcondition.PieceCollisionService;
import uk.ac.mmu.game.observer.GameEventPublisher;
import uk.ac.mmu.game.observer.events.GameStateTransition;
import uk.ac.mmu.game.observer.events.PieceWon;
import uk.ac.mmu.game.observer.events.TurnChange;
import uk.ac.mmu.game.pieces.PieceService;
import uk.ac.mmu.game.wincondition.WinEvaluationService;

public final class InPlay implements GameState {
    @Override
    public void execute(Game context) {
        // Local references to make the main loop neater
        final List<PieceService> pieces = context.getPieces();
        final DiceRollingService diceRoller = context.getConfig().diceRoller();
        final PieceCollisionService collisionHandler = context.getConfig().pieceCollisionService();
        final WinEvaluationService winEvaluator = context.getConfig().winEvaluator();
        final BoardService board = context.getConfig().board();
        final GameTurn turnSequence = context.getConfig().turnSequence();

        final GameEventPublisher publisher = context.getEventPublisher();
        
        publisher.publish(new GameStateTransition("In Play"));

        int numTurns = 0;
        
        boolean isGameWon;
        PieceService currentPiece;
        int currentPieceNumber;

		do {
            /*
                Determine whose piece's turn it is
             */
			currentPieceNumber = this.getNextPieceIndex(numTurns, pieces.size());
			currentPiece = pieces.get(currentPieceNumber);
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
