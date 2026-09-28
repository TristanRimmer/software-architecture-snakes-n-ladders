package uk.ac.mmu.game.domain.game.state.gameturn;

import java.util.ArrayList;
import java.util.List;

import uk.ac.mmu.game.domain.board.Board;
import uk.ac.mmu.game.domain.dice.DiceRolling;
import uk.ac.mmu.game.domain.events.GameEventPublisher;
import uk.ac.mmu.game.domain.events.types.DiceRolled;
import uk.ac.mmu.game.domain.events.types.HitConditionsPreventedMove;
import uk.ac.mmu.game.domain.events.types.PieceMove;
import uk.ac.mmu.game.domain.events.types.SpecialPositionMovedPiece;
import uk.ac.mmu.game.domain.events.types.WinConditionPreventedWin;
import uk.ac.mmu.game.domain.pieces.Piece;
import uk.ac.mmu.game.domain.rules.hitcondition.CollisionCondition;
import uk.ac.mmu.game.domain.rules.hitcondition.CollisionStatus;
import uk.ac.mmu.game.domain.rules.wincondition.WinCondition;
import uk.ac.mmu.game.domain.rules.wincondition.WinEvaluationStatus;
import uk.ac.mmu.game.domain.util.GridPosition;

/*
    This class isn't strictly necessary as its quite a close relationship to a directly concrete implementation,
    however for the purposes of making the InPlay state a bit more simplistic its worth it

    It also opens the doors for interesting opportunities, like stacking special pieces (a chain of them), or multiple
    dice rolls per person, etc. All of that is nonsensical, of course, but its available now without changing another class!
*/
public class ComprehensiveGameTurn implements GameTurn {

    @Override
    public boolean didNextTurnWinGame(Piece currentPiece, List<Piece> allPieces,
            DiceRolling diceRoller, CollisionCondition collisionHandler, WinCondition winEvaluator,
            Board board, GameEventPublisher publisher) {
        /*
            Store some initial position data about this and other pieces
        */
        GridPosition initialPosition = currentPiece.getCurrentPosition();
        ArrayList<GridPosition> allPositions = new ArrayList<>();

        for (Piece p : allPieces) {
            if (p != currentPiece)
                allPositions.add(p.getCurrentPosition());
        }
        
        /*
            Roll the dice and move the piece
        */
        int newDiceRoll = diceRoller.nextDiceRoll();
        currentPiece.move(newDiceRoll);

        publisher.publish(new DiceRolled(newDiceRoll));

        /*
            Has the piece just won?
        */
        WinEvaluationStatus winEvaluationStatus = winEvaluator.evaluateWinStatus(currentPiece, board);
        if (winEvaluationStatus == WinEvaluationStatus.WON) {
            return true;
        } else if (winEvaluationStatus == WinEvaluationStatus.CLOSECALL) {
            publisher.publish(new WinConditionPreventedWin());
        } // Otherwise, its just a continue
        
        /*
            Does the piece need to move based on hit rules?    
        */
        CollisionStatus collisionStatus = collisionHandler.evaluateCollisions(currentPiece, initialPosition, currentPiece.getCurrentPosition(), allPositions);
        if (collisionStatus == CollisionStatus.MOVEDIDNTHAPPEN) {
            publisher.publish(new HitConditionsPreventedMove());
        } // Otherwise the piece did move

        /*
            Did the piece land on a special spot?
        */
        if (board.pieceHasLandedOnSpecialSpot(currentPiece.getCurrentPosition())) {
            publisher.publish(new SpecialPositionMovedPiece());
            
            currentPiece.setPosition(
                board.getSpecialPositionBehaviour(currentPiece.getCurrentPosition())
            );
            /*
                Does THIS need to be voided based on hit rules?
            */
            CollisionStatus secondCollisionStatus = collisionHandler.evaluateCollisions(currentPiece, initialPosition, currentPiece.getCurrentPosition(), allPositions);
            if (secondCollisionStatus == CollisionStatus.MOVEDIDNTHAPPEN) {
                publisher.publish(new HitConditionsPreventedMove());
            } // Otherwise the piece did move
        }
        publisher.publish(new PieceMove(initialPosition, currentPiece.getCurrentPosition()));
        
        return false;
    }
}
