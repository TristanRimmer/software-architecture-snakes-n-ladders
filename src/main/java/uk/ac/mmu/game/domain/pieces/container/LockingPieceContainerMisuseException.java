package uk.ac.mmu.game.domain.pieces.container;

public class LockingPieceContainerMisuseException extends RuntimeException {
    public LockingPieceContainerMisuseException(String message) {
        super(message);
    }
}
