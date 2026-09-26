package uk.ac.mmu.game.gamestate;

import uk.ac.mmu.game.output.StylisedPrinter;
import uk.ac.mmu.game.output.TextOutputHandler;

public final class Ready implements GameState {

    @Override
    public void execute(Game context) {
        TextOutputHandler output = context.getOutputHandler();

        StylisedPrinter.printBanner(output, "Game Information");

        int numberOfPlayers = context.getPieces().size();
        int boardWidth = context.getBoard().getBoardWidth();
        int boardHeight = context.getBoard().getBoardHeight();

        output.println("-> Number of Pieces/Players: " + numberOfPlayers);
        output.println("-> Board Dimensions: " + boardWidth + " x " + boardHeight);
        output.println("-> Piece Hit Rules: TO_STRING TODO");
        output.println("-> Win Condition Rules: TO_STRING TODO");
        output.println("-> Dice Set: TO_STRING TODO");
    }

    @Override
    public GameState progessState() {
        return new InPlay();    
    }
}
