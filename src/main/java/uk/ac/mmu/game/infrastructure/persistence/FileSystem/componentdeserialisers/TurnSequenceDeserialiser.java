package uk.ac.mmu.game.infrastructure.persistence.FileSystem.componentdeserialisers;

import java.util.List;

import uk.ac.mmu.game.domain.game.state.gameturn.GameTurn;
import uk.ac.mmu.game.domain.util.ImplFactoryException;
import uk.ac.mmu.game.infrastructure.persistence.FileSystem.FileSystemUtil;
import uk.ac.mmu.game.infrastructure.serialisation.GameTurnSerialiser;

public class TurnSequenceDeserialiser implements GamSubcomponentDeserialiser<GameTurn> {
    private final String TURN_SEQUENCE_HEADER;

    private boolean instanceCreated = false;
    private GameTurn sequence = null;

    public TurnSequenceDeserialiser(String header) {
        this.TURN_SEQUENCE_HEADER = header;
    }

    @Override
    public void loadNextLine(String line) {
        if (this.loadedObjectSuccessfully())
            return;

        List<String> linePieces = FileSystemUtil.splitString(line); 
        
        if (linePieces.size() != 2 || !linePieces.getFirst().equals(this.TURN_SEQUENCE_HEADER))
            return;

        try {
            this.sequence = GameTurnSerialiser.getImplementationFromString(linePieces.get(1));
            this.instanceCreated = true;
        } catch (ImplFactoryException e) { /* Implicit Return */ }
    }

    @Override
    public boolean loadedObjectSuccessfully() {
        return this.instanceCreated;
    }

    @Override
    public GameTurn getObject() throws DeserialisedObjectNotCreatedException {
        if (!this.loadedObjectSuccessfully())
            throw new DeserialisedObjectNotCreatedException("Turn Sequence Not successfully created");

        return this.sequence;
    }

}
