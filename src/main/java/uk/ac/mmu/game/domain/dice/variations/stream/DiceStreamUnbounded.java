package uk.ac.mmu.game.domain.dice.variations.stream;

import java.util.List;

import uk.ac.mmu.game.domain.dice.DiceRolling;
import uk.ac.mmu.game.domain.dice.GenerateRandomNumber;

public class DiceStreamUnbounded implements DiceRolling {
    DiceRollFromStream diceRollStream;
    DiceRolling backupRoller;

    public DiceStreamUnbounded(List<Integer> stream, DiceRolling backup) {
        this.diceRollStream = new DiceRollFromStream(stream);
        this.backupRoller = backup;
    }
    @Override
    public int nextDiceRoll() {
        try {
            return this.diceRollStream.nextDiceRoll();
        } catch (DiceRollStreamMisuseException e) {
            return backupRoller.nextDiceRoll();
        }
    }
    @Override
    public GenerateRandomNumber randomNumbersSource() {
        return null;
    }
}
