package uk.ac.mmu.game.domain.events;

import uk.ac.mmu.game.domain.events.types.GameEvent;

public interface GameEventSubscriber {
    void notify(GameEvent state);
}
