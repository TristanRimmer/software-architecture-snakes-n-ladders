package uk.ac.mmu.game.infrastructure.persistence.FileSystem.componentdeserialisers;

import java.util.List;

import uk.ac.mmu.game.domain.rules.wincondition.WinCondition;
import uk.ac.mmu.game.domain.util.ImplFactoryException;
import uk.ac.mmu.game.infrastructure.persistence.FileSystem.FileSystemUtil;
import uk.ac.mmu.game.infrastructure.serialisation.WinConditionSerialiser;

public class WinConditionDeserialiser implements GamSubcomponentDeserialiser<WinCondition>{

    private boolean instanceCreated = false;
    private WinCondition condition = null;
    private final String WIN_CONDITION_HEADER;

    public WinConditionDeserialiser(String header) {
        this.WIN_CONDITION_HEADER = header;
    }

    @Override
    public void loadNextLine(String line) {
        if (this.loadedObjectSuccessfully())
            return;

        List<String> linePieces = FileSystemUtil.splitString(line); 
        
        if (linePieces.size() != 2 || !linePieces.getFirst().equals(this.WIN_CONDITION_HEADER))
            return;

        try {
            this.condition = WinConditionSerialiser.getImplementationFromString(linePieces.get(1));
            this.instanceCreated = true;
        } catch (ImplFactoryException e) { /* Implicit Return */ }
    }

    @Override
    public boolean loadedObjectSuccessfully() {
        return this.instanceCreated;
    }

    @Override
    public WinCondition getObject() throws DeserialisedObjectNotCreatedException {
        if (!this.loadedObjectSuccessfully())
            throw new DeserialisedObjectNotCreatedException("Win Condition not successfully created");

        return this.condition;
    }
}
