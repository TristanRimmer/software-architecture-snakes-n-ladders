package uk.ac.mmu.game.domain.events.types;

import uk.ac.mmu.game.domain.pieces.PieceService;

public record PieceWon(PieceService piece, int pieceNum) implements GameEvent {}