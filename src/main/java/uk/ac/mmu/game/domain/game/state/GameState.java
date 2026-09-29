package uk.ac.mmu.game.domain.game.state;

import uk.ac.mmu.game.domain.game.Game;

public sealed interface GameState permits Ready, InPlay, GameOver {
    GameState execute(Game context);

    boolean isEndOfChain();
}
