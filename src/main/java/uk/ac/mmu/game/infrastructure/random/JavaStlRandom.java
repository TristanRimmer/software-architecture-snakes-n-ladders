package uk.ac.mmu.game.infrastructure.random;

import java.util.Random;

import uk.ac.mmu.game.domain.dice.GenerateRandomNumber;

public class JavaStlRandom implements GenerateRandomNumber {
    private final Random randGen;

    public JavaStlRandom() {
        this.randGen = new Random();
    }
    @Override
    public int randomNumberInRange(int lowerBoundInclusive, int upperBoundNonInclusive) {
        return this.randGen.nextInt(lowerBoundInclusive, upperBoundNonInclusive); 
    }
}
