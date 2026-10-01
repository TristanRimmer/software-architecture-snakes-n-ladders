package uk.ac.mmu.game.infrastructure.random;

import java.util.Random;

import uk.ac.mmu.game.domain.dice.GenerateRandomNumber;

public class JavaStlRandomSeeded implements GenerateRandomNumber {
    private final Random randGen;

    public JavaStlRandomSeeded() {
        this.randGen = new Random();
    }
    public void setSeed(int seed) {
        this.randGen.setSeed(seed);
    }
    @Override
    public int randomNumberInRange(int lowerBoundInclusive, int upperBoundNonInclusive) {
        return this.randGen.nextInt(lowerBoundInclusive, upperBoundNonInclusive); 
    }
}
