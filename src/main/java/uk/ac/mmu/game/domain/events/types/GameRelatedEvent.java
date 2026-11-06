package uk.ac.mmu.game.domain.events.types;

public sealed interface GameRelatedEvent extends GameEvent permits BetterGameStateTransition, DiceRolled, TurnChange, PieceWon {}
