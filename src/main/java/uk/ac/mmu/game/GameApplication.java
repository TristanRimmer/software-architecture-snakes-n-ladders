package uk.ac.mmu.game;

import java.util.ArrayList;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import uk.ac.mmu.game.board.Board;
import uk.ac.mmu.game.board.BoardService;
import uk.ac.mmu.game.board.SpecialPositionService;
import uk.ac.mmu.game.board.Teleporter;
import uk.ac.mmu.game.diceroller.DiceRoller;
import uk.ac.mmu.game.diceroller.DiceRollingService;
import uk.ac.mmu.game.diceroller.NextDiceRoll;
import uk.ac.mmu.game.diceroller.SingleDice;
import uk.ac.mmu.game.hitcondition.HitsForfeitTurn;
import uk.ac.mmu.game.hitcondition.PieceCollisionService;
import uk.ac.mmu.game.pieces.LLtoUR;
import uk.ac.mmu.game.pieces.Piece;
import uk.ac.mmu.game.pieces.PieceService;
import uk.ac.mmu.game.pieces.PositionTrackingConverter;
import uk.ac.mmu.game.pieces.URtoLL;
import uk.ac.mmu.game.shared.GridPosition;
import uk.ac.mmu.game.wincondition.ExactHit;
import uk.ac.mmu.game.wincondition.WinEvaluationService;

@SpringBootApplication
public class GameApplication {
	/*
		GameApplication:
		- Owns the board -> DONE!
		- Owns the pieces -> DONE!
		- Owns the DiceRoller -> DONE!
		- Owns a HitCondition Evaluator -> VERY PRIMITIVE VERSION IMPLEMENTED
		- Owns the WinCondition Evaluater -> DONE
	*/
	public static void main(String[] args) {
		SpringApplication.run(GameApplication.class, args);

		/*
			Board Initialisation
		*/
		ArrayList<SpecialPositionService> specialPositions = new ArrayList<>();
		specialPositions.add(new Teleporter(new GridPosition(0,3), new GridPosition(1, 0)));

		BoardService board = new Board(5, 5, specialPositions);

		/*
			Piece initialisation
		*/
		ArrayList<PieceService> pieces = new ArrayList<>();

		// Could implement a wicked factory here
		PositionTrackingConverter pieceConverter = new LLtoUR();
		pieces.add(new Piece(pieceConverter, board.getBoardWidth(), board.getBoardHeight()));

		pieceConverter = new URtoLL();
		pieces.add(new Piece(pieceConverter, board.getBoardWidth(), board.getBoardHeight()));

		/*
			Dice Rolling Initialisation
		*/
		NextDiceRoll chosenDiceImpl = new SingleDice(6);
		// NextDiceRoll chosenDiceImpl = new DiceStreamFixed(
		// 	new ArrayList<>(
		//	 	List.of(1, 1, 5, 2, 5, 3, 2, 3, 6, 5, 2, 6, 6, 6, 5, 1, 6, 1, 2, 6, 4, 1, 6, 1, 5, 3)
		// 		List.of(6, 6, 3, 5, 2, 5, 6, 2, 6, 6, 4, 2, 4, 4, 3)
		// 	)
		// );
		DiceRollingService diceRoller = new DiceRoller(chosenDiceImpl);

		/*
			Win Condition Ininitalisation		
		*/
		WinEvaluationService winEvaluator = new ExactHit();

		/*
			Hit Condition Initialisation - NOT IN USE!
		*/
		PieceCollisionService collisionHandler = new HitsForfeitTurn();

		//System.out.println("Starting Piece Position: " + piece.getCurrentPosition());
		System.out.println("System Dimensions: [" + board.getBoardWidth() + ", " + board.getBoardHeight() + "]");

		int turns = 0;

		while (true) {
			/*
				0th step -> Get a list of all initial positions
				1 - Rolls the Dice
				2 - Gets a new proposed position
				3 - Checks for a win condition based on proposed position and respond correctly
				[TODO] First player hit check
				4 - Checks for a special spot and responds correctly
				[TODO] Second player hit check

				There are two points where it may need to forfeit its turn or not
			*/
			
			/*
				Whose turn is it
			*/
			int pieceNum = turns % pieces.size();
			PieceService piece = pieces.get(pieceNum);
			turns++;
	
			ArrayList<GridPosition> allPositions = new ArrayList<>();
			for (PieceService p : pieces) {
				if (p != piece) // Memory Address Comparison
					allPositions.add(p.getCurrentPosition());
			}

			GridPosition initialPosition = piece.getCurrentPosition();

			System.out.println("========= Piece " + pieceNum + " =========");
			System.out.println(" - Position at start of turn: " + initialPosition);

			int newDiceRoll = diceRoller.nextDiceRoll();

			piece.move(newDiceRoll);

			System.out.println(" - Rolled a " + newDiceRoll + "!");

			if (winEvaluator.hasPieceWon(piece, board)) {
				System.out.println("Piece " + pieceNum + " has reached its winning spot");
				break;
			}
			
			piece.setPosition(
				collisionHandler.canPieceOccupyNewSpace(
					allPositions, 
					initialPosition, 
					piece.getCurrentPosition()));

			GridPosition updatedPiecePosition = piece.getCurrentPosition();

			if (board.pieceHasLandedOnSpecialSpot(updatedPiecePosition)) {
				GridPosition oldProposedPosition = updatedPiecePosition;

				updatedPiecePosition = board.getSpecialPositionBehaviour(updatedPiecePosition);

				System.out.println("Position " + oldProposedPosition + " was a teleporter! Piece is now at " + updatedPiecePosition);
			}

			// Lastly, if piece landed on a special spot update its position from there
			piece.setPosition(updatedPiecePosition);

			piece.setPosition(
				collisionHandler.canPieceOccupyNewSpace(
					allPositions, 
					initialPosition, 
					piece.getCurrentPosition()));

			System.out.println(" - Position at end of turn:   " + piece.getCurrentPosition());
		}

		ArrayList<Integer> diceStream = diceRoller.getDiceRollHistory();

		System.out.println("Dice Roll History: ");
		String diceRollSequence = "| ";
		for (Integer i : diceStream) {
			diceRollSequence = diceRollSequence + i + ", ";
		}
		System.out.println(diceRollSequence + " |");
	}
}
