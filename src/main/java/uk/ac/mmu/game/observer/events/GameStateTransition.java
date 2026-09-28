package uk.ac.mmu.game.observer.events;

public record GameStateTransition(String newStateName) implements GameEvent {}
