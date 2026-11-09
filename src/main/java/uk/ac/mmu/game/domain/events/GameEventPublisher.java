package uk.ac.mmu.game.domain.events;

import java.util.ArrayList;

import uk.ac.mmu.game.domain.events.types.ConfigurableStateRelatedEvent;
import uk.ac.mmu.game.domain.events.types.GameRelatedEvent;
import uk.ac.mmu.game.domain.events.types.PieceRelatedEvent;

public class GameEventPublisher {
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
        for (ConfigurableStateRelatedEventSubscriber sub : this.configurableStateSubscribers) {
            sub.notify(event);
        }
    }
    public void publish(GameRelatedEvent event) {
        for (GameRelatedEventSubscriber sub : this.gameRelatedEventSubscribers) {
            sub.notify(event);
        }
    }
    public void publish(PieceRelatedEvent event) {
        for (PieceRelatedEventSubscriber sub : this.pieceRelatedEventSubscribers) {
            sub.notify(event);
        }
    }
}
