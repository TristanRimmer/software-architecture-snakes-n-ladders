package uk.ac.mmu.game;

import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import uk.ac.mmu.game.domain.events.GameEventPublisher;
import uk.ac.mmu.game.domain.game.Game;
import uk.ac.mmu.game.infrastructure.input.CommandLineInterface;
import uk.ac.mmu.game.infrastructure.persistence.FileSystem.FileSystemGameRepository;
import uk.ac.mmu.game.infrastructure.subscribers.ConsolePrinter;
import uk.ac.mmu.game.infrastructure.subscribers.DiceRollRecorder;
import uk.ac.mmu.game.usecase.GameIDInvalidException;
import uk.ac.mmu.game.usecase.GameRepository;
import uk.ac.mmu.game.usecase.GameStore;

@SpringBootApplication
public class GameApplication {
	private static final List<String> PLAY_OPTIONS = List.of("New", "Replay", "Exit");
	public static void main(String[] args) {
		SpringApplication.run(GameApplication.class, args);

		while (true) {
			boolean sessionOver = false;
			switch(
				CommandLineInterface.pickOptionNumberFromList(
					"Choose how you would like to source a game", 
					PLAY_OPTIONS)
			) {
				case 0 -> {
					System.out.println("Create new");
				}
				case 1 -> {
					System.out.println("Replay Existing");
				}
				case 2 -> {
					sessionOver = true;	
				}
				default -> {}
			}

			if (sessionOver)
				break;
		}


		GameEventPublisher publisher = new GameEventPublisher();
		publisher.registerNewSubscriber(new ConsolePrinter());

		DiceRollRecorder diceRollTracker = new DiceRollRecorder();
		publisher.registerNewSubscriber(diceRollTracker);

		GameRepository repository = new FileSystemGameRepository();
		GameStore storedGame;
		try {
			storedGame = repository.loadGame(0);
		} catch (GameIDInvalidException e) {
			e.printStackTrace();
			return;
		}

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
