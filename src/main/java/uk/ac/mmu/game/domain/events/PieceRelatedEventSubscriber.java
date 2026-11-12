package uk.ac.mmu.game.domain.events;

import uk.ac.mmu.game.domain.events.types.PieceRelatedEvent;

public interface PieceRelatedEventSubscriber {
    void notify(PieceRelatedEvent event);
}
