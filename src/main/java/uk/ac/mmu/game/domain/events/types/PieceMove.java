package uk.ac.mmu.game.domain.events.types;

import uk.ac.mmu.game.domain.shared.GridPosition;

public record PieceMove(GridPosition oldPos, GridPosition newPos) implements GameEvent {}
