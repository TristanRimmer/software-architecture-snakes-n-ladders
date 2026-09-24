package uk.ac.mmu.game.pieces;

import uk.ac.mmu.game.shared.GridPosition;
import uk.ac.mmu.game.shared.MovementUtil;

public final class LowRightToUpLeftPiece implements PieceMovesetFromOrigin {
    private final int boardWidth;
    private final int boardHeight;

    // These are not indexes, however they are 0 indexed
    private int posX;
    private int posY;

    public LowRightToUpLeftPiece(int width, int height) {
        this.boardWidth = width;
        this.boardHeight = height;

        // TODO: Perhaps check the bounds are sensible? Might be an unecessaryily explicity level of validation

        this.posX = width - 1;
        this.posY = 0;
    }

    @Override
    public GridPosition moveForward(int dice_sum) {
        int traversalRemaining = dice_sum;
        int localPosX = posY % 2 != 0 ? this.posX : this.boardWidth - this.posX;

        // INFO: Assumptions were made here because the passed parameter should be unsigned
        while (traversalRemaining != 0) {
            MovementUtil.TraversalStatus traversalResult = MovementUtil.traverseHorizontal(localPosX, this.boardWidth, traversalRemaining);
            if (traversalResult.wrapped()) {
                this.posY += 1;
            }

            if (traversalResult.newPos() >= boardWidth) {
                // If the leftover is >= the boardWidth, traversing needs to happen again
                traversalRemaining = traversalResult.newPos();

                localPosX = 0;
            } else {
                // If not, the traversal is done and we know the remainder is from the left
                traversalRemaining = 0;

                this.posX = posY % 2 != 0 ? traversalResult.newPos() : this.boardWidth - traversalResult.newPos() - 1;
            }
        }

        return new GridPosition(this.posX, this.posY);
    }

    @Override
    // Intentionally forced to fit within the bounds of the domain
    // Nearly identical logic to the moveForward
    public GridPosition moveBackward(int back_step) {
        int traversalRemaining = back_step;

        int localPosX = posY % 2 != 0 ? this.posX : this.boardWidth - this.posX;

        while (traversalRemaining != 0) {
            MovementUtil.TraversalStatus traversalResult = MovementUtil.traverseHorizontal(localPosX, this.boardWidth, traversalRemaining);
            if (traversalResult.wrapped()) {
                // Decrease this time
                this.posY -= 1;
            }

            if (traversalResult.newPos() >= boardWidth) {
                traversalRemaining = traversalResult.newPos();

                localPosX = 0;
            } else {
                traversalRemaining = 0;

                this.posX = posY % 2 != 0 ? traversalResult.newPos() : this.boardWidth - traversalResult.newPos() - 1;
            }
        }

        this.posX = Math.max(0, posX);
        this.posY = Math.max(0, posY);

        return new GridPosition(this.posX, this.posY);
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
}
