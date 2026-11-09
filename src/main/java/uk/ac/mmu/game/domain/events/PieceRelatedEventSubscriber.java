package uk.ac.mmu.game.domain.events;

import uk.ac.mmu.game.domain.events.types.PieceRelatedEvent;

public interface PieceRelatedEventSubscriber {
    void notify(PieceRelatedEvent event);
    // void notify(PieceMove event);
    // void notify(PiecesHit event);
    // void notify(PieceWon event);
    // void notify(SpecialPositionMovedPiece event);
    // void notify(TemporaryNewPiecePosition event);
}
