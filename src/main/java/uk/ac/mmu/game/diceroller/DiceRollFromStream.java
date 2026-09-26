package uk.ac.mmu.game.diceroller;

import java.util.ArrayList;


public class DiceRollFromStream {    
    ArrayList<Integer> stream;
    int currentDiceRollIndex;

    public DiceRollFromStream(ArrayList<Integer> stream) {
        this.stream = stream;
        this.currentDiceRollIndex = 0;
    }

    public int nextDiceRoll() throws DiceRollStreamMisuseException {
        if (currentDiceRollIndex < 0)
            throw new DiceRollStreamRuntimeException("Dice Roll Stream object internal indexer has corrupted");            

        if (currentDiceRollIndex >= stream.size())
            throw new DiceRollStreamMisuseException("Dice Roll Stream exhausted");

        int roll = this.stream.get(this.currentDiceRollIndex);
        
        this.currentDiceRollIndex++;

        return roll;

    }

}
