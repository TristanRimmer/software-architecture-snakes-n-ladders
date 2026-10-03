package uk.ac.mmu.game.domain.events.types;

import uk.ac.mmu.game.domain.game.state.GameState;

public record BetterGameStateTransition(Class<? extends GameState> state) implements GameEvent {}
