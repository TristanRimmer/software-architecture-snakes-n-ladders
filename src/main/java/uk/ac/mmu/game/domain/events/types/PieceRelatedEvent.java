package uk.ac.mmu.game.domain.events.types;

public sealed interface PieceRelatedEvent permits 
    PieceMove, 
    PiecesHit, 
    PieceWon, 
    SpecialPositionMovedPiece, 
    TemporaryNewPiecePosition {}
