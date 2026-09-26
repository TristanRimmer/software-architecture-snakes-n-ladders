package uk.ac.mmu.game.diceroller;

import java.util.Random;

public class SingleDice implements NextDiceRoll {
    Random randGen;
    int maxDiceRoll;
    
    public SingleDice(int maxDiceRoll) {
        this.randGen = new Random();
        this.maxDiceRoll = maxDiceRoll;
    }

    @Override
    public int nextDiceRoll() {
        return 1 + randGen.nextInt(maxDiceRoll);
    }

}
