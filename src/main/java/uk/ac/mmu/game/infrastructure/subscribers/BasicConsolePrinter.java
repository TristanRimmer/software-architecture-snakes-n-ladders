package uk.ac.mmu.game.infrastructure.subscribers;

import uk.ac.mmu.game.domain.events.GameEventSubscriber;
import uk.ac.mmu.game.domain.events.types.BetterGameStateTransition;
import uk.ac.mmu.game.domain.events.types.DiceRolled;
import uk.ac.mmu.game.domain.events.types.GameEvent;
import uk.ac.mmu.game.domain.events.types.PieceMove;
import uk.ac.mmu.game.domain.game.state.GameOver;
import uk.ac.mmu.game.domain.game.state.GameState;
import uk.ac.mmu.game.domain.game.state.InPlay;
import uk.ac.mmu.game.domain.game.state.Ready;
import uk.ac.mmu.game.infrastructure.output.SystemOut;
import uk.ac.mmu.game.infrastructure.output.TextOutputHandler;

public class BasicConsolePrinter implements GameEventSubscriber {
    private final TextOutputHandler io = new SystemOut();
    private boolean inGameOverState = false;

    @Override
    public void notify(GameEvent state) {
        switch (state) {
            case DiceRolled dr -> this.io.println(
                    this.inGameOverState ?
                            "The Game is over; dice roll ignored!" :
                            "-> piece rolled a " + dr.diceRoll()
            );
            case PieceMove pm -> this.io.println(
                    "-> piece started at " + pm.oldPos() + " and ended at " + pm.newPos()
            );
            case BetterGameStateTransition bgst -> {
                Class<? extends GameState> newState = bgst.state();
                
                String stateAsString = switch(newState) {
                    case Class<?> c when c == Ready.class -> "Ready";
                    case Class<?> c when c == InPlay.class -> "In Play";
                    case Class<?> c when c == GameOver.class -> "Game Over";
                    default -> "Unreachable" /* GameState is a sealed interface -> this code will never be reached */;
                };
                
                System.out.println("|=====| " + stateAsString + " |=====|");
                
                this.inGameOverState = newState.equals(GameOver.class);
            } 
            default -> {}
        }
    }

}
