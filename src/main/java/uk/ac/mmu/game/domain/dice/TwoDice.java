package uk.ac.mmu.game.domain.dice;

public class TwoDice implements NextDiceRoll {
    GenerateRandomNumber rng;
    int maxDiceRoll;
    
    public TwoDice(GenerateRandomNumber rng, int maxDiceRoll) {
        this.rng = rng;
        this.maxDiceRoll = maxDiceRoll;
    }

    @Override
    public int nextDiceRoll() {
        return 2 
            + rng.randomNumberInRange(0, this.maxDiceRoll) 
            + rng.randomNumberInRange(0, this.maxDiceRoll);
    }
}
