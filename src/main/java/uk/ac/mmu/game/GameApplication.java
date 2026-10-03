package uk.ac.mmu.game;

import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Import;

import uk.ac.mmu.game.domain.events.GameEventPublisher;
import uk.ac.mmu.game.domain.game.GameConfiguration;
import uk.ac.mmu.game.infrastructure.factories.SpringBootConfiguration;
import uk.ac.mmu.game.infrastructure.input.CommandLineInterface;
import uk.ac.mmu.game.infrastructure.output.StylisedPrinter;
import uk.ac.mmu.game.infrastructure.output.SystemOut;
import uk.ac.mmu.game.infrastructure.subscribers.ConsolePrinter;
import uk.ac.mmu.game.infrastructure.subscribers.TurnsTracker;
import uk.ac.mmu.game.usecase.GameRepository;
import uk.ac.mmu.game.usecase.action.GameAction;
import uk.ac.mmu.game.usecase.action.NewGameAction;
import uk.ac.mmu.game.usecase.action.ReplayGameAction;
import uk.ac.mmu.game.usecase.action.SessionOverAction;
import uk.ac.mmu.game.usecase.types.PieceConfigurationsList;

@Import(SpringBootConfiguration.class)
@SpringBootApplication
public class GameApplication {
	private static final List<String> PLAY_OPTIONS = List.of("New", "Replay", "Exit");
	public static void main(String[] args) {
		ConfigurableApplicationContext context = 
			SpringApplication.run(GameApplication.class, args);

		GameRepository repository = context.getBean(GameRepository.class);

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
                        case 0 -> new NewGameAction(
                                context.getBean(GameConfiguration.class), 
                                context.getBean(PieceConfigurationsList.class),
                                publisher, 
                                repository
                            );
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
