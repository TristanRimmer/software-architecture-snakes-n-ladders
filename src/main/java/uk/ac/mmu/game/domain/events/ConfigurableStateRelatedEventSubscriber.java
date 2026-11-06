package uk.ac.mmu.game.domain.events;

import uk.ac.mmu.game.domain.events.types.HitConditionsPreventedMove;
import uk.ac.mmu.game.domain.events.types.SpecialPositionMovedPiece;
import uk.ac.mmu.game.domain.events.types.WinConditionPreventedWin;

public non-sealed interface ConfigurableStateRelatedEventSubscriber extends GameEventSubscriber {
    void notify(HitConditionsPreventedMove event);
    void notify(WinConditionPreventedWin event);
    void notify(SpecialPositionMovedPiece event);
}
