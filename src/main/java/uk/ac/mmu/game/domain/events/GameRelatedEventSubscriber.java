package uk.ac.mmu.game.domain.events;

import uk.ac.mmu.game.domain.events.types.BetterGameStateTransition;
import uk.ac.mmu.game.domain.events.types.DiceRolled;
import uk.ac.mmu.game.domain.events.types.PieceWon;
import uk.ac.mmu.game.domain.events.types.TurnChange;

public non-sealed interface GameRelatedEventSubscriber extends GameEventSubscriber {
    void notify(BetterGameStateTransition event);
    void notify(DiceRolled event);
    void notify(TurnChange event);
    void notify(PieceWon event);
}
