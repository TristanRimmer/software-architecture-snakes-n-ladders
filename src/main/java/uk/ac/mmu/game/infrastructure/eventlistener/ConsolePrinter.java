package uk.ac.mmu.game.infrastructure.eventlistener;

import uk.ac.mmu.game.domain.events.GameEventSubscriber;
import uk.ac.mmu.game.domain.events.types.ArbitraryHeader;
import uk.ac.mmu.game.domain.events.types.ArbitraryMessage;
import uk.ac.mmu.game.domain.events.types.DiceRolled;
import uk.ac.mmu.game.domain.events.types.GameEvent;
import uk.ac.mmu.game.domain.events.types.GameStateTransition;
import uk.ac.mmu.game.domain.events.types.PieceMove;
import uk.ac.mmu.game.domain.events.types.PieceWon;
import uk.ac.mmu.game.domain.events.types.PiecesHit;
import uk.ac.mmu.game.domain.events.types.TurnChange;
import uk.ac.mmu.game.infrastructure.output.StylisedPrinter;
import uk.ac.mmu.game.infrastructure.output.SystemOut;
import uk.ac.mmu.game.infrastructure.output.TextOutputHandler;

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
        System.out.println("=> The piece has landed on " + pieceWon.piece().getCurrentPosition() + " and has won the game!"); 

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
