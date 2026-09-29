package uk.ac.mmu.game.domain.pieces.container;

import java.util.List;

import uk.ac.mmu.game.domain.pieces.Piece;
import uk.ac.mmu.game.domain.util.GridPosition;

public interface PieceContainer {
    void registerNewPiece(Piece newPiece);
    Piece getNextPiece();
    List<GridPosition> getOtherPiecesPositions();
    int getNumPieces();
}
