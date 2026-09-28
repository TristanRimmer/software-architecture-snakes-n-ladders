package uk.ac.mmu.game.gamestate;

import java.util.ArrayList;

import uk.ac.mmu.game.board.BoardService;
import uk.ac.mmu.game.diceroller.DiceRollingService;
import uk.ac.mmu.game.hitcondition.CollisionStatus;
import uk.ac.mmu.game.hitcondition.PieceCollisionService;
import uk.ac.mmu.game.observer.GameEventPublisher;
import uk.ac.mmu.game.observer.events.ArbitraryMessage;
import uk.ac.mmu.game.observer.events.DiceRolled;
import uk.ac.mmu.game.observer.events.GameStateTransition;
import uk.ac.mmu.game.observer.events.PieceMove;
import uk.ac.mmu.game.observer.events.PieceWon;
import uk.ac.mmu.game.observer.events.TurnChange;
import uk.ac.mmu.game.pieces.PieceService;
import uk.ac.mmu.game.shared.GridPosition;
import uk.ac.mmu.game.wincondition.WinEvaluationService;
import uk.ac.mmu.game.wincondition.WinEvaluationStatus;

public final class InPlay implements GameState {

    @Override
    public void execute(Game context) {
        // Some local references
        ArrayList<PieceService> pieces = context.getPieces();
        DiceRollingService diceRoller = context.getDiceRoller();
        PieceCollisionService collisionHandler = context.getCollisionService();
        WinEvaluationService winEvaluator = context.getWinEvaluator();
        BoardService board = context.getBoard();
        GameEventPublisher publisher = context.getEventPublisher();
        
        publisher.publish(new GameStateTransition("In Play"));

        int numTurns = 0;

		while (true) {
            /*
                Determine whose piece's turn it is
             */
			int pieceNum = numTurns % pieces.size();
			PieceService piece = pieces.get(pieceNum);
            numTurns++;

            publisher.publish(new TurnChange(pieceNum));

            /*
                Store some initial position data about this and other pieces
            */
			GridPosition initialPosition = piece.getCurrentPosition();
			ArrayList<GridPosition> allPositions = new ArrayList<>();

			for (PieceService p : pieces) {
				if (p != piece)
					allPositions.add(p.getCurrentPosition());
			}
            
            /*
                Roll the dice and move the piece
            */
			int newDiceRoll = diceRoller.nextDiceRoll();
            piece.move(newDiceRoll);

            publisher.publish(new DiceRolled(newDiceRoll));

            /*
                Has the piece just won?
            */
            WinEvaluationStatus winEvaluationStatus = winEvaluator.evaluateWinStatus(piece, board);
			if (winEvaluationStatus.equals(WinEvaluationStatus.WON)) {
                publisher.publish(new PieceWon(piece, pieceNum));
				break;
            } else if (winEvaluationStatus.equals(WinEvaluationStatus.CLOSECALL)) {
                publisher.publish(new ArbitraryMessage("Piece nearly won, but the win rules prevented it."));
            } // Otherwise, its just a continue
			
            /*
                Does the piece need to move based on hit rules?    
            */
            CollisionStatus collisionStatus = collisionHandler.evaluateCollisions(piece, initialPosition, piece.getCurrentPosition(), allPositions);
            if (collisionStatus.equals(CollisionStatus.MOVEDIDNTHAPPEN)) {
                publisher.publish(new ArbitraryMessage("The piece's move did not happen because of the hit rules!"));
            } // Otherwise the piece did move

            /*
                Did the piece land on a special spot?
            */
			if (board.pieceHasLandedOnSpecialSpot(piece.getCurrentPosition())) {
                publisher.publish(new ArbitraryMessage("The piece landed on a special spot!"));
                
				piece.setPosition(
                    board.getSpecialPositionBehaviour(piece.getCurrentPosition())
                );
                /*
                    Does THIS need to be voided based on hit rules?
                */
                CollisionStatus secondCollisionStatus = collisionHandler.evaluateCollisions(piece, initialPosition, piece.getCurrentPosition(), allPositions);
                if (secondCollisionStatus.equals(CollisionStatus.MOVEDIDNTHAPPEN)) {
                    publisher.publish(
                        new ArbitraryMessage(
                            "The piece's move did not happen, as a result of the special spot's behaviour and the chosen hit rules!"));
                } // Otherwise the piece did move
            }
            publisher.publish(new PieceMove(initialPosition, piece.getCurrentPosition()));
		}
    }

    @Override
    public GameState progessState() {
        return new GameOver();
    }
}
