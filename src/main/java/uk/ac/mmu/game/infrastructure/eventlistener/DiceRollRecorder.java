package uk.ac.mmu.game.infrastructure.eventlistener;

import java.util.ArrayList;
import java.util.List;

import uk.ac.mmu.game.domain.events.GameEventSubscriber;
import uk.ac.mmu.game.domain.events.types.DiceRolled;
import uk.ac.mmu.game.domain.events.types.GameEvent;
import uk.ac.mmu.game.domain.events.types.PieceWon;

public class DiceRollRecorder implements GameEventSubscriber {
    private final List<Integer> list;
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

    public List<Integer> getListOfDiceRolls() {
        return this.list;
    }
}
