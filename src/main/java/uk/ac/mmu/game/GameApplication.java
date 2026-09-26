package uk.ac.mmu.game;

import java.util.ArrayList;
import java.util.Random;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import uk.ac.mmu.game.board.Board;
import uk.ac.mmu.game.board.BoardService;
import uk.ac.mmu.game.board.SpecialPositionService;
import uk.ac.mmu.game.board.Teleporter;
import uk.ac.mmu.game.pieces.LLtoUR;
import uk.ac.mmu.game.pieces.Piece;
import uk.ac.mmu.game.pieces.PieceService;
import uk.ac.mmu.game.pieces.PositionTrackingConverter;
import uk.ac.mmu.game.shared.GridPosition;

@SpringBootApplication
public class GameApplication {
	/*
		GameApplication:
		- Owns the board -> Done for now?
		- Owns the pieces -> PIECES ARE DONE!
		- Owns the DiceRoller
		- Owns a HitCondition Evaluator
		- Owns the WinCondition Evaluater
	*/
	public static void main(String[] args) {
		SpringApplication.run(GameApplication.class, args);

		final int rand_seed = 134798234;

		// Create the special positions
		ArrayList<SpecialPositionService> specialPositions = new ArrayList<>();
		specialPositions.add(new Teleporter(new GridPosition(3,4), new GridPosition(1, 0)));

		BoardService board = new Board(5, 5, specialPositions);

		PositionTrackingConverter pieceConverter = new LLtoUR();
		PieceService piece = new Piece(pieceConverter, board.getBoardWidth(), board.getBoardHeight());

		final int MAX_DICE_ROLL = 6;

		System.out.println("Starting Piece Position: " + piece.getCurrentPosition());
		System.out.println("System Dimensions: [" + board.getBoardWidth() + ", " + board.getBoardHeight() + "]");

		Random rand_gen = new Random();

		rand_gen.setSeed(rand_seed);

		for (int i = 0; i < 100; i++) {
			int newDiceRoll = rand_gen.nextInt(MAX_DICE_ROLL + 1);

			GridPosition proposedNewPos = piece.proposeNewPosition(newDiceRoll);
			
			System.out.println("Pos: " + piece.getCurrentPosition() + " + " + newDiceRoll + " -> " + proposedNewPos);

			// Win condition first
			int displFromWinning = piece.getDisplacementFromWinning();

			if (displFromWinning == 0) {
				System.out.println("Piece has reached its winning spot");
				return;
			} else if (displFromWinning > 0) {
				System.out.println("Piece has bounced back from finish by " + displFromWinning + " to " + piece.proposeNewPosition(-2 * displFromWinning));
				proposedNewPos = (piece.proposeNewPosition(-2 * displFromWinning));
			}

			if (board.pieceHasLandedOnSpecialSpot(proposedNewPos)) {
				GridPosition oldProposedPosition = proposedNewPos;
				proposedNewPos = board.getSpecialPositionBehaviour(proposedNewPos);

				System.out.println("Position " + oldProposedPosition + " was a teleporter! Piece is now at " + proposedNewPos);
			}

			piece.setPosition(proposedNewPos);
		}
	}
}
