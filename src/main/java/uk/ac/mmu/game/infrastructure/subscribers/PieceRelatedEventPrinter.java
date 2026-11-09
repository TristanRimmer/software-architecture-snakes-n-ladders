package uk.ac.mmu.game.infrastructure.subscribers;

import uk.ac.mmu.game.domain.events.PieceRelatedEventSubscriber;
import uk.ac.mmu.game.domain.events.types.PieceMove;
import uk.ac.mmu.game.domain.events.types.PieceRelatedEvent;
import uk.ac.mmu.game.domain.events.types.PieceWon;
import uk.ac.mmu.game.domain.events.types.PiecesHit;
import uk.ac.mmu.game.domain.events.types.SpecialPositionMovedPiece;
import uk.ac.mmu.game.domain.events.types.TemporaryNewPiecePosition;

public final class PieceRelatedEventPrinter implements PieceRelatedEventSubscriber {

    @Override
    public void notify(PieceRelatedEvent event) {
        switch (event) {
            case PieceMove ev -> this.action(ev);
            case PiecesHit ev -> this.action(ev);
            case PieceWon ev -> this.action(ev);
            case SpecialPositionMovedPiece ev-> this.action(ev);
            case TemporaryNewPiecePosition ev -> this.action(ev);
        }
    }

    private void action(PieceMove event) {
        System.out.println("-> piece has moved from " + event.oldPos() + " to " + event.newPos());
    }

    // The args are unused but allow method overloading
    private void action(PiecesHit event) {
        System.out.println("-> piece has hit another piece on the board!");
    }
    private void action(SpecialPositionMovedPiece event) {
        System.out.println("-> piece has landed on a teleporter!");
    }

    private void action(PieceWon event) {
        System.out.println("-> piece has won! It finished at " + event.piece().getCurrentPosition());
    }

    private void action(TemporaryNewPiecePosition event) {
        System.out.println("-> (internally, the piece is at " + event.temp() + ")");
    }
}
