package uk.ac.mmu.game.infrastructure.serialisation;

import uk.ac.mmu.game.domain.dice.GenerateRandomNumber;
import uk.ac.mmu.game.domain.util.ImplFactoryException;
import uk.ac.mmu.game.infrastructure.random.JavaStlRandom;
import uk.ac.mmu.game.infrastructure.random.JavaStlRandomSeeded;

public class GenerateRandomNumberSerialiser {
    public static final String JAVA_STL_UNSEEDED = "Default";
    public static final String JAVA_STL_SEEDED = "StlSeeded";

    public static GenerateRandomNumber getImplementationFromString(String string) throws ImplFactoryException {
        switch (string) {
            case JAVA_STL_UNSEEDED:
                return new JavaStlRandom();
            case JAVA_STL_SEEDED:
                return new JavaStlRandomSeeded();
            default:
                throw new ImplFactoryException(
                        "The provided string does not map to any existing implementation");
        }
    }

    public static String getStringFromImplementation(GenerateRandomNumber impl) throws ImplFactoryException {
        if (impl instanceof JavaStlRandom)
            return JAVA_STL_UNSEEDED;
        if (impl instanceof JavaStlRandomSeeded)        
            return JAVA_STL_SEEDED;

        throw new ImplFactoryException("Provided implementation doesnt have a serialisable string yet");
    }
}
