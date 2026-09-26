package uk.ac.mmu.game.diceroller;

import java.util.ArrayList;

public class DiceStreamUnbounded implements NextDiceRoll{
    DiceRollFromStream diceRollStream;
    NextDiceRoll backupRoller;

    public DiceStreamUnbounded(ArrayList<Integer> stream, NextDiceRoll backup) {
        this.diceRollStream = new DiceRollFromStream(stream);
        this.backupRoller = backup;
    }
    @Override
    public int nextDiceRoll() {
        try {
            return this.diceRollStream.nextDiceRoll();
        } catch (DiceRollStreamMisuseException e) {
            return backupRoller.nextDiceRoll();
        }
    }
}
