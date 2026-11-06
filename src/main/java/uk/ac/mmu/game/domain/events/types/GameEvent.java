package uk.ac.mmu.game.domain.events.types;

public sealed interface GameEvent permits 
    ConfigurableStateRelatedEvent, 
    PieceRelatedEvent, 
    GameRelatedEvent {}

