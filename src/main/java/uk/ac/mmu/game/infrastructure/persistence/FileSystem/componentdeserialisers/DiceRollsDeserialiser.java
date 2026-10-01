package uk.ac.mmu.game.infrastructure.persistence.FileSystem.componentdeserialisers;

import java.util.ArrayList;
import java.util.List;

import uk.ac.mmu.game.infrastructure.persistence.FileSystem.FileSystemUtil;

public class DiceRollsDeserialiser implements GamSubcomponentDeserialiser<ArrayList<Integer>> {
    private final String DICE_ROLLS_HEADER;
    private final ArrayList<Integer> rolls = new ArrayList<>();

    private boolean headerRegistered = false;
    private int numPieces = -1;
    private boolean instancesCreated = false;

    public DiceRollsDeserialiser(String header) {
        this.DICE_ROLLS_HEADER = header;
    }

    @Override
    public void loadNextLine(String line) {
        if (this.loadedObjectSuccessfully())
            return;

        List<String> linePieces = FileSystemUtil.splitString(line); 
        
        if (!headerRegistered) {
            if (linePieces.size() != 2 || !linePieces.getFirst().equals(this.DICE_ROLLS_HEADER))
                return;

            numPieces = FileSystemUtil.stringToUnsignedInt(linePieces.get(1));

            if (numPieces < 0) {
                return;
            } else if (numPieces == 0) {
                this.instancesCreated = true;
                return;                
            }

            this.headerRegistered = true;        
        } else {
            if (linePieces.size() != 1)
                return;

            // Head registered, 1 per line 
            int newRoll = FileSystemUtil.stringToUnsignedNonZeroInt(linePieces.getLast());

            if (newRoll <= 0)
                return;

            this.rolls.add(newRoll);
            
            if (this.rolls.size() == numPieces) {
                this.headerRegistered = false;
                this.instancesCreated = true;
            }
        } 
    }

    @Override
    public boolean loadedObjectSuccessfully() {
        return this.instancesCreated;
    }

    @Override
    public ArrayList<Integer> getObject() throws DeserialisedObjectNotCreatedException {
        if (!this.loadedObjectSuccessfully())
            throw new DeserialisedObjectNotCreatedException("DiceRoll List not created successfully");

        return this.rolls;
    }

}
