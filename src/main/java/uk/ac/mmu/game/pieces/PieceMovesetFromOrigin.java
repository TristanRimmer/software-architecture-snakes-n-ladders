package uk.ac.mmu.game.pieces;

import uk.ac.mmu.game.shared.GridPosition;

// TODO: the forward and backward should take in Unsigned due to their 1D -> 2D behaviour
public sealed interface PieceMovesetFromOrigin permits LowLeftToUpRightPiece, LowRightToUpLeftPiece, UpLeftToLowRightPiece, UpRightToLowLeftPiece {
    // Concept 1
    GridPosition moveForward(int dice_sum);
    GridPosition moveBackward(int back_step);

    GridPosition getPosition();
    void set_position(GridPosition new_position);
}
