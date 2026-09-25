package uk.ac.mmu.game.pieces;

import uk.ac.mmu.game.shared.GridPosition;
import uk.ac.mmu.game.shared.MovementUtil;

public final class LowLeftToUpRightPiece implements PieceMovesetFromOrigin {
    private final int boardWidth;
    // For clarity, its kept, but this may not be necessary
    private final int boardHeight;

    // These are not indexes, however they are 0 indexed
    private int posX;
    private int posY;

    public LowLeftToUpRightPiece(int width, int height) {
        this.boardWidth = width;
        this.boardHeight = height;

        this.posX = 0;
        this.posY = 0;
    }
    @Override
    // Intentionally not rounded to the board dimensions
    public GridPosition moveForward(int dice_sum) {
        int traversalRemaining = dice_sum;

        int returnedPosX = this.posX;
        int returnedPosY = this.posY;

        // Need to calculate how far along the current row the piece is, accounting for 0 indexing and direction
        // The -1 is here because without it you get off by 1 errors 
        // (it makes sense when converting from local traversal X to global X)
        int localPosX = returnedPosY % 2 == 0
            ? returnedPosX
            : this.boardWidth - 1 - returnedPosX;
        
        while (traversalRemaining != 0) {
            MovementUtil.TraversalStatus traversalResult = MovementUtil.traverseAlongLine(localPosX, this.boardWidth, traversalRemaining);

            traversalRemaining = traversalResult.placesLeftoverToMove();
            boolean wrappedAround = traversalResult.wrapped();

            if (wrappedAround) {
                returnedPosY += 1;
            }

            if (traversalRemaining >= this.boardWidth) {
                localPosX = 0;
            } else {
                localPosX = traversalRemaining;

                traversalRemaining = 0;
            }
        }

        // -1 for the same reason as localPosX
        returnedPosX = returnedPosY % 2 == 0
            ? localPosX
            : this.boardWidth - 1 - localPosX;

        return new GridPosition(returnedPosX, returnedPosY);
    }

    @Override
    // Intentionally forced to fit within the bounds of the domain
    // Nearly identical logic to the moveForward
    public GridPosition moveBackward(int back_step) {
        int traversalRemaining = back_step;

        int returnedPosX = this.posX;
        int returnedPosY = this.posY;

        // Need to calculate how far along the current row the piece is, accounting for 0 indexing and direction
        // The -1 is here because without it you get off by 1 errors 
        // (it makes sense when converting from local traversal X to global X)
        int localPosX = returnedPosY % 2 == 0
            ? returnedPosX
            : this.boardWidth - 1 - returnedPosX;
        
        while (traversalRemaining != 0) {
            MovementUtil.TraversalStatus traversalResult = MovementUtil.traverseAlongLine(localPosX, this.boardWidth, traversalRemaining);

            traversalRemaining = traversalResult.placesLeftoverToMove();
            boolean wrappedAround = traversalResult.wrapped();

            if (wrappedAround) {
                returnedPosY -= 1;
            }

            if (traversalRemaining >= this.boardWidth) {
                localPosX = 0;
            } else {
                localPosX = traversalRemaining;

                traversalRemaining = 0;
            }
        }

        // -1 for the same reason as localPosX
        returnedPosX = returnedPosY % 2 == 0
            ? localPosX
            : this.boardWidth - 1 - localPosX;


        returnedPosX = Math.max(0, returnedPosX);
        returnedPosY = Math.max(0, returnedPosY);

        return new GridPosition(returnedPosX, returnedPosY);
    }

    @Override
    public void set_position(GridPosition new_position) {
        this.posX = new_position.x();
        this.posY = new_position.y();
    }
    @Override
    public GridPosition getPosition() {
        return new GridPosition(this.posX, this.posY);
    }
    @Override
    public int distanceFromWinningSpot() {
        int winningSpot = this.boardWidth * this.boardHeight;

        int currentGlobalIndex = (this.boardWidth * this.posY) + this.posX;

        return currentGlobalIndex - winningSpot;
    }
}
