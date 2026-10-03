package uk.ac.mmu.game;

import java.util.List;

import uk.ac.mmu.game.domain.events.GameEventPublisher;
import uk.ac.mmu.game.domain.game.Game;
import uk.ac.mmu.game.domain.game.GameConfiguration;
import uk.ac.mmu.game.domain.pieces.container.PieceContainer;
import uk.ac.mmu.game.infrastructure.factories.HardCodedGameConfigurationFactory;
import uk.ac.mmu.game.infrastructure.factories.HardCodedGameRepositoryFactory;
import uk.ac.mmu.game.infrastructure.factories.HardCodedPieceConfigurationFactory;
import uk.ac.mmu.game.infrastructure.input.CommandLineInterface;
import uk.ac.mmu.game.infrastructure.output.StylisedPrinter;
import uk.ac.mmu.game.infrastructure.output.SystemOut;
import uk.ac.mmu.game.infrastructure.subscribers.ConsolePrinter;
import uk.ac.mmu.game.infrastructure.subscribers.DiceRollRecorder;
import uk.ac.mmu.game.infrastructure.subscribers.MetadataDumper;
import uk.ac.mmu.game.infrastructure.subscribers.TurnsTracker;
import uk.ac.mmu.game.usecase.GameConfigurationFactory;
import uk.ac.mmu.game.usecase.GameIDInvalidException;
import uk.ac.mmu.game.usecase.GameRepository;
import uk.ac.mmu.game.usecase.GameRepositoryFactory;
import uk.ac.mmu.game.usecase.GameStore;
import uk.ac.mmu.game.usecase.PieceConfigurationFactory;

public class HardCodedApplication {
	private static final List<String> PLAY_OPTIONS = List.of("New", "Replay", "Exit");
    public static void main(String[] args) {
        GameRepositoryFactory gameRepositoryFactory = new HardCodedGameRepositoryFactory();
        GameConfigurationFactory gameConfigurationFactory = new HardCodedGameConfigurationFactory();
        PieceConfigurationFactory pieceConfigurationFactory = new HardCodedPieceConfigurationFactory();

        GameRepository repository = gameRepositoryFactory.getGameRepository();

		while (true) {
            StylisedPrinter.printBanner(new SystemOut(), "Snakes & Ladders");

            // Objects are recreated each run so internal behaviour resets
            GameEventPublisher publisher = new GameEventPublisher();
            DiceRollRecorder diceRollTracker = new DiceRollRecorder();

            publisher.registerNewSubscriber(new ConsolePrinter());
            publisher.registerNewSubscriber(new TurnsTracker());
            publisher.registerNewSubscriber(diceRollTracker);

			switch(
				CommandLineInterface.pickOptionNumberFromList(
					"Choose how you would like to source a game", 
					PLAY_OPTIONS)
			) {
				case 0 -> {
					System.out.println("Create New");
                    GameConfiguration config = gameConfigurationFactory.getGameConfiguration();
                    PieceContainer pieces = pieceConfigurationFactory.getPieceContainer(
                        config.board().getBoardWidth(), 
                        config.board().getBoardHeight());

                    publisher.registerNewSubscriber(new MetadataDumper(config, pieces));

                    Game currentGame = new Game(config, pieces, publisher);
                    
                    currentGame.play();

                    List<Integer> diceStream = diceRollTracker.getListOfDiceRolls();

                    String gameName = 
                        CommandLineInterface.askQuestionAndGetStringBack("Choose a name for this game save (or leave blank to stop saving): ");
                    
                    if (gameName.strip().isBlank())
                        break;

                    GameStore gameStore = new GameStore(config, pieces, diceStream, gameName);

                    repository.saveGame(gameStore);
				}
				case 1 -> {
					System.out.println("Replay Existing");

                    int gameChoice = CommandLineInterface.pickOptionNumberFromList(
                        "Choose which game to load: ", repository.getSavedGameOptions());

                    try {
                        GameStore gameStore = repository.loadGame(gameChoice);
                        Game currentGame = new Game(gameStore.configuration(), gameStore.pieces(), publisher);

                        publisher.registerNewSubscriber(new MetadataDumper(gameStore.configuration(), gameStore.pieces()));

                        currentGame.play();
                    } catch (GameIDInvalidException e) {
                        System.out.println("Error: the chosen game could not be serialised. Please try another game");
                    }
				}
				case 2 -> {
					return;	
				}
				default -> {}
			}
		}
    }
}
