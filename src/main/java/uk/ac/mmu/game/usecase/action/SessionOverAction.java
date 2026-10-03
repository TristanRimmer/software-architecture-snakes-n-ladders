package uk.ac.mmu.game.usecase.action;

public class SessionOverAction implements GameAction {
    @Override
    public void run() {}

    @Override
    public boolean endOfSession() {
        return true;
    }
}
