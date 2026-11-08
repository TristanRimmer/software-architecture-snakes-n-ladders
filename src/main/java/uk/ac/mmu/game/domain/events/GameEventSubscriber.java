package uk.ac.mmu.game.domain.events;

public sealed interface GameEventSubscriber permits 
    GameRelatedEventSubscriber, 
    PieceRelatedEventSubscriber, 
    ConfigurableStateRelatedEventSubscriber {}

