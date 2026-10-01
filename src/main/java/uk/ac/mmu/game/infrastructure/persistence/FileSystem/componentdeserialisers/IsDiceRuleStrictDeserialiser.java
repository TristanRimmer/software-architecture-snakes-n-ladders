package uk.ac.mmu.game.infrastructure.persistence.FileSystem.componentdeserialisers;

import java.util.List;

import uk.ac.mmu.game.infrastructure.persistence.FileSystem.FileSystemUtil;

public class IsDiceRuleStrictDeserialiser implements GamSubcomponentDeserialiser<Boolean>{
    private final String DICE_RULE_HEADER;
    private final String DICE_RULE_STRICT;
    private final String DICE_RULE_LENIENT;

    private boolean determined = false;
    private boolean ruleIsStrict = false;

    public IsDiceRuleStrictDeserialiser(String header, String strict, String lenient) {
        this.DICE_RULE_HEADER = header;
        this.DICE_RULE_STRICT = strict;
        this.DICE_RULE_LENIENT = lenient;
    }

    @Override
    public void loadNextLine(String line) {
        if (this.loadedObjectSuccessfully())
            return;

        List<String> linePieces = FileSystemUtil.splitString(line); 

        if (linePieces.size() != 2 || !linePieces.getFirst().equals(this.DICE_RULE_HEADER))
            return;

        if (linePieces.getLast().equals(this.DICE_RULE_STRICT)) {
            this.ruleIsStrict = true;
        } else if (linePieces.getLast().equals(this.DICE_RULE_LENIENT)) {
            this.ruleIsStrict = false;
        } else {
            return;
        }
        
        this.determined = true;
    }

    @Override
    public boolean loadedObjectSuccessfully() {
        return this.determined;
    }

    @Override
    public Boolean getObject() throws DeserialisedObjectNotCreatedException {
        if (!this.loadedObjectSuccessfully())
            throw new DeserialisedObjectNotCreatedException("Dice Rule Strictness not successfully created");

        return this.ruleIsStrict;
    }
}
