package uk.ac.mmu.game.diceroller;

import java.util.ArrayList;

public class DiceRoller implements DiceRollingService {
    NextDiceRoll diceRoller;
    ArrayList<Integer> rollHistory;

    public DiceRoller(NextDiceRoll diceRoller) {
        this.diceRoller = diceRoller;
        this.rollHistory = new ArrayList<>();
    }

    @Override
    public int nextDiceRoll() {
        Integer roll = diceRoller.nextDiceRoll();

        rollHistory.add(roll);
        
        return roll;
    }

    @Override
    public ArrayList<Integer> getDiceRollHistory() {
        return this.rollHistory;
    }
}
