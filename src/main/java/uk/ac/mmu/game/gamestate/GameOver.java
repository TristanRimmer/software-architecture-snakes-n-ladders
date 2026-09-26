package uk.ac.mmu.game.gamestate;

import uk.ac.mmu.game.output.StylisedPrinter;

public final class GameOver implements GameState {

    @Override
    public void execute(Game context) {
                
        StylisedPrinter.printBanner(context.getOutputHandler(), "Game Over");

        context.getOutputHandler().println("TODO: Gathering data from the game just gone..");
    }

    @Override
    public GameState progessState() {
        return new Ready();
    }

}
