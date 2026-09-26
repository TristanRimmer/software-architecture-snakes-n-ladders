package uk.ac.mmu.game.diceroller;

import java.util.Random;

public class TwoDice implements NextDiceRoll {
    Random randGen;
    int maxDiceRoll;
    
    public TwoDice(int maxDiceRoll) {
        this.randGen = new Random();
        this.maxDiceRoll = maxDiceRoll;
    }

    @Override
    public int nextDiceRoll() {
        return 2 + randGen.nextInt(maxDiceRoll) + randGen.nextInt(maxDiceRoll);
    }
}
