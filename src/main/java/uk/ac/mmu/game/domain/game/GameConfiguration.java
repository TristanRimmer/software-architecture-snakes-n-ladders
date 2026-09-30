package uk.ac.mmu.game.domain.game;

import uk.ac.mmu.game.domain.board.Board;
import uk.ac.mmu.game.domain.dice.DiceRolling;
import uk.ac.mmu.game.domain.game.state.gameturn.GameTurn;
import uk.ac.mmu.game.domain.rules.hitcondition.CollisionCondition;
import uk.ac.mmu.game.domain.rules.wincondition.WinCondition;

public record GameConfiguration(
        WinCondition winEvaluator,
        CollisionCondition collisionEvaluator,
        DiceRolling diceRoller,
        Board board,
        GameTurn turnSequence) {
}
