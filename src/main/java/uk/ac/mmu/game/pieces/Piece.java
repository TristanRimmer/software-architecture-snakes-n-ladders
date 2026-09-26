package uk.ac.mmu.game.pieces;

import uk.ac.mmu.game.shared.GridPosition;

public class Piece implements PieceService {
    PositionTrackingConverter converter;

    int gridWidth;
    int gridHeight;

    int currentDisplacement;

    public Piece(PositionTrackingConverter converter, int gridWidth, int gridHeight) {
        this.converter = converter;
        this.gridWidth = gridWidth;
        this.gridHeight = gridHeight;
        this.currentDisplacement = 0;
    }
    /*
        Methods from PieceMoveset - used by the service that handles the turns of each piece
    */
    @Override
    public GridPosition move(int increment) {
        //System.out.println("Displ before: " + this.currentDisplacement + ", adding: " + increment);
        this.currentDisplacement += increment;

        return converter.displacementToGridPosition(
            this.currentDisplacement, 
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
    public int getDisplacementAsScalar() {
        return this.currentDisplacement;
    }

}
