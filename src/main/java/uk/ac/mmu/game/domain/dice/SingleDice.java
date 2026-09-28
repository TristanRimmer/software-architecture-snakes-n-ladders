package uk.ac.mmu.game.domain.dice;

public class SingleDice implements NextDiceRoll {
    GenerateRandomNumber rng;
    int maxDiceRoll;
    
    public SingleDice(GenerateRandomNumber rng, int maxDiceRoll) {
        this.rng = rng;
        this.maxDiceRoll = maxDiceRoll;
    }

    @Override
    public int nextDiceRoll() {
        return 1 + rng.randomNumberInRange(0, this.maxDiceRoll);
    }

}
