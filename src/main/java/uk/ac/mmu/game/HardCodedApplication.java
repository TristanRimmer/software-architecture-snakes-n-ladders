package uk.ac.mmu.game;

import java.util.List;

import uk.ac.mmu.game.domain.events.GameEventPublisher;
import uk.ac.mmu.game.domain.game.GameConfiguration;
import uk.ac.mmu.game.domain.util.PieceConfigurationsList;
import uk.ac.mmu.game.infrastructure.contextsource.HardCodedGameConfigurationSource;
import uk.ac.mmu.game.infrastructure.contextsource.HardCodedGameRepositorySource;
import uk.ac.mmu.game.infrastructure.contextsource.HardCodedPieceConfigurationSource;
import uk.ac.mmu.game.infrastructure.input.CommandLineInterface;
import uk.ac.mmu.game.infrastructure.output.StylisedPrinter;
import uk.ac.mmu.game.infrastructure.output.SystemOut;
import uk.ac.mmu.game.infrastructure.subscribers.PrettyConsolePrinter;
import uk.ac.mmu.game.infrastructure.subscribers.TurnsTracker;
import uk.ac.mmu.game.usecase.GameRepository;
import uk.ac.mmu.game.usecase.action.GameAction;
import uk.ac.mmu.game.usecase.action.NewGameAction;
import uk.ac.mmu.game.usecase.action.ReplayGameAction;
import uk.ac.mmu.game.usecase.action.SessionOverAction;

public class HardCodedApplication {
	private static final List<String> PLAY_OPTIONS = List.of("New", "Replay", "Exit");
    public static void main(String[] args) {

        GameRepository repository = HardCodedGameRepositorySource.getGameRepository();

		while (true) {
            StylisedPrinter.printBanner(new SystemOut(), "Snakes & Ladders");

			// Not configurable
            GameEventPublisher publisher = new GameEventPublisher();
            publisher.registerNewSubscriber(new PrettyConsolePrinter());
            publisher.registerNewSubscriber(new TurnsTracker());

            GameAction action = switch (
				CommandLineInterface.pickOptionNumberFromList(
					"Choose how you would like to source a game", 
					PLAY_OPTIONS)) {
                        case 0 -> { 
                            GameConfiguration config = HardCodedGameConfigurationSource.getGameConfiguration();
                            PieceConfigurationsList pieces = HardCodedPieceConfigurationSource.getPieceConfigList();

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
