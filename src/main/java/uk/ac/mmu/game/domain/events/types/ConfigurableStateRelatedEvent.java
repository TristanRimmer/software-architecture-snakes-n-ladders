package uk.ac.mmu.game.domain.events.types;

public sealed interface ConfigurableStateRelatedEvent extends GameEvent permits HitConditionsPreventedMove, WinConditionPreventedWin, SpecialPositionMovedPiece {}
