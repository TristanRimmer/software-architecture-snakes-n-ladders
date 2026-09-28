package uk.ac.mmu.game.observer.subscribers;

import uk.ac.mmu.game.observer.events.ArbitraryHeader;
import uk.ac.mmu.game.observer.events.ArbitraryMessage;
import uk.ac.mmu.game.observer.events.DiceRolled;
import uk.ac.mmu.game.observer.events.GameEvent;
import uk.ac.mmu.game.observer.events.GameStateTransition;
import uk.ac.mmu.game.observer.events.PieceMove;
import uk.ac.mmu.game.observer.events.PieceWon;
import uk.ac.mmu.game.observer.events.PiecesHit;
import uk.ac.mmu.game.observer.events.TurnChange;
import uk.ac.mmu.game.output.StylisedPrinter;
import uk.ac.mmu.game.output.SystemOut;
import uk.ac.mmu.game.output.TextOutputHandler;

// TODO: add early returns and make it a chain
public class ConsolePrinter implements GameEventSubscriber {
    private final TextOutputHandler stylisedPrinterIOMechanism = new SystemOut();
    
    @Override
    public void notify(GameEvent state) {
        /*
            The ConsolePrinter cares about every single state

            In a language with better templating (like C++), there would be a manually monomorphised implementation of each state it cares about

            Java cannot do this, so this is the next best thing
        */
       if (state instanceof DiceRolled diceRolled)
        System.out.println("=> The Dice was rolled and landed on a " + diceRolled.diceRoll());

       if (state instanceof PieceMove pieceMove)
        System.out.println("=> The Piece has moved from " + pieceMove.oldPos() + " to " + pieceMove.newPos());

       if (state instanceof PiecesHit piecesHit)
        System.out.println("=> The piece has collided with another one at " + piecesHit.pieceB().getCurrentPosition());

       if (state instanceof PieceWon pieceWon)
        System.out.println("=> The peice has landed on " + pieceWon.piece().getCurrentPosition() + " and has won the game!"); 

       if (state instanceof TurnChange pieceChange)
        StylisedPrinter.printSubheading(stylisedPrinterIOMechanism, "Piece " + pieceChange.pieceNumber() + "'s Turn");

       if (state instanceof GameStateTransition stateTransition)
        StylisedPrinter.printBanner(this.stylisedPrinterIOMechanism, stateTransition.newStateName());
        
       if (state instanceof ArbitraryHeader header)
        StylisedPrinter.printSubheading(stylisedPrinterIOMechanism, header.msg());    

       if (state instanceof ArbitraryMessage message)
        System.out.println("=> " + message.msg());         
    }
}
