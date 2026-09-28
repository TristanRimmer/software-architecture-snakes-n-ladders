package uk.ac.mmu.game.domain.events.types;

public record GameStateTransition(String newStateName) implements GameEvent {}
