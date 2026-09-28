package uk.ac.mmu.game.observer.events;

import uk.ac.mmu.game.pieces.PieceService;

public record PieceWon(PieceService piece, int pieceNum) implements GameEvent {}