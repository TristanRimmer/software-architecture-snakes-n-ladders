package uk.ac.mmu.game.infrastructure.subscribers;

import uk.ac.mmu.game.domain.events.GameRelatedEventSubscriber;
import uk.ac.mmu.game.domain.events.types.BetterGameStateTransition;
import uk.ac.mmu.game.domain.events.types.DiceRolled;
import uk.ac.mmu.game.domain.events.types.PieceWon;
import uk.ac.mmu.game.domain.events.types.TurnChange;
import uk.ac.mmu.game.domain.game.state.GameOver;
import uk.ac.mmu.game.domain.game.state.GameState;
import uk.ac.mmu.game.domain.game.state.InPlay;
import uk.ac.mmu.game.domain.game.state.Ready;
import uk.ac.mmu.game.infrastructure.output.SystemOut;
import uk.ac.mmu.game.infrastructure.output.TextOutputHandler;

public class GameRelatedEventPrinter implements GameRelatedEventSubscriber {
    private final TextOutputHandler io = new SystemOut();
    private boolean inGameOverState = false;

    @Override
    public void notify(BetterGameStateTransition event) {
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

    @Override
    public void notify(DiceRolled event) {
        System.out.println(this.inGameOverState ?
                "The Game is over; dice roll ignored!" :
                "-> piece rolled a " + event.diceRoll());
    }

    @Override
    public void notify(PieceWon event) {
        io.println("-> This piece has won the game at " + event.piece().getCurrentPosition());
    }

    @Override
    public void notify(TurnChange event) {}
}
