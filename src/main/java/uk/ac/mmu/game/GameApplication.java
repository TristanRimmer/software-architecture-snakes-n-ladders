package uk.ac.mmu.game;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import uk.ac.mmu.game.board.Board;
import uk.ac.mmu.game.board.BoardService;
import uk.ac.mmu.game.board.SpecialPositionService;
import uk.ac.mmu.game.board.Teleporter;
import uk.ac.mmu.game.diceroller.DiceRoller;
import uk.ac.mmu.game.diceroller.DiceRollingService;
import uk.ac.mmu.game.diceroller.DiceStreamFixed;
import uk.ac.mmu.game.diceroller.NextDiceRoll;
import uk.ac.mmu.game.pieces.LLtoUR;
import uk.ac.mmu.game.pieces.Piece;
import uk.ac.mmu.game.pieces.PieceService;
import uk.ac.mmu.game.pieces.PositionTrackingConverter;
import uk.ac.mmu.game.shared.GridPosition;

@SpringBootApplication
public class GameApplication {
	/*
		GameApplication:
		- Owns the board -> DONE!
		- Owns the pieces -> DONE!
		- Owns the DiceRoller -> DONE!
		- Owns a HitCondition Evaluator
		- Owns the WinCondition Evaluater
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
		PositionTrackingConverter pieceConverter = new LLtoUR();
		PieceService piece = new Piece(pieceConverter, board.getBoardWidth(), board.getBoardHeight());

		/*
			Dice Rolling Initialisation
		*/
		// NextDiceRoll chosenDiceImpl = new SingleDice(6);
		NextDiceRoll chosenDiceImpl = new DiceStreamFixed(
			new ArrayList<>(
				List.of(3, 3, 1, 5, 5, 3, 2, 5, 6, 1, 4, 2)
			)
		);
		DiceRollingService diceRoller = new DiceRoller(chosenDiceImpl);

		System.out.println("Starting Piece Position: " + piece.getCurrentPosition());
		System.out.println("System Dimensions: [" + board.getBoardWidth() + ", " + board.getBoardHeight() + "]");

		boolean gameFinished = false;
		int turns = 0;

		while (!gameFinished) {
			/*
				1 - Rolls the Dice
				2 - Gets a new proposed position
				[TODO] First player hit check
				3 - Checks for a win condition based on proposed position and respond correctly
				4 - Checks for a special spot and responds correctly
				[TODO] Second player hit check

				There are two points where it may need to forfeit its turn or not
			*/
			turns++;

			int newDiceRoll = diceRoller.nextDiceRoll();

			GridPosition proposedNewPos = piece.proposeNewPosition(newDiceRoll);

			System.out.print("[" + turns + "] Pos: " + piece.getCurrentPosition() + " + " + newDiceRoll + " -> " + proposedNewPos);

			// Update its position so displacement calculations work
			piece.setPosition(proposedNewPos);
			int displFromWinning = piece.getDisplacementFromWinning();

			System.out.print(" (Displ: " + displFromWinning + ")\n");

			// Win condition first
			if (displFromWinning == 0) {
				System.out.println("Piece has reached its winning spot");
				gameFinished = true;
				break;
			} else if (displFromWinning > 0) {
				System.out.println("Piece has bounced back from finish by " + displFromWinning + " to " + piece.proposeNewPosition(-2 * displFromWinning));
				proposedNewPos = (piece.proposeNewPosition(-2 * displFromWinning));
			}

			// In case win condition updated proposedNewPos, re-set piece position again

			piece.setPosition(proposedNewPos);

			if (board.pieceHasLandedOnSpecialSpot(proposedNewPos)) {
				GridPosition oldProposedPosition = proposedNewPos;
				proposedNewPos = board.getSpecialPositionBehaviour(proposedNewPos);

				System.out.println("Position " + oldProposedPosition + " was a teleporter! Piece is now at " + proposedNewPos);
			}

			// Lastly, if piece landed on a special spot update its position from there
			piece.setPosition(proposedNewPos);
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
