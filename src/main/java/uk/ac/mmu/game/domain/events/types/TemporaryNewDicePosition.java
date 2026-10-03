package uk.ac.mmu.game.domain.events.types;

import uk.ac.mmu.game.domain.util.GridPosition;

public record TemporaryNewDicePosition(GridPosition temp) implements GameEvent {}
