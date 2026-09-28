package uk.ac.mmu.game.domain.events.types;

import uk.ac.mmu.game.domain.pieces.Piece;

public record PiecesHit(Piece pieceA, Piece pieceB) implements GameEvent {}