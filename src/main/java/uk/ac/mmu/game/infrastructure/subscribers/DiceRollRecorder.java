package uk.ac.mmu.game.infrastructure.subscribers;

import java.util.ArrayList;
import java.util.List;

import uk.ac.mmu.game.domain.events.GameRelatedEventSubscriber;
import uk.ac.mmu.game.domain.events.types.BetterGameStateTransition;
import uk.ac.mmu.game.domain.events.types.DiceRolled;
import uk.ac.mmu.game.domain.events.types.GameRelatedEvent;
import uk.ac.mmu.game.domain.events.types.TurnChange;
import uk.ac.mmu.game.domain.game.state.GameOver;

public final class DiceRollRecorder implements GameRelatedEventSubscriber {
    private final List<Integer> list;
    private boolean gameWon;

    public DiceRollRecorder() {
        this.list = new ArrayList<>();
        this.gameWon = false;
    }

    public List<Integer> getListOfDiceRolls() {
        return this.list;
    }

    @Override
    public void notify(GameRelatedEvent event) {
        switch (event) {
            case DiceRolled ev -> this.action(ev);
            case BetterGameStateTransition ev -> this.action(ev);
            case TurnChange ev -> {}
        }
    }

    private void action(DiceRolled event) {
        if (!this.gameWon)
            this.list.add(event.diceRoll());
    }

    private void action(BetterGameStateTransition event) {
        if (event.state().equals(GameOver.class))
            this.gameWon = true;
    }
}
