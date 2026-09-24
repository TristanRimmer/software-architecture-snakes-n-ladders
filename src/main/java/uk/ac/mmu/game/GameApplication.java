package uk.ac.mmu.game;

import java.util.Random;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import uk.ac.mmu.game.pieces.LowLeftToUpRightPiece;
import uk.ac.mmu.game.pieces.PieceMovesetFromOrigin;

@SpringBootApplication
public class GameApplication {

	public static void main(String[] args) {
		SpringApplication.run(GameApplication.class, args);

		System.out.println("Hello world!");

		final int grid_x = 10;
		final int grid_y = 10;
		final int rand_seed = 100;

		PieceMovesetFromOrigin piece = new LowLeftToUpRightPiece(grid_x, grid_y);

		final int MAX_DICE_ROLL = 6;

		System.out.println("Starting Piece Position: " + piece.getPosition().toString());
		System.out.println("System Dimensions: [" + grid_x + ", " + grid_y + "]");

		Random rand_gen = new Random();

		rand_gen.setSeed(rand_seed);
		
		for (int i = 0; i < 100; i++) {
			int dice_roll = 1; //rand_gen.nextInt(MAX_DICE_ROLL + 1);
			
			System.out.println("Piece Position before and after following a " + dice_roll + ": " + piece.getPosition().toString() + " -> " + piece.moveForward(dice_roll).toString());
		}
	}
}
