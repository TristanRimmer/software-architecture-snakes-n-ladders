package uk.ac.mmu.game.domain.board.specialpositions;

import java.util.List;

import uk.ac.mmu.game.domain.pieces.Piece;

public interface SpecialPositionContainer {
    public enum TeleporterRegisterStatus {
        ACCEPTED,
        REJECTED
    }
    public enum SpecialPositionStatus {
        NOTHING_HAPPENED,
        PIECE_TELEPORTED,
    }

    SpecialPositionStatus evaluatePieceHittingSpecialPositions(Piece piece);

    TeleporterRegisterStatus registerNewSpecialPosition(SpecialLinkedPositions newPosition);

    List<SpecialLinkedPositions> getSpecialPositions();
}
