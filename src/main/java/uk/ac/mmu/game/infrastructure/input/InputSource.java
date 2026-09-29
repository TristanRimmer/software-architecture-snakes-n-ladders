package uk.ac.mmu.game.infrastructure.input;

import uk.ac.mmu.game.infrastructure.output.TextOutputHandler;

public interface InputSource {
    String getNextLine();
    int getNextInt(String failMessage, TextOutputHandler output);
}
