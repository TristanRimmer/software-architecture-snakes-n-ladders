package uk.ac.mmu.game.usecase.action;

public interface GameAction {
    void run();

    boolean endOfSession();
}
