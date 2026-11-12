package uk.ac.mmu.game.domain.events;

import uk.ac.mmu.game.domain.events.types.ConfigurableStateRelatedEvent;

public interface ConfigurableStateRelatedEventSubscriber {
    void notify(ConfigurableStateRelatedEvent event);
}
