package uk.ac.mmu.game.gamestate;

import java.util.ArrayList;

import uk.ac.mmu.game.board.BoardService;
import uk.ac.mmu.game.diceroller.DiceRollingService;
import uk.ac.mmu.game.hitcondition.PieceCollisionService;
import uk.ac.mmu.game.output.StylisedPrinter;
import uk.ac.mmu.game.output.TextOutputHandler;
import uk.ac.mmu.game.pieces.PieceService;
import uk.ac.mmu.game.shared.GridPosition;
import uk.ac.mmu.game.wincondition.WinEvaluationService;

public final class InPlay implements GameState {

    @Override
    public void execute(Game context) {
        // Some local references
        ArrayList<PieceService> pieces = context.getPieces();
        DiceRollingService diceRoller = context.getDiceRoller();
        TextOutputHandler output = context.getOutputHandler();
        PieceCollisionService collisionHandler = context.getCollisionService();
        WinEvaluationService winEvaluator = context.getWinEvaluator();
        BoardService board = context.getBoard();
        
        StylisedPrinter.printBanner(output, "In Play");

        int numTurns = 0;

		while (true) {
            /*
                Determine whose piece's turn it is
             */
			int pieceNum = numTurns % pieces.size();
			PieceService piece = pieces.get(pieceNum);
            numTurns++;

            StylisedPrinter.printSubheading(output, "Piece " + pieceNum + "'s Turn");

            /*
                Store some initial position data about this and other pieces
            */
			GridPosition initialPosition = piece.getCurrentPosition();
			ArrayList<GridPosition> allPositions = new ArrayList<>();

			for (PieceService p : pieces) {
				if (p != piece) // Memory Address Comparison
					allPositions.add(p.getCurrentPosition());
			}
            
            output.println("=> Position at start of turn: " + initialPosition);
            
            /*
                Roll the dice and move the piece
            */
			int newDiceRoll = diceRoller.nextDiceRoll();
            output.println("=> Rolled a " + newDiceRoll + "!");
			piece.move(newDiceRoll);

            /*
                Has the piece just won?
            */
			if (winEvaluator.hasPieceWon(piece, board)) {
				System.out.println("\n<==> Piece " + pieceNum + " has won the game <==>");
				break;
			}
			
            /*
                Does the piece need to move based on hit rules?    
            */
			piece.setPosition(
				collisionHandler.canPieceOccupyNewSpace(
					allPositions, 
					initialPosition, 
					piece.getCurrentPosition()));

            /*
                Did the piece land on a special spot?
            */
			if (board.pieceHasLandedOnSpecialSpot(piece.getCurrentPosition())) {
				piece.setPosition(
                    board.getSpecialPositionBehaviour(piece.getCurrentPosition())
                );
                /*
                    Does THIS need to be voided based on hit rules?
                */
                piece.setPosition(
                    collisionHandler.canPieceOccupyNewSpace(
                        allPositions, 
                        initialPosition, 
                        piece.getCurrentPosition()));

				output.println("=> The piece landed on a special spot and is now " + piece.getCurrentPosition());
            }
            output.println("=> Position at end of turn:   " + piece.getCurrentPosition());
		}
    }

    @Override
    public GameState progessState() {
        return new GameOver();
    }

}
