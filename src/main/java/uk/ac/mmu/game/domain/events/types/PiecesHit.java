package uk.ac.mmu.game.domain.events.types;

import uk.ac.mmu.game.domain.pieces.PieceService;

public record PiecesHit(PieceService pieceA, PieceService pieceB) implements GameEvent {}