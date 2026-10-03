package uk.ac.mmu.game.infrastructure.persistence.FileSystem.componentdeserialisers;

import java.util.ArrayList;
import java.util.List;

import uk.ac.mmu.game.domain.pieces.positiontrackers.PositionTrackingConverter;
import uk.ac.mmu.game.domain.util.ImplFactoryException;
import uk.ac.mmu.game.infrastructure.implmappers.PositionTrackingConverterMapper;
import uk.ac.mmu.game.infrastructure.persistence.FileSystem.FileSystemUtil;

public class PiecePositionTrackerDeserialiser implements GamSubcomponentDeserialiser<ArrayList<PositionTrackingConverter>> {
    private final String PIECES_HEADER;
    private final ArrayList<PositionTrackingConverter> converters = new ArrayList<>();

    private boolean headerRegistered = false;
    private int numPieces = -1;
    private boolean instancesCreated = false;

    public PiecePositionTrackerDeserialiser(String header) {
        this.PIECES_HEADER = header;
    }

    @Override
    public void loadNextLine(String line) {
        if (this.loadedObjectSuccessfully())
            return;

        List<String> linePieces = FileSystemUtil.splitString(line); 
        
        if (!headerRegistered) {
            if (linePieces.size() != 2 || !linePieces.getFirst().equals(this.PIECES_HEADER))
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
            try {
                this.converters.add(
                    PositionTrackingConverterMapper.getImplementationFromString(linePieces.getLast())
                );
            } catch (ImplFactoryException _e) {/* Implicit Return */}
            
            if (this.converters.size() == numPieces) {
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
    public ArrayList<PositionTrackingConverter> getObject() throws DeserialisedObjectNotCreatedException {
        if (!this.loadedObjectSuccessfully())
            throw new DeserialisedObjectNotCreatedException("Piece Position Tracking Converters not successfully created");

        return this.converters;
    }

}
