package uk.ac.mmu.game.domain.rules.hitcondition;

// TODO: rename to something more suggestive of it being whether a piece moved or not
// Ultimately what it boils down to though is that regardless of the hit condition implementation,
// A piece either moves, or it doesn't
public enum CollisionStatus {
    MOVEHAPPENED,
    MOVEDIDNTHAPPEN,
}
