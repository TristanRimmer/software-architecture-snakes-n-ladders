package uk.ac.mmu.game.infrastructure.subscribers;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import uk.ac.mmu.game.domain.events.GameRelatedEventSubscriber;
import uk.ac.mmu.game.domain.events.types.BetterGameStateTransition;
import uk.ac.mmu.game.domain.events.types.GameRelatedEvent;
import uk.ac.mmu.game.domain.events.types.TurnChange;
import uk.ac.mmu.game.infrastructure.output.StylisedPrinter;
import uk.ac.mmu.game.infrastructure.output.SystemOut;
import uk.ac.mmu.game.infrastructure.output.TextOutputHandler;

public final class TurnsTracker implements GameRelatedEventSubscriber {
    private final Map<Integer, Integer> numberOfTurnsPerPiece;
    private final TextOutputHandler stylisedPrinterIOMechanism = new SystemOut();

    private static final List<String> colours = List.of(
        "\u001B[31m", // Red
        "\u001B[34m", // Blue
        "\u001B[32m", // Green/Yellow I dont remember 
        "\u001B[33m", // The opposite of above 
        "\u001B[0m" // Clear
    );

    public TurnsTracker() {
        this.numberOfTurnsPerPiece = new LinkedHashMap<>();
    }


    @Override
    public void notify(GameRelatedEvent event) {
        switch (event) {
            case BetterGameStateTransition ev -> this.action(ev);
            case TurnChange ev -> this.action(ev);
            default -> {}
        }
    }

    private void action(BetterGameStateTransition event) {
        StylisedPrinter.printSubheading(this.stylisedPrinterIOMechanism, "Turns Breakdown");
        int runningTotal = 0;

        for (Integer key : this.numberOfTurnsPerPiece.keySet()) {
            this.stylisedPrinterIOMechanism.println(
                "-> Piece " + key + " had " + this.numberOfTurnsPerPiece.get(key) + " turns");
            runningTotal += this.numberOfTurnsPerPiece.get(key);
        }
        this.stylisedPrinterIOMechanism.println("-> In total, there were " + runningTotal + " turns this game.");
    }

    private void action(TurnChange event) {
        int pieceNumber = event.pieceNumber();

        String colour = (pieceNumber <= 4) ? colours.get(pieceNumber) : colours.getLast();
        StylisedPrinter.printSubheading(stylisedPrinterIOMechanism, colour + "Piece " + pieceNumber + colours.getLast() + "'s Turn");

        if (!this.numberOfTurnsPerPiece.containsKey(pieceNumber))
            this.numberOfTurnsPerPiece.put(pieceNumber, 0);
        
        Integer currentScore = this.numberOfTurnsPerPiece.get(pieceNumber);

        this.numberOfTurnsPerPiece.put(pieceNumber, currentScore + 1);

        this.stylisedPrinterIOMechanism.println("-> Number of turns so far: " + (currentScore + 1));
    }
}
