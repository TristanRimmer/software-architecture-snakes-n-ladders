package uk.ac.mmu.game.observer.events;

import uk.ac.mmu.game.shared.GridPosition;

public record PieceMove(GridPosition oldPos, GridPosition newPos) implements GameEvent {}
