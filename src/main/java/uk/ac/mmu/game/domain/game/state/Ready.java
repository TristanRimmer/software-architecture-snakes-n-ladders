package uk.ac.mmu.game.domain.game.state;

import uk.ac.mmu.game.domain.events.GameEventPublisher;
import uk.ac.mmu.game.domain.events.types.BetterGameStateTransition;
import uk.ac.mmu.game.domain.game.Game;

public final class Ready implements GameState {

    @Override
    public GameState execute(Game context) {
        GameEventPublisher output = context.getEventPublisher();
        output.publish(new BetterGameStateTransition(Ready.class));

        return new InPlay();
    }

    @Override
    public boolean isEndOfChain() {
        return false;
    }
}
