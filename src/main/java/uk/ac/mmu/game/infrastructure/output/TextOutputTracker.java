package uk.ac.mmu.game.infrastructure.output;

import java.util.ArrayList;

public class TextOutputTracker implements TextOutputHandler {
    ArrayList<String> lines;
    TextOutputHandler handler;

    public TextOutputTracker(TextOutputHandler handler) {
        this.lines = new ArrayList<>();
        this.handler = handler;
    }

    public ArrayList<String> getLines() {
        return this.lines;
    }
    
    @Override
    public void println(String text) {
        this.lines.add(text);
        this.handler.println(text);    
    }
}
