package uk.ac.mmu.game.domain.game.state;

import uk.ac.mmu.game.domain.game.Game;

public sealed interface GameState permits Ready, InPlay, GameOver {
    void execute(Game context);
    
    GameState progessState();

    boolean isEndOfChain();
}
