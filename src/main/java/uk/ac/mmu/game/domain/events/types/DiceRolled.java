package uk.ac.mmu.game.domain.events.types;

public record DiceRolled(int diceRoll) implements GameEvent {}
