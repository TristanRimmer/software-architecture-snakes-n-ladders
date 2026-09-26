package uk.ac.mmu.game;

import java.util.Random;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import uk.ac.mmu.game.pieces.DisplacementConversionService;
import uk.ac.mmu.game.pieces.LLtoUR;
import uk.ac.mmu.game.pieces.Piece;
import uk.ac.mmu.game.shared.GridPosition;

@SpringBootApplication
public class GameApplication {
	public static void main(String[] args) {
		SpringApplication.run(GameApplication.class, args);

		System.out.println("Hello world!");

		final int grid_x = 5;
		final int grid_y = 5;
		final int rand_seed = 134798234;

		DisplacementConversionService pieceConverter = new LLtoUR();
		Piece piece = new Piece(pieceConverter, grid_x, grid_y);

		// PieceMovesetFromOrigin piece = new LowLeftToUpRightPiece(grid_x, grid_y);

		final int MAX_DICE_ROLL = 6;

		System.out.println("Starting Piece Position: " + piece.getCurrentPosition());
		System.out.println("System Dimensions: [" + grid_x + ", " + grid_y + "]");

		Random rand_gen = new Random();

		rand_gen.setSeed(rand_seed);

		for (int i = 0; i < 100; i++) {
			int newDiceRoll = rand_gen.nextInt(MAX_DICE_ROLL + 1);

			GridPosition proposedNewPos = piece.getPositionFromMove(newDiceRoll);
			
			System.out.println("Pos: " + piece.getCurrentPosition() + " + " + newDiceRoll + " -> " + proposedNewPos);

			piece.setPosition(proposedNewPos);

			int displFromWinning = piece.getDisplacementFromWinning();

			if (displFromWinning == 0) {
				System.out.println("Piece has reached final perfectly");
				return;
			} else if (displFromWinning > 0) {
				System.out.println("Piece has bounced back from finish by " + displFromWinning + " to " + piece.getPositionFromMove(-2 * displFromWinning));
				piece.setPosition(piece.getPositionFromMove(-2 * displFromWinning));
			}
		}

		// piece.set_position(new GridPosition(4,4));
		
		// for (int i = 0; i < 25; i++) {
		// 	GridPosition newP = piece.moveBackward(1);
		// 	System.out.println("New Position: " + newP.toString());

		// 	piece.set_position(newP);
		// }

		// for (int i = 0; i < 100; i++) {
		// 	int dice_roll = rand_gen.nextInt(MAX_DICE_ROLL + 1);
			
		// 	GridPosition newProposedPosition = piece.moveForward(dice_roll);

		// 	System.out.println("Piece Position before and after rolling a " + dice_roll + ": " + piece.getPosition().toString() + " -> " + newProposedPosition.toString());

		// 	// If it collides then nu uh
		// 	piece.set_position(newProposedPosition); 
			
		// 	int distanceFromWin = piece.distanceFromWinningSpot();

		// 	System.err.println("Piece reports its distance from winning is " + distanceFromWin);
		// 	// TODO: this is wrong
		// 	if (distanceFromWin == 0) {
		// 		System.out.println("Game won");
		// 		return;
		// 	} else if (distanceFromWin > 0) {
		// 		// distanceFromWin * 2:
		// 		// First distanceFromWinw ill offset it back to the winning point
		// 		// Second one is the actual bounce
		// 		GridPosition backPos = piece.moveBackward(distanceFromWin * 2);	
		// 		piece.set_position(backPos);

		// 		System.out.println("Piece bounced off the end and knocked back by " + distanceFromWin + ". Its new position is " + backPos.toString());
		// 	} 
		// }
	}
}
