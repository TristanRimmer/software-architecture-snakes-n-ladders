package uk.ac.mmu.game.infrastructure.persistence.FileSystem.componentdeserialisers;

import java.util.List;

import uk.ac.mmu.game.domain.board.Board;
import uk.ac.mmu.game.domain.board.GameBoard;
import uk.ac.mmu.game.infrastructure.persistence.FileSystem.FileSystemUtil;

public class BoardDeserialiser implements GamSubcomponentDeserialiser<Board> {
    private int boardWidth = -1;
    private int boardHeight = -1;

    private final Class<? extends Board> chosenImplementation;
    private final String BOARD_SIZE_HEADER;

    public BoardDeserialiser(Class<? extends Board> chosenImpl, String header) {
        this.chosenImplementation = chosenImpl;
        this.BOARD_SIZE_HEADER = header;
    }

    @Override
    public void loadNextLine(String line) {
        if (this.loadedObjectSuccessfully())
            return;

        List<String> linePieces = FileSystemUtil.splitString(line); 
        
        if (linePieces.size() != 3 || !linePieces.getFirst().equals(this.BOARD_SIZE_HEADER))
            return;

        this.boardWidth = FileSystemUtil.stringToUnsignedNonZeroInt(linePieces.get(1));
        this.boardHeight = FileSystemUtil.stringToUnsignedNonZeroInt(linePieces.get(2));
    }

    @Override
    public boolean loadedObjectSuccessfully() {
       return this.boardWidth > 0 && this.boardHeight > 0; 
    }

    @Override
    public Board getObject() throws DeserialisedObjectNotCreatedException {
        if (!this.loadedObjectSuccessfully())
            throw new DeserialisedObjectNotCreatedException("Board object was not successfully created");

        if (this.chosenImplementation == GameBoard.class)
            return new GameBoard(this.boardWidth, this.boardHeight);

        throw new DeserialisedObjectNotCreatedException("Board object implementation not supported");
    }
}
