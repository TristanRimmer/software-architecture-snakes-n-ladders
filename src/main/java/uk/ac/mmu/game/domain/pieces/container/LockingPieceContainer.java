package uk.ac.mmu.game.domain.pieces.container;

import java.util.ArrayList;
import java.util.List;

import uk.ac.mmu.game.domain.pieces.Piece;
import uk.ac.mmu.game.domain.util.GridPosition;


/**
 * LockingPieceContainer - contains a list of the registered pieces, also locking once play has begun so no more pieces can be registered.
 * 
 * If the user attempts to register a new piece with this impl, it throws a runtime error
 */
public class LockingPieceContainer implements PieceContainer {
    private final List<Piece> pieces;
    private boolean inPlay;
    private int currentPieceIndex;

    public LockingPieceContainer() {
        this.pieces = new ArrayList<>();
        this.inPlay = false;
        this.currentPieceIndex = 0;
    }
    @Override 
    public void registerNewPiece(Piece newPiece) {
        if (!this.inPlay) {
            this.pieces.add(newPiece);
            return;
        }
        throw new LockingPieceContainerMisuseException(
            "Tried to register a new piece after In-Play methods were invoked. Use/Create an alternative if this wasn't an accident");
    }
    @Override 
    public Piece getNextPiece() {
        // The moment this function has been invoked, lock the pieces list
        this.inPlay = true;

        Piece piece = this.currentPieceFromList();

        this.currentPieceIndex++;

        return piece;
    }

    @Override
    // This uses the internal tracker to automatically choose which position should be omitted and returns the other positions
    public List<GridPosition> getOtherPiecesPositions() {
        List<GridPosition> positions = new ArrayList<>();
        Piece currentPiece = this.currentPieceFromList();

        for (Piece piece : this.pieces) {
            if (!(piece == currentPiece))
                positions.add(piece.getCurrentPosition());
        }

        return positions;
    }

    // Done purely to adhere to DRY
    private Piece currentPieceFromList() {
        return this.pieces.get(this.currentPieceIndex % this.pieces.size());
    }

    @Override
    public int getNumPieces() {
        return this.pieces.size();
    }

}
