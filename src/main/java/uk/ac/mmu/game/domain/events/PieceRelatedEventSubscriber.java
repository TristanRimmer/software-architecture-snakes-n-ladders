package uk.ac.mmu.game.domain.events;

import uk.ac.mmu.game.domain.events.types.PieceMove;
import uk.ac.mmu.game.domain.events.types.PieceWon;
import uk.ac.mmu.game.domain.events.types.PiecesHit;
import uk.ac.mmu.game.domain.events.types.SpecialPositionMovedPiece;
import uk.ac.mmu.game.domain.events.types.TemporaryNewPiecePosition;

public non-sealed interface PieceRelatedEventSubscriber extends GameEventSubscriber {
    void notify(PieceMove event);
    void notify(PiecesHit event);
    void notify(PieceWon event);
    void notify(SpecialPositionMovedPiece event);
    void notify(TemporaryNewPiecePosition event);
}
