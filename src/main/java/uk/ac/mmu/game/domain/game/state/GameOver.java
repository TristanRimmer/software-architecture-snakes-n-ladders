package uk.ac.mmu.game.domain.game.state;

import uk.ac.mmu.game.domain.events.types.BetterGameStateTransition;
import uk.ac.mmu.game.domain.game.Game;

public final class GameOver implements GameState {
    @Override
    public GameState execute(Game context) {
        context.getEventPublisher().publish(new BetterGameStateTransition(GameOver.class));
        return new GameOver(); 
    }

    @Override
    public boolean isEndOfChain() {
        return true;
    }

}
