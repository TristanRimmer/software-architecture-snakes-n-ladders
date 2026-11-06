package uk.ac.mmu.game.domain.events.types;

public sealed interface PieceRelatedEvent extends GameEvent permits 
    PieceMove, 
    PiecesHit, 
    PieceWon, 
    SpecialPositionMovedPiece, 
    TemporaryNewPiecePosition {}
