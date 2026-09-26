package uk.ac.mmu.game.diceroller;

import java.util.ArrayList;

public class DiceStreamFixed implements NextDiceRoll{
    DiceRollFromStream diceRollStream;

    public DiceStreamFixed(ArrayList<Integer> stream) {
        this.diceRollStream = new DiceRollFromStream(stream);
    }
    
    @Override
    public int nextDiceRoll() {
        try {
            return this.diceRollStream.nextDiceRoll();
        } catch (DiceRollStreamMisuseException e) {
            throw new DiceRollStreamRuntimeException("The DiceStreamFixed object has exceeded its stream of dice rolls");
        }
    }
}
