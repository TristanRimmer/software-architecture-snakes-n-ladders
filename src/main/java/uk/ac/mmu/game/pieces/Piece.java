package uk.ac.mmu.game.pieces;

import uk.ac.mmu.game.shared.GridPosition;

public class Piece implements PieceService {
    PositionTrackingConverter converter;

    int gridWidth;
    int gridHeight;
    // This can always be formed from gridWidth and Height but its nice to have a copy 
    int winningDisplacement;

    int currentDisplacement;

    public Piece(PositionTrackingConverter converter, int gridWidth, int gridHeight) {
        this.converter = converter;
        this.gridWidth = gridWidth;
        this.gridHeight = gridHeight;
        this.winningDisplacement = gridWidth * gridHeight - 1;
        this.currentDisplacement = 0;
    }
    /*
        Methods from PieceMoveset - used by the service that handles the turns of each piece
    */
    @Override
    public GridPosition proposeNewPosition(int increment) {
        return converter.displacementToGridPosition(
            this.currentDisplacement + increment, 
            this.gridWidth, 
            this.gridHeight
        );
    }

    @Override
    public void setPosition(GridPosition newPos) {
        this.currentDisplacement = converter.gridPositionToDisplacement(
            newPos, 
            this.gridWidth,
            this.gridHeight
        );       
    }

    @Override
    public GridPosition getCurrentPosition() {
        return converter.displacementToGridPosition(
            this.currentDisplacement, 
            this.gridWidth,
            this.gridHeight
        );
    }

    /*
        Methods from WinConditionCheckingMethods - used by the service which checks whether a piece has won
    */
    @Override
    public int getDisplacementFromWinning() {
        return this.currentDisplacement - this.winningDisplacement;
    }

}
