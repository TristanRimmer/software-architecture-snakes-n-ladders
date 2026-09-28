package uk.ac.mmu.game.observer.subscribers;

import java.util.ArrayList;

import uk.ac.mmu.game.observer.events.DiceRolled;
import uk.ac.mmu.game.observer.events.GameEvent;
import uk.ac.mmu.game.observer.events.PieceWon;

public class DiceRollRecorder implements GameEventSubscriber {
    private ArrayList<Integer> list;
    private boolean gameWon;

    public DiceRollRecorder() {
        this.list = new ArrayList<>();
        this.gameWon = false;
    }

    @Override
    public void notify(GameEvent state) {
        if (state instanceof DiceRolled diceRolled) {
            if (!this.gameWon)
                this.list.add(diceRolled.diceRoll());
        } else if (state instanceof PieceWon)
            this.gameWon = true;
    }

    public ArrayList<Integer> getListOfDiceRolls() {
        return this.list;
    }
}
