package uk.ac.mmu.game.domain.game.state;

import uk.ac.mmu.game.domain.board.Board;
import uk.ac.mmu.game.domain.events.GameEventPublisher;
import uk.ac.mmu.game.domain.events.types.ArbitraryHeader;
import uk.ac.mmu.game.domain.events.types.ArbitraryMessage;
import uk.ac.mmu.game.domain.game.Game;

public final class Ready implements GameState {

    @Override
    public void execute(Game context) {
        GameEventPublisher output = context.getEventPublisher();

        output.publish(new ArbitraryHeader("Game Information"));

        Board board = context.getConfig().board();

        int numberOfPlayers = context.getPieces().size();
        int boardWidth = board.getBoardWidth();
        int boardHeight = board.getBoardHeight();

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

    @Override
    public boolean isEndOfChain() {
        return false;
    }
}
