package uk.ac.mmu.game.infrastructure.subscribers;

import uk.ac.mmu.game.domain.events.PieceRelatedEventSubscriber;
import uk.ac.mmu.game.domain.events.types.PieceMove;
import uk.ac.mmu.game.domain.events.types.PieceWon;
import uk.ac.mmu.game.domain.events.types.PiecesHit;
import uk.ac.mmu.game.domain.events.types.SpecialPositionMovedPiece;
import uk.ac.mmu.game.domain.events.types.TemporaryNewPiecePosition;

public class PieceRelatedEventPrinter implements PieceRelatedEventSubscriber {

    @Override
    public void notify(PieceMove event) {
        System.out.println("-> piece has moved from " + event.oldPos() + " to " + event.newPos());
    }

    @Override
    public void notify(PiecesHit event) {
        System.out.println("-> piece has hit another piece on the board!");
    }

    @Override
    public void notify(PieceWon event) {
        System.out.println("-> piece has won! It finished at " + event.piece().getCurrentPosition());
    }

    @Override
    public void notify(SpecialPositionMovedPiece event) {
        System.out.println("-> piece has landed on a teleporter!");
    }

    @Override
    public void notify(TemporaryNewPiecePosition event) {
        System.out.println("-> (internally, the piece is at " + event.temp() + ")");
    }

}
