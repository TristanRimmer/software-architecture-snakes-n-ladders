package uk.ac.mmu.game.domain.events.types;

public sealed interface GameRelatedEvent permits BetterGameStateTransition, DiceRolled, TurnChange {}
