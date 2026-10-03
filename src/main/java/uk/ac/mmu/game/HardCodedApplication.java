package uk.ac.mmu.game;

import java.util.List;

import uk.ac.mmu.game.domain.events.GameEventPublisher;
import uk.ac.mmu.game.domain.game.GameConfiguration;
import uk.ac.mmu.game.infrastructure.factories.HardCodedGameConfigurationFactory;
import uk.ac.mmu.game.infrastructure.factories.HardCodedGameRepositoryFactory;
import uk.ac.mmu.game.infrastructure.factories.HardCodedPieceConfigurationFactory;
import uk.ac.mmu.game.infrastructure.input.CommandLineInterface;
import uk.ac.mmu.game.infrastructure.output.StylisedPrinter;
import uk.ac.mmu.game.infrastructure.output.SystemOut;
import uk.ac.mmu.game.infrastructure.subscribers.ConsolePrinter;
import uk.ac.mmu.game.infrastructure.subscribers.TurnsTracker;
import uk.ac.mmu.game.usecase.GameAction;
import uk.ac.mmu.game.usecase.GameConfigurationFactory;
import uk.ac.mmu.game.usecase.GameRepository;
import uk.ac.mmu.game.usecase.GameRepositoryFactory;
import uk.ac.mmu.game.usecase.NewGameAction;
import uk.ac.mmu.game.usecase.PieceConfigurationFactory;
import uk.ac.mmu.game.usecase.PieceConfigurationsList;
import uk.ac.mmu.game.usecase.ReplayGameAction;
import uk.ac.mmu.game.usecase.SessionOverAction;

public class HardCodedApplication {
	private static final List<String> PLAY_OPTIONS = List.of("New", "Replay", "Exit");
    public static void main(String[] args) {
        GameRepositoryFactory gameRepositoryFactory = new HardCodedGameRepositoryFactory();
        GameConfigurationFactory gameConfigurationFactory = new HardCodedGameConfigurationFactory();
        PieceConfigurationFactory pieceConfigurationFactory = new HardCodedPieceConfigurationFactory();

        GameRepository repository = gameRepositoryFactory.getGameRepository();

		while (true) {
            StylisedPrinter.printBanner(new SystemOut(), "Snakes & Ladders");

			// Not configurable
            GameEventPublisher publisher = new GameEventPublisher();
            publisher.registerNewSubscriber(new ConsolePrinter());
            publisher.registerNewSubscriber(new TurnsTracker());

            GameAction action = switch (
				CommandLineInterface.pickOptionNumberFromList(
					"Choose how you would like to source a game", 
					PLAY_OPTIONS)) {
                        case 0 -> { 
                            GameConfiguration config = gameConfigurationFactory.getGameConfiguration();
                            PieceConfigurationsList pieces = pieceConfigurationFactory.getPieceConfigList(
                                config.board().getBoardWidth(), config.board().getBoardHeight());

                            yield new NewGameAction(
                                config,
                                pieces,
                                publisher, 
                                repository
                            );
                        }
                        case 1 -> new ReplayGameAction(
                            publisher, 
                            repository
                        );
                        default -> new SessionOverAction(); 
                    };
            if (action.endOfSession())
                return;

            action.run();
		}
    }
}
