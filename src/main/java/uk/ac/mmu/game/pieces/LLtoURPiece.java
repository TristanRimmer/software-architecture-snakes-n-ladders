package uk.ac.mmu.game.pieces;

import uk.ac.mmu.game.shared.GridPosition;

public final class LLtoURPiece implements PieceMovesetFromOrigin {
    int gridWidth;
    int gridHeight;
    int displacement;

    public LLtoURPiece(int gW, int gH) {
        this.gridWidth = gW;
        this.gridHeight = gH;

        this.displacement = 0;
    }
    @Override
    public GridPosition moveForward(int dice_sum) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'moveForward'");
    }

    @Override
    public GridPosition moveBackward(int back_step) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'moveBackward'");
    }

    @Override
    public GridPosition getPosition() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getPosition'");
    }

    @Override
    public void set_position(GridPosition new_position) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'set_position'");
    }

    @Override
    public int distanceFromWinningSpot() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'distanceFromWinningSpot'");
    }

}
