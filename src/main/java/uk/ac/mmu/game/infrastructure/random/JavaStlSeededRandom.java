package uk.ac.mmu.game.infrastructure.random;

import java.util.Random;

import uk.ac.mmu.game.domain.dice.GenerateRandomNumber;

public class JavaStlSeededRandom implements GenerateRandomNumber {
    private final Random randGen;

    public JavaStlSeededRandom(int seed) {
        this.randGen = new Random(seed);
    }
    @Override
    public int randomNumberInRange(int lowerBoundInclusive, int upperBoundNonInclusive) {
        return this.randGen.nextInt(lowerBoundInclusive, upperBoundNonInclusive); 
    }
}
