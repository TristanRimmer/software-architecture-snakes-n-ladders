package uk.ac.mmu.game.gamestate;

import uk.ac.mmu.game.observer.events.ArbitraryMessage;
import uk.ac.mmu.game.observer.events.GameStateTransition;

public final class GameOver implements GameState {

    @Override
    public void execute(Game context) {
        context.getEventPublisher().publish(new GameStateTransition("Game Over"));
        context.getEventPublisher().publish(new ArbitraryMessage("TODO: Gathering data from the game just gone.."));
    }

    @Override
    public GameState progessState() {
        return new Ready();
    }

}
