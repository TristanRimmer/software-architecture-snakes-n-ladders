package uk.ac.mmu.game.gamestate;

import uk.ac.mmu.game.observer.GameEventPublisher;
import uk.ac.mmu.game.observer.events.ArbitraryHeader;
import uk.ac.mmu.game.observer.events.ArbitraryMessage;

public final class Ready implements GameState {

    @Override
    public void execute(Game context) {
        GameEventPublisher output = context.getEventPublisher();

        output.publish(new ArbitraryHeader("Game Information"));

        int numberOfPlayers = context.getPieces().size();
        int boardWidth = context.getBoard().getBoardWidth();
        int boardHeight = context.getBoard().getBoardHeight();

        output.publish(new ArbitraryMessage("-> Number of Pieces/Players: " + numberOfPlayers));
        output.publish(new ArbitraryMessage("-> Board Dimensions: " + boardWidth + " x " + boardHeight));
        output.publish(new ArbitraryMessage("-> Piece Hit Rules: TO_STRING TODO"));
        output.publish(new ArbitraryMessage("-> Win Condition Rules: TO_STRING TODO"));
        output.publish(new ArbitraryMessage("-> Dice Set: TO_STRING TODO"));
    }

    @Override
    public GameState progessState() {
        return new InPlay();    
    }
}
