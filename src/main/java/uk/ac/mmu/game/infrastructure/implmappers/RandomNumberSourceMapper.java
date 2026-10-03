package uk.ac.mmu.game.infrastructure.implmappers;

import uk.ac.mmu.game.domain.dice.GenerateRandomNumber;
import uk.ac.mmu.game.infrastructure.random.JavaStlRandom;

public class RandomNumberSourceMapper {
    public static final String JAVA_STL_RANDOM = "Default";

    public static GenerateRandomNumber getImplementationFromString(String name) throws ImplFactoryException {
        if (name.equals(JAVA_STL_RANDOM))
            return new JavaStlRandom();

        throw new ImplFactoryException("Invalid variant passed to game turn factory");
    }

    public static String getStringFromImplementation(GenerateRandomNumber impl) {
        return JAVA_STL_RANDOM;
    }
}
