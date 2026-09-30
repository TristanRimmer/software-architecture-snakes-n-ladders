package uk.ac.mmu.game;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import uk.ac.mmu.game.domain.board.Board;
import uk.ac.mmu.game.domain.board.GameBoard;
import uk.ac.mmu.game.domain.board.specialpositions.SpecialLinkedPositions;
import uk.ac.mmu.game.domain.dice.DiceRolling;
import uk.ac.mmu.game.domain.dice.variations.SingleDice;
import uk.ac.mmu.game.domain.events.GameEventPublisher;
import uk.ac.mmu.game.domain.events.eventlistener.DiceRollRecorder;
import uk.ac.mmu.game.domain.game.Game;
import uk.ac.mmu.game.domain.game.GameConfiguration;
import uk.ac.mmu.game.domain.game.GameStore;
import uk.ac.mmu.game.domain.game.state.gameturn.ComprehensiveGameTurn;
import uk.ac.mmu.game.domain.pieces.GamePiece;
import uk.ac.mmu.game.domain.pieces.container.LockingPieceContainer;
import uk.ac.mmu.game.domain.pieces.container.PieceContainer;
import uk.ac.mmu.game.domain.pieces.positiontrackers.LowerLeftOrigin;
import uk.ac.mmu.game.domain.pieces.positiontrackers.LowerRightOrigin;
import uk.ac.mmu.game.domain.pieces.positiontrackers.PositionTrackingConverter;
import uk.ac.mmu.game.domain.pieces.positiontrackers.UpperLeftOrigin;
import uk.ac.mmu.game.domain.pieces.positiontrackers.UpperRightOrigin;
import uk.ac.mmu.game.domain.rules.hitcondition.CollisionCondition;
import uk.ac.mmu.game.domain.rules.hitcondition.HitsForfeitTurn;
import uk.ac.mmu.game.domain.rules.wincondition.ExactHit;
import uk.ac.mmu.game.domain.rules.wincondition.WinCondition;
import uk.ac.mmu.game.infrastructure.output.ConsolePrinter;
import uk.ac.mmu.game.infrastructure.persistence.FileSystemGameRepository;
import uk.ac.mmu.game.infrastructure.random.JavaStlRandom;
import uk.ac.mmu.game.usecase.GameIDInvalidException;
import uk.ac.mmu.game.usecase.GameRepository;

@SpringBootApplication
public class GameApplication {
	public static void main(String[] args) {
		SpringApplication.run(GameApplication.class, args);
		// /*
		// * Board Initialisation
		// */
		// ArrayList<SpecialLinkedPositions> specialPositions = new ArrayList<>();
		// // specialPositions.add(new Teleporter(new GridPosition(0,3), new
		// // GridPosition(1, 0)));

		// Board board = new GameBoard(5, 5, specialPositions);

		// /*
		// * Piece initialisation
		// */
		// PieceContainer pieces = new LockingPieceContainer();

		// // Could implement a wicked factory here
		// PositionTrackingConverter pieceConverter = new LowerLeftOrigin();
		// pieces.registerNewPiece(new GamePiece(pieceConverter, board.getBoardWidth(),
		// board.getBoardHeight()));

		// pieceConverter = new UpperRightOrigin();
		// pieces.registerNewPiece(new GamePiece(pieceConverter, board.getBoardWidth(),
		// board.getBoardHeight()));

		// pieceConverter = new UpperLeftOrigin();
		// pieces.registerNewPiece(new GamePiece(pieceConverter, board.getBoardWidth(),
		// board.getBoardHeight()));

		// pieceConverter = new LowerRightOrigin();
		// pieces.registerNewPiece(new GamePiece(pieceConverter, board.getBoardWidth(),
		// board.getBoardHeight()));

		// /*
		// * Dice Rolling Initialisation
		// */
		// DiceRolling diceRoller = new SingleDice(new JavaStlRandom(), 6);
		// // DiceRolling diceRoller = new DiceStreamFixed(
		// // new ArrayList<>(
		// // List.of(5, 5, 4, 5, 2, 1, 2, 1, 4, 3, 2, 3, 5, 3, 3, 2, 2, 6, 2, 4, 3, 4,
		// 2,
		// // 4, 6, 1, 5, 5)
		// // //List.of(1, 1, 5, 2, 5, 3, 2, 3, 6, 5, 2, 6, 6, 6, 5, 1, 6, 1, 2, 6, 4,
		// 1,
		// // 6, 1, 5, 3)
		// // //List.of(6, 6, 3, 5, 2, 5, 6, 2, 6, 6, 4, 2, 4, 4, 3)
		// // )
		// // );
		// /*
		// * Win Condition Ininitalisation
		// */
		// WinCondition winEvaluator = new ExactHit();

		// /*
		// * Hit Condition Initialisation
		// */
		// CollisionCondition collisionHandler = new HitsForfeitTurn();

		GameEventPublisher publisher = new GameEventPublisher();
		publisher.registerNewSubscriber(new ConsolePrinter());

		DiceRollRecorder diceRollTracker = new DiceRollRecorder();
		publisher.registerNewSubscriber(diceRollTracker);

		GameRepository repository = new FileSystemGameRepository();

		GameStore storedGame;
		try {
			storedGame = repository.loadGame(0);
		} catch (GameIDInvalidException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}

		// GameConfiguration config = new GameConfiguration(winEvaluator,
		// collisionHandler, diceRoller, board,
		// new ComprehensiveGameTurn());

		Game game = new Game(storedGame.configuration(), storedGame.pieces(), publisher);

		game.play();

		List<Integer> diceStream = diceRollTracker.getListOfDiceRolls();

		System.out.println("Dice Roll History: ");
		String diceRollSequence = "| ";
		for (Integer i : diceStream) {
			diceRollSequence = diceRollSequence + i + ", ";
		}
		System.out.println(diceRollSequence + " |");

		repository.saveGame(storedGame);
	}
}
