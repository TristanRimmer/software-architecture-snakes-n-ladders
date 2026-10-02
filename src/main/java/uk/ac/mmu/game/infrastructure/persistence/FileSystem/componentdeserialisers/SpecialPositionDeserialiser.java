package uk.ac.mmu.game.infrastructure.persistence.FileSystem.componentdeserialisers;

import java.util.ArrayList;
import java.util.List;

import uk.ac.mmu.game.domain.board.specialpositions.SpecialLinkedPositions;
import uk.ac.mmu.game.domain.util.GridPosition;
import uk.ac.mmu.game.domain.util.ImplFactoryException;
import uk.ac.mmu.game.infrastructure.persistence.FileSystem.FileSystemUtil;
import uk.ac.mmu.game.infrastructure.serialisation.SpecialPositionSerialiser;

public class SpecialPositionDeserialiser implements GamSubcomponentDeserialiser<ArrayList<SpecialLinkedPositions>>{
    private final String SPECIAL_POSITION_HEADER;
    private final ArrayList<SpecialLinkedPositions> positions = new ArrayList<>();

    private boolean headerRegistered = false;
    private int numPieces = -1;
    private boolean instancesCreated = false;

    public SpecialPositionDeserialiser(String header) {
        this.SPECIAL_POSITION_HEADER = header;
    }

    @Override
    public void loadNextLine(String line) {
        if (this.loadedObjectSuccessfully())
            return;

        List<String> linePieces = FileSystemUtil.splitString(line); 
        
        if (!headerRegistered) {
            if (linePieces.size() != 2 || !linePieces.getFirst().equals(this.SPECIAL_POSITION_HEADER))
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
            if (linePieces.size() != 5)
                return;

            System.out.println(line);

            int x1 = FileSystemUtil.stringToUnsignedInt(linePieces.get(1));
            int y1 = FileSystemUtil.stringToUnsignedInt(linePieces.get(2));
            int x2 = FileSystemUtil.stringToUnsignedInt(linePieces.get(3));
            int y2 = FileSystemUtil.stringToUnsignedInt(linePieces.get(4));

            if (x1 < 0 || y1 < 0 || x2 < 0 || y2 < 0)
                return;

            // Head registered, 1 per line 
            try {
                this.positions.add(
                    SpecialPositionSerialiser.getImplementationFromString(
                        linePieces.get(0), 
                        new GridPosition(x1, y1), 
                        new GridPosition(x2, y2))
                );
            } catch (ImplFactoryException _e) {/* Implicit Return */}
            
            if (this.positions.size() == numPieces) {
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
    public ArrayList<SpecialLinkedPositions> getObject() throws DeserialisedObjectNotCreatedException {
        if (!this.loadedObjectSuccessfully())
            throw new DeserialisedObjectNotCreatedException("Special Position List not created successfully");

        return this.positions;
    }

}
