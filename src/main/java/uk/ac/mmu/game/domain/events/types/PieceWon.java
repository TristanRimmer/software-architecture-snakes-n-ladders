package uk.ac.mmu.game.domain.events.types;

import uk.ac.mmu.game.domain.pieces.Piece;

public record PieceWon(Piece piece, int pieceNum) implements GameEvent {}