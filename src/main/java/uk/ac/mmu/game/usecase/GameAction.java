package uk.ac.mmu.game.usecase;

public interface GameAction {
    void run();

    boolean endOfSession();
}
