package uk.ac.mmu.game.domain.pieces;

import uk.ac.mmu.game.domain.pieces.positiontrackers.PositionTrackingConverter;
import uk.ac.mmu.game.domain.util.BoardDimension;
import uk.ac.mmu.game.domain.util.GridPosition;

public class GamePiece implements Piece {
    PositionTrackingConverter converter;
    BoardDimension gridWidth;
    BoardDimension gridHeight;

    // TODO: work on a value object for this that forces it to always be at least 0
    int currentDisplacement;

    public GamePiece(PositionTrackingConverter converter, 
                     BoardDimension gridWidth,
                     BoardDimension gridHeight) {
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
                this.gridHeight);
    }

    @Override
    public void setPosition(GridPosition newPos) {
        this.currentDisplacement = converter.gridPositionToDisplacement(
                newPos,
                this.gridWidth,
                this.gridHeight);
    }

    @Override
    public GridPosition getCurrentPosition() {
        return converter.displacementToGridPosition(
                this.currentDisplacement,
                this.gridWidth,
                this.gridHeight);
    }

    @Override
    public int getDisplacement() {
        return this.currentDisplacement;
    }
}
