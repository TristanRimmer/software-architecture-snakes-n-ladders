package uk.ac.mmu.game.observer.events;

import uk.ac.mmu.game.pieces.PieceService;

public record PiecesHit(PieceService pieceA, PieceService pieceB) implements GameEvent {}