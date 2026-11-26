package uk.ac.mmu.game.domain.dice;

/*
import uk.ac.mmu.game.domain.dice.variations.SingleDice;
import uk.ac.mmu.game.domain.dice.variations.TwoDice;
import uk.ac.mmu.game.domain.dice.variations.stream.DiceStreamFixed;
import uk.ac.mmu.game.domain.dice.variations.stream.DiceStreamUnbounded;
*/

public /* sealed */ interface DiceRolling /* permits SingleDice, TwoDice, DiceStreamFixed, DiceStreamUnbounded */ {
    int nextDiceRoll();
    int getMaxPossibleRoll();
}
