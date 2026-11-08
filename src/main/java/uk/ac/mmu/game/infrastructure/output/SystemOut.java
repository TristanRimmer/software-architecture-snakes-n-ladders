package uk.ac.mmu.game.infrastructure.output;

public class SystemOut implements TextOutputHandler {
    @Override
    public void println(String text) {
        System.out.println(text);    
    }
}
