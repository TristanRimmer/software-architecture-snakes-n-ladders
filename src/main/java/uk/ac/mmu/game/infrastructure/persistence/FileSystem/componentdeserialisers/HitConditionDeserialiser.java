package uk.ac.mmu.game.infrastructure.persistence.FileSystem.componentdeserialisers;

import java.util.List;

import uk.ac.mmu.game.domain.rules.hitcondition.CollisionCondition;
import uk.ac.mmu.game.domain.util.ImplFactoryException;
import uk.ac.mmu.game.infrastructure.persistence.FileSystem.FileSystemUtil;
import uk.ac.mmu.game.infrastructure.serialisation.HitConditionSerialiser;

public class HitConditionDeserialiser implements GamSubcomponentDeserialiser<CollisionCondition> {

    private boolean instanceCreated = false;
    private CollisionCondition condition = null;
    private final String HIT_CONDITION_HEADER;

    public HitConditionDeserialiser(String header) {
        this.HIT_CONDITION_HEADER = header;
    }

    @Override
    public void loadNextLine(String line) {
        if (this.loadedObjectSuccessfully())
            return;

        List<String> linePieces = FileSystemUtil.splitString(line); 
        
        if (linePieces.size() != 2 || !linePieces.getFirst().equals(HIT_CONDITION_HEADER))
            return;

        try {
            this.condition = HitConditionSerialiser.getImplementationFromString(linePieces.get(1));
            this.instanceCreated = true;
        } catch (ImplFactoryException e) { /* Implicit Return */ }
    }

    @Override
    public boolean loadedObjectSuccessfully() {
        return this.instanceCreated;
    }

    @Override
    public CollisionCondition getObject() throws DeserialisedObjectNotCreatedException {
        if (!this.loadedObjectSuccessfully())
            throw new DeserialisedObjectNotCreatedException("Hit Condition not created successfully");
        
        return this.condition;
    }
}
