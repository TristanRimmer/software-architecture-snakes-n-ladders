package uk.ac.mmu.game.infrastructure.subscribers;

import uk.ac.mmu.game.domain.events.GameRelatedEventSubscriber;
import uk.ac.mmu.game.domain.events.types.BetterGameStateTransition;
import uk.ac.mmu.game.domain.events.types.DiceRolled;
import uk.ac.mmu.game.domain.events.types.GameRelatedEvent;
import uk.ac.mmu.game.domain.events.types.TurnChange;
import uk.ac.mmu.game.domain.game.state.GameOver;
import uk.ac.mmu.game.domain.game.state.GameState;
import uk.ac.mmu.game.domain.game.state.InPlay;
import uk.ac.mmu.game.domain.game.state.Ready;
import uk.ac.mmu.game.infrastructure.output.SystemOut;
import uk.ac.mmu.game.infrastructure.output.TextOutputHandler;

public final class GameRelatedEventPrinter implements GameRelatedEventSubscriber {
    private final TextOutputHandler io = new SystemOut();
    private boolean inGameOverState = false;

    @Override
    public void notify(GameRelatedEvent event) {
        switch (event) {
            case BetterGameStateTransition ev -> this.action(ev);
            case DiceRolled ev -> this.action(ev);
            case TurnChange ev -> {} 
        }
    }

    private void action(BetterGameStateTransition event) {
        Class<? extends GameState> newState = event.state();
        
        String stateAsString = switch(newState) {
            case Class<?> c when c == Ready.class -> "Ready";
            case Class<?> c when c == InPlay.class -> "In Play";
            case Class<?> c when c == GameOver.class -> "Game Over";
            default -> "Unreachable" /* GameState is a sealed interface -> this code will never be reached */;
        };
        
        System.out.println("|=====| " + stateAsString + " |=====|");
        
        this.inGameOverState = newState.equals(GameOver.class);
    }

    private void action(DiceRolled event) {
        System.out.println(this.inGameOverState ?
                "The Game is over; dice roll ignored!" :
                "-> piece rolled a " + event.diceRoll());
    }
}
