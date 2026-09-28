package uk.ac.mmu.game.domain.gamestate;

public sealed interface GameState permits Ready, InPlay, GameOver {
    void execute(Game context);
    
    GameState progessState();

    boolean isEndOfChain();
}
