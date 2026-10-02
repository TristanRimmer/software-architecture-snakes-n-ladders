package uk.ac.mmu.game.domain.dice.variations;

import uk.ac.mmu.game.domain.dice.DiceRolling;
import uk.ac.mmu.game.domain.dice.GenerateRandomNumber;

public class SingleDice implements DiceRolling {
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

    @Override
    public GenerateRandomNumber randomNumbersSource() {
        return this.rng;
    }

}
