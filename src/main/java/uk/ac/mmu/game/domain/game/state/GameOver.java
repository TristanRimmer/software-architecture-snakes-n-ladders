package uk.ac.mmu.game.domain.game.state;

import uk.ac.mmu.game.domain.events.types.ArbitraryMessage;
import uk.ac.mmu.game.domain.events.types.GameStateTransition;
import uk.ac.mmu.game.domain.game.Game;

public final class GameOver implements GameState {

    @Override
    public GameState execute(Game context) {
        context.getEventPublisher().publish(new GameStateTransition("Game Over"));
        context.getEventPublisher().publish(new ArbitraryMessage("TODO: Gathering data from the game just gone.."));

        return new GameOver(); 
    }

    @Override
    public boolean isEndOfChain() {
        return true;
    }

}
