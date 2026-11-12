package uk.ac.mmu.game.domain.events;

import uk.ac.mmu.game.domain.events.types.GameRelatedEvent;

public interface GameRelatedEventSubscriber {
    void notify(GameRelatedEvent event);
}
