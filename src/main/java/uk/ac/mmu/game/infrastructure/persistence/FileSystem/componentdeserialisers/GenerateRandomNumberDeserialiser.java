package uk.ac.mmu.game.infrastructure.persistence.FileSystem.componentdeserialisers;

import java.util.List;

import uk.ac.mmu.game.domain.dice.GenerateRandomNumber;
import uk.ac.mmu.game.domain.util.ImplFactoryException;
import uk.ac.mmu.game.infrastructure.persistence.FileSystem.FileSystemUtil;
import uk.ac.mmu.game.infrastructure.random.JavaStlRandomSeeded;
import uk.ac.mmu.game.infrastructure.serialisation.GenerateRandomNumberSerialiser;

public class GenerateRandomNumberDeserialiser implements GamSubcomponentDeserialiser<GenerateRandomNumber>{
    private final String RANDOM_NUMBER_GENERATOR;

    private boolean instanceCreated = false;
    private GenerateRandomNumber rng = null;

    public GenerateRandomNumberDeserialiser(String header) {
        this.RANDOM_NUMBER_GENERATOR = header;
    }

    @Override
    public void loadNextLine(String line) {
        if (this.loadedObjectSuccessfully())
            return;

        List<String> linePieces = FileSystemUtil.splitString(line); 
        
        if (linePieces.size() < 2 || !linePieces.getFirst().equals(this.RANDOM_NUMBER_GENERATOR))
            return;

        try {
            this.rng = GenerateRandomNumberSerialiser.getImplementationFromString(linePieces.get(1));
            this.instanceCreated = true;
        } catch (ImplFactoryException e) { 
            return;
        }
        // Seeded version needs seed
        if (linePieces.size() == 3 && this.rng instanceof JavaStlRandomSeeded rngs) {
            try {
                int seed = Integer.parseInt(linePieces.getLast());
                rngs.setSeed(seed);  
            } catch (NumberFormatException _e) { /* Implicit return */}
        }
    }

    @Override
    public boolean loadedObjectSuccessfully() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'loadedObjectSuccessfully'");
    }

    @Override
    public GenerateRandomNumber getObject() throws DeserialisedObjectNotCreatedException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getObject'");
    }

}
