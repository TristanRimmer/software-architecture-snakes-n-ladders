package uk.ac.mmu.game.domain.dice.variations.stream;

import java.util.List;

import uk.ac.mmu.game.domain.dice.DiceRolling;

public class DiceStreamFixed implements DiceRolling {
    DiceRollFromStream diceRollStream;

    public DiceStreamFixed(List<Integer> stream) {
        this.diceRollStream = new DiceRollFromStream(stream);
    }
    
    @Override
    public int nextDiceRoll() {
        try {
            return this.diceRollStream.nextDiceRoll();
        } catch (DiceRollStreamMisuseException e) {
            throw new DiceRollStreamRuntimeException("The DiceStreamFixed object has exceeded its stream of dice rolls");
        }
    }
}
