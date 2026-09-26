package uk.ac.mmu.game;

import java.util.Random;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import uk.ac.mmu.game.pieces.DisplacementConversionService;
import uk.ac.mmu.game.pieces.LLtoUR;
import uk.ac.mmu.game.pieces.Piece;
import uk.ac.mmu.game.pieces.PieceService;
import uk.ac.mmu.game.shared.GridPosition;

@SpringBootApplication
public class GameApplication {
	public static void main(String[] args) {
		SpringApplication.run(GameApplication.class, args);

		final int grid_x = 5;
		final int grid_y = 5;
		final int rand_seed = 134798234;

		DisplacementConversionService pieceConverter = new LLtoUR();
		PieceService piece = new Piece(pieceConverter, grid_x, grid_y);

		// PieceMovesetFromOrigin piece = new LowLeftToUpRightPiece(grid_x, grid_y);

		final int MAX_DICE_ROLL = 6;

		System.out.println("Starting Piece Position: " + piece.getCurrentPosition());
		System.out.println("System Dimensions: [" + grid_x + ", " + grid_y + "]");

		Random rand_gen = new Random();

		rand_gen.setSeed(rand_seed);

		for (int i = 0; i < 100; i++) {
			int newDiceRoll = rand_gen.nextInt(MAX_DICE_ROLL + 1);

			GridPosition proposedNewPos = piece.proposeNewPosition(newDiceRoll);
			
			System.out.println("Pos: " + piece.getCurrentPosition() + " + " + newDiceRoll + " -> " + proposedNewPos);

			piece.setPosition(proposedNewPos);

			int displFromWinning = piece.getDisplacementFromWinning();

			if (displFromWinning == 0) {
				System.out.println("Piece has reached final perfectly");
				return;
			} else if (displFromWinning > 0) {
				System.out.println("Piece has bounced back from finish by " + displFromWinning + " to " + piece.proposeNewPosition(-2 * displFromWinning));
				piece.setPosition(piece.proposeNewPosition(-2 * displFromWinning));
			}
		}
	}
}
