package uk.ac.mmu.game.usecase;

import uk.ac.mmu.game.domain.events.GameEventPublisher;
import uk.ac.mmu.game.domain.game.Game;
import uk.ac.mmu.game.domain.game.GameConfiguration;
import uk.ac.mmu.game.domain.pieces.container.PieceContainer;
import uk.ac.mmu.game.infrastructure.input.CommandLineInterface;
import uk.ac.mmu.game.infrastructure.subscribers.MetadataDumper;

public final class ReplayGameAction implements GameAction {
    private final GameEventPublisher publisher;
    private final GameRepository repository;
    
    public ReplayGameAction(
        GameEventPublisher publisher,
        GameRepository repository
    ) {
        this.publisher = publisher;
        this.repository = repository;
    }

    @Override 
    public void run() {
        // Before anything, make sure this was a valid option to begin with
        if (this.repository.getSavedGameOptions().isEmpty()) {
            System.out.println("There are currently no saved games");
            return;
        }

        int gameChoice = CommandLineInterface.pickOptionNumberFromList(
            "Choose which game to load: ", this.repository.getSavedGameOptions());

        GameStore gameStore;
        try {
            gameStore = this.repository.loadGame(gameChoice);    
        } catch (GameIDInvalidException e) {
            System.out.println("Error: The chosen game could not be serialised due to: " + e.getMessage());
            System.out.println("Ensure that the game you chose to load is valid. Continuing..");
            return;
        }

        GameConfiguration config = gameStore.configuration();
        PieceContainer pieces = gameStore.pieces();

        // Still needs a data dumper for pretty printing
        this.publisher.registerNewSubscriber(
            new MetadataDumper(config, pieces));

        Game game = new Game(config, pieces, this.publisher);
        game.play();
    }

    @Override
    public boolean endOfSession() {
        return false;
    }
}
