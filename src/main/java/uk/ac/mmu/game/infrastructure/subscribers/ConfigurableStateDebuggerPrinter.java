package uk.ac.mmu.game.infrastructure.subscribers;

import uk.ac.mmu.game.domain.events.ConfigurableStateRelatedEventSubscriber;
import uk.ac.mmu.game.domain.events.types.ConfigurableStateRelatedEvent;
import uk.ac.mmu.game.domain.events.types.HitConditionsPreventedMove;
import uk.ac.mmu.game.domain.events.types.WinConditionPreventedWin;

public final class ConfigurableStateDebuggerPrinter implements ConfigurableStateRelatedEventSubscriber{
    @Override
    public void notify(ConfigurableStateRelatedEvent event) {
        switch (event) {
            case HitConditionsPreventedMove ev -> this.action(ev);
            case WinConditionPreventedWin ev -> this.action(ev);
        }
    }

    private void action(HitConditionsPreventedMove event) {
        System.out.println("[DEBUG]: hit conditions prevent move.");
    }

    private void action(WinConditionPreventedWin event) {
        System.out.println("[DEBUG]: win conditions prevent win. piece now internally at " + event.piece().getCurrentPosition());
    }
}
