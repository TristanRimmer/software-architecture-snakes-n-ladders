package uk.ac.mmu.game.infrastructure.subscribers;

import java.util.ArrayList;
import java.util.List;

import uk.ac.mmu.game.domain.events.GameRelatedEventSubscriber;
import uk.ac.mmu.game.domain.events.types.DiceRolled;
import uk.ac.mmu.game.domain.events.types.GameRelatedEvent;

public final class DiceRollRecorder implements GameRelatedEventSubscriber{
    private final List<Integer> list;
    private final Boolean gameWon;

    public DiceRollRecorder() {
        this.list = new ArrayList<>();
        this.gameWon = false;
    }

    public List<Integer> getListOfDiceRolls() {
        return this.list;
    }


    @Override
    public void notify(GameRelatedEvent event) {
        if (event instanceof DiceRolled dr)
            this.action(dr);
    }

    private void action(DiceRolled event) {
        if (!this.gameWon)
            this.list.add(event.diceRoll());
    }
}
