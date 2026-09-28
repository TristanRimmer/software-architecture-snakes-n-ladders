package uk.ac.mmu.game.observer.subscribers;

import uk.ac.mmu.game.observer.events.GameEvent;

public interface GameEventSubscriber {
    void notify(GameEvent state);
}
