package uk.ac.mmu.game.infrastructure.subscribers;

import java.util.List;

import uk.ac.mmu.game.domain.board.specialpositions.SpecialLinkedPositions;
import uk.ac.mmu.game.domain.dice.DiceRolling;
import uk.ac.mmu.game.domain.dice.variations.SingleDice;
import uk.ac.mmu.game.domain.dice.variations.TwoDice;
import uk.ac.mmu.game.domain.dice.variations.stream.DiceStreamFixed;
import uk.ac.mmu.game.domain.dice.variations.stream.DiceStreamUnbounded;
import uk.ac.mmu.game.domain.events.GameEventSubscriber;
import uk.ac.mmu.game.domain.events.types.BetterGameStateTransition;
import uk.ac.mmu.game.domain.events.types.GameEvent;
import uk.ac.mmu.game.domain.game.GameConfiguration;
import uk.ac.mmu.game.domain.game.state.Ready;
import uk.ac.mmu.game.domain.pieces.Piece;
import uk.ac.mmu.game.domain.pieces.container.PieceContainer;
import uk.ac.mmu.game.domain.util.GridPosition;
import uk.ac.mmu.game.infrastructure.implmappers.HitConditionMapper;
import uk.ac.mmu.game.infrastructure.implmappers.PositionTrackingConverterMapper;
import uk.ac.mmu.game.infrastructure.implmappers.SpecialPositionMapper;
import uk.ac.mmu.game.infrastructure.implmappers.WinConditionMapper;
import uk.ac.mmu.game.infrastructure.output.StylisedPrinter;
import uk.ac.mmu.game.infrastructure.output.SystemOut;
import uk.ac.mmu.game.infrastructure.output.TextOutputHandler;

public class MetadataDumper implements GameEventSubscriber {
    private final GameConfiguration gameConfig;
    private final PieceContainer pieces;
    private final TextOutputHandler output = new SystemOut();

    public MetadataDumper(GameConfiguration config, PieceContainer pieces) {
        this.gameConfig = config;
        this.pieces = pieces;
    }

    @Override
    public void notify(GameEvent state) {
        if (state instanceof BetterGameStateTransition stateTransition
            && stateTransition.state().equals(Ready.class)) {
            StylisedPrinter.printSubheading(output, "Game Configuration");
            output.println("-> Board Dimensions: " + new GridPosition(
                gameConfig.board().getBoardWidth(), 
                gameConfig.board().getBoardHeight()));
            
            final List<SpecialLinkedPositions> specialPositions = this.gameConfig.board().getSpecialPositions();
            output.println("- Number of Teleporter-Pairs: " + specialPositions.size());
            output.println("- Variety of types being used: ");
            String types = "";
            for (SpecialLinkedPositions pos : specialPositions) {
                String asString = SpecialPositionMapper.getStringFromImplemention(pos);
                if (!types.contains(asString))
                    types = (types.isEmpty()) ? "--- " + asString : types + "\n" + "--- " + asString;
            }
            output.println(types);

            String winConditionAsString = WinConditionMapper.getStringFromImplementation(gameConfig.winEvaluator());
            String hitConditionAsString = HitConditionMapper.getStringFromImplementation(gameConfig.collisionEvaluator());

            output.println("- Win Condition: " + winConditionAsString);
            output.println("- Hit Condition: " + hitConditionAsString);

            final DiceRolling diceStrategy = gameConfig.diceRoller();

            if (diceStrategy instanceof SingleDice) {
                output.println("- Number of dice: 1xD" + diceStrategy.getMaxPossibleRoll());
            } else if (diceStrategy instanceof TwoDice) {
                output.println("- Number of dice: 2xD" + diceStrategy.getMaxPossibleRoll());
            } else if (diceStrategy instanceof DiceStreamFixed) {
                output.println("- Dice Rolls Pre Recorded: 2 D" + diceStrategy.getMaxPossibleRoll());
                output.println("- The dice rule is Strict. If the list of rolls is exhausted, the program will intentionally crash"); 
            } else if (diceStrategy instanceof DiceStreamUnbounded) {
                output.println("- Dice Rolls Pre Recorded: 2 D" + diceStrategy.getMaxPossibleRoll());
                output.println("- The dice rule is lenient. If the list of rolls is exhausted, the program will automatically generate more"); 
            }

            StylisedPrinter.printSubheading(output, "Piece Configuration");
            List<Piece> pieces = this.pieces.getPiecesInOriginalOrder();

            output.println("- Number of Pieces: " + pieces.size());
            output.println("- Starting corner of each piece: ");
            for (Piece p : pieces) {
                output.println("- - " + PositionTrackingConverterMapper.getStringFromImplementation(p.getTrackingConverter()));
            }
        }
    }
}
