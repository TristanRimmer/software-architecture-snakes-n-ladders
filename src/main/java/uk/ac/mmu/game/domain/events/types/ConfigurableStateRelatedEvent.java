package uk.ac.mmu.game.domain.events.types;

public sealed interface ConfigurableStateRelatedEvent permits HitConditionsPreventedMove, WinConditionPreventedWin {}
