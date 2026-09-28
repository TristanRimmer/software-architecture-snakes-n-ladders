package uk.ac.mmu.game.domain.pieces;

import uk.ac.mmu.game.domain.pieces.positiontrackers.PositionTrackingConverter;
import uk.ac.mmu.game.domain.util.GridPosition;

public class GamePiece implements Piece {
    PositionTrackingConverter converter;

    int gridWidth;
    int gridHeight;

    int currentDisplacement;

    public GamePiece(PositionTrackingConverter converter, int gridWidth, int gridHeight) {
        this.converter = converter;
        this.gridWidth = gridWidth;
        this.gridHeight = gridHeight;
        this.currentDisplacement = 0;
    }

    @Override
    public GridPosition move(int increment) {
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

    @Override
    public int getDisplacement() {
        return this.currentDisplacement;
    }

}
