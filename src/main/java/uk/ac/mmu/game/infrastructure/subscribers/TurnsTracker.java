package uk.ac.mmu.game.infrastructure.subscribers;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import uk.ac.mmu.game.domain.events.GameEventSubscriber;
import uk.ac.mmu.game.domain.events.types.BetterGameStateTransition;
import uk.ac.mmu.game.domain.events.types.GameEvent;
import uk.ac.mmu.game.domain.events.types.TurnChange;
import uk.ac.mmu.game.domain.game.state.GameOver;
import uk.ac.mmu.game.infrastructure.output.StylisedPrinter;
import uk.ac.mmu.game.infrastructure.output.SystemOut;
import uk.ac.mmu.game.infrastructure.output.TextOutputHandler;

public class TurnsTracker implements GameEventSubscriber {
    private final Map<Integer, Integer> numberOfTurnsPerPiece;
    private final TextOutputHandler stylisedPrinterIOMechanism = new SystemOut();

    private static final List<String> colours = List.of(
        "\u001B[31m",
        "\u001B[34m",
        "\u001B[32m",
        "\u001B[33m",
        "\u001B[0m"
    );

    public TurnsTracker() {
        this.numberOfTurnsPerPiece = new LinkedHashMap<>();
    }
    @Override
    public void notify(GameEvent state) {
        if (state instanceof TurnChange(int pieceNumber)) {
            String colour = (pieceNumber <= 4) ? colours.get(pieceNumber) : colours.getLast();
            StylisedPrinter.printSubheading(stylisedPrinterIOMechanism, colour + "Piece " + pieceNumber + colours.getLast() + "'s Turn");

            if (!this.numberOfTurnsPerPiece.containsKey(pieceNumber))
                this.numberOfTurnsPerPiece.put(pieceNumber, 0);
            
            Integer currentScore = this.numberOfTurnsPerPiece.get(pieceNumber);

            this.numberOfTurnsPerPiece.put(pieceNumber, currentScore + 1);

            this.stylisedPrinterIOMechanism.println("-> Number of turns so far: " + (currentScore + 1));

        } else if (state instanceof BetterGameStateTransition stateTransition
            && stateTransition.state().equals(GameOver.class)
        ) {
            StylisedPrinter.printSubheading(this.stylisedPrinterIOMechanism, "Turns Breakdown");
            int runningTotal = 0;

            for (Integer key : this.numberOfTurnsPerPiece.keySet()) {
                this.stylisedPrinterIOMechanism.println(
                    "-> Piece " + key + " had " + this.numberOfTurnsPerPiece.get(key) + " turns");
                runningTotal += this.numberOfTurnsPerPiece.get(key);
            }
            this.stylisedPrinterIOMechanism.println("-> In total, there were " + runningTotal + " turns this game.");
        }
    }

}
