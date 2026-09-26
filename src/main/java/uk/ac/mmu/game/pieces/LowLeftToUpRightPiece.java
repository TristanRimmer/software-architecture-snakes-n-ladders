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

        // Calculate position along the current row's traversal direction
        int localPosX = returnedPosY % 2 == 0
            ? returnedPosX
            : this.boardWidth - 1 - returnedPosX;

        while (traversalRemaining != 0) {
            MovementUtil.TraversalStatus traversalResult = MovementUtil.traverseBackwards(localPosX, this.boardWidth,traversalRemaining);

            traversalRemaining = traversalResult.placesLeftoverToMove();
            boolean wrappedAround = traversalResult.wrapped();

            if (wrappedAround) {
                returnedPosY -= 1;
            }

            if (traversalRemaining >= this.boardWidth) {
                localPosX = this.boardWidth - 1;
            } else {
                localPosX = this.boardWidth - 1 - traversalRemaining;

                traversalRemaining = 0;
            }
        }

        // Convert local traversal X back into the actual board X
        returnedPosX = returnedPosY % 2 == 0 ? localPosX : this.boardWidth - 1 - localPosX;

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
        // Calculate the local position on the current row
        int localPosX = this.posY % 2 == 0 ?  this.posX : this.boardWidth - 1 - this.posX;
        // Calculate the displacement of the piece along its serpentine/whatever the word is path
        int displacementOnPath = this.posY * this.boardWidth + localPosX;
        
        // The winning position is always going to be this
        int winningDisplacement = this.boardWidth * this.boardHeight - 1;

        int distance = displacementOnPath - winningDisplacement;

        GridPosition winningPoint = distance <= 0
            ? this.moveForward(Math.abs(distance))
            : this.moveBackward(Math.abs(distance));

        System.out.println(
            "Current Displacement along Path: " + displacementOnPath 
            + ". Winning Displacmenet is " + winningDisplacement 
            + ". Difference = " + distance 
            + ". This puts the winning point for this piece at " +
            winningPoint.toString()
        );

        return distance;

        // // To calculate the difference is not just a subtraction:
        // // - find the distance left on the current row in its direction
        // // - for any full rows left, its just the width

        // int fullRows = this.posY - (this.boardHeight - 1);
        // // board width -1 becasue the winning spot is the last point, not one after
        
        // // 4 Permutations here:
        // // If full rows is negative, we have not passed the end point and so we need to calculate 
        // // how far forward we need to go
        // // Otherwise, how far back we need to go -> this just affects whether 'Posy % 2' should equal 
        // // or not equal zero 
        // // This number should always be positive

        // int dstLeftOnCurrentRow = (fullRows <= 0) ? 
        //     (this.boardWidth - 1) - (this.posY % 2 == 0 ? this.posX : this.boardWidth - 1 - this.posX)
        //   : (this.boardWidth - 1) - (this.posY % 2 != 0 ? this.posX : this.boardWidth - 1 - this.posX);

        // int distance = Math.abs(fullRows) * this.boardWidth + dstLeftOnCurrentRow;
        
        // // If it was positive (has surpassed win condition), fullRows will always report at least one.
        // // If this is the case, that excess needs to be subtracted off.
        // distance = fullRows > 0 ? distance - this.boardWidth : distance;
            
        // GridPosition winningPoint = (fullRows <= 0) ? this.moveForward(distance) : this.moveBackward(distance);

        // System.out.println("Rows: " + fullRows + " curr row: " + dstLeftOnCurrentRow + ". THis puts the winning point for this piece at " + winningPoint.toString());
         
        // // Distance should be negative if it hasnt succeeded past its win point
        // return distance * (fullRows <= 0 ? -1 : 1); 
    }
}
