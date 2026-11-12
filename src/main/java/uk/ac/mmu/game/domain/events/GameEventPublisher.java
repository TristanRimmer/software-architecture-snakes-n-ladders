package uk.ac.mmu.game.domain.events;

import java.util.ArrayList;

import uk.ac.mmu.game.domain.events.types.ConfigurableStateRelatedEvent;
import uk.ac.mmu.game.domain.events.types.GameRelatedEvent;
import uk.ac.mmu.game.domain.events.types.PieceRelatedEvent;

public final class GameEventPublisher {
    private final ArrayList<ConfigurableStateRelatedEventSubscriber> configurableStateSubscribers;
    private final ArrayList<GameRelatedEventSubscriber> gameRelatedEventSubscribers;
    private final ArrayList<PieceRelatedEventSubscriber> pieceRelatedEventSubscribers;

    public GameEventPublisher() {
        this.configurableStateSubscribers = new ArrayList<>();
        this.gameRelatedEventSubscribers = new ArrayList<>();
        this.pieceRelatedEventSubscribers = new ArrayList<>();
    }

    public void registerNewSubscriber(PieceRelatedEventSubscriber subscriber) {
        this.pieceRelatedEventSubscribers.add(subscriber);
    }
    public void registerNewSubscriber(GameRelatedEventSubscriber subscriber) {
        this.gameRelatedEventSubscribers.add(subscriber);
    }
    public void registerNewSubscriber(ConfigurableStateRelatedEventSubscriber subscriber) {
        this.configurableStateSubscribers.add(subscriber);
    }

    public void publish(ConfigurableStateRelatedEvent event) {
        this.configurableStateSubscribers.forEach(subscriber -> subscriber.notify(event));
    }
    public void publish(GameRelatedEvent event) {
        this.gameRelatedEventSubscribers.forEach(subscriber -> subscriber.notify(event));
    }
    public void publish(PieceRelatedEvent event) {
        this.pieceRelatedEventSubscribers.forEach(subscriber -> subscriber.notify(event));
    }
}
