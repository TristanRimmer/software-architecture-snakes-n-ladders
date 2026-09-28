package uk.ac.mmu.game.domain.events;

import java.util.ArrayList;

import uk.ac.mmu.game.domain.events.types.GameEvent;

public class GameEventPublisher {
    ArrayList<GameEventSubscriber> subscribers;

    public GameEventPublisher() {
        this.subscribers = new ArrayList<>();
    }

    public void registerNewSubscriber(GameEventSubscriber subscriber) {
        this.subscribers.add(subscriber);
    }

    public void publish(GameEvent event) {
        for (GameEventSubscriber subscriber : this.subscribers) {
            subscriber.notify(event);
        }
    }
}
