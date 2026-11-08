package uk.ac.mmu.game.infrastructure.subscribers;

import uk.ac.mmu.game.domain.events.ConfigurableStateRelatedEventSubscriber;
import uk.ac.mmu.game.domain.events.types.HitConditionsPreventedMove;
import uk.ac.mmu.game.domain.events.types.SpecialPositionMovedPiece;
import uk.ac.mmu.game.domain.events.types.WinConditionPreventedWin;

public class ConfigurableStateDebuggerPrinter implements ConfigurableStateRelatedEventSubscriber{

    @Override
    public void notify(HitConditionsPreventedMove event) {
        System.out.println("[DEBUG]: hit conditions prevent move.");
    }

    @Override
    public void notify(WinConditionPreventedWin event) {
        System.out.println("[DEBUG]: win conditions prevent win. piece now internally at " + event.piece().getCurrentPosition());
    }

    @Override
    public void notify(SpecialPositionMovedPiece event) {
        System.out.println("[DEBUG]: teleporter moved piece.");
    }

}
