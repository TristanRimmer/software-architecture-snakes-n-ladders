package uk.ac.mmu.game.domain.events.types;

public record TurnChange(int pieceNumber) implements GameEvent {}
