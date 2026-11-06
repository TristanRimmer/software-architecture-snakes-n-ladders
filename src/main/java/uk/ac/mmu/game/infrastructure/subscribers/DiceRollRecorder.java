package uk.ac.mmu.game.infrastructure.subscribers;

import java.util.ArrayList;
import java.util.List;

import uk.ac.mmu.game.domain.events.GameRelatedEventSubscriber;
import uk.ac.mmu.game.domain.events.types.BetterGameStateTransition;
import uk.ac.mmu.game.domain.events.types.DiceRolled;
import uk.ac.mmu.game.domain.events.types.PieceWon;
import uk.ac.mmu.game.domain.events.types.TurnChange;

public class DiceRollRecorder implements GameRelatedEventSubscriber{
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
    public void notify(BetterGameStateTransition event) {}

    @Override
    public void notify(DiceRolled event) {
        if (!this.gameWon)
            this.list.add(event.diceRoll());
    }

    @Override
    public void notify(TurnChange event) {}

    @Override
    public void notify(PieceWon event) {
        this.gameWon = true;
    }
}
