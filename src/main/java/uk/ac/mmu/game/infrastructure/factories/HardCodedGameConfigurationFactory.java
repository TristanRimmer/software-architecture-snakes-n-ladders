package uk.ac.mmu.game.infrastructure.factories;

import java.util.ArrayList;
import java.util.List;

import uk.ac.mmu.game.domain.board.Board;
import uk.ac.mmu.game.domain.board.GameBoard;
import uk.ac.mmu.game.domain.board.specialpositions.SpecialLinkedPositions;
import uk.ac.mmu.game.domain.board.specialpositions.TwoWayTeleporter;
import uk.ac.mmu.game.domain.dice.DiceRolling;
import uk.ac.mmu.game.domain.dice.variations.SingleDice;
import uk.ac.mmu.game.domain.game.GameConfiguration;
import uk.ac.mmu.game.domain.game.gameturn.ComprehensiveGameTurn;
import uk.ac.mmu.game.domain.game.gameturn.GameTurn;
import uk.ac.mmu.game.domain.rules.hitcondition.CollisionCondition;
import uk.ac.mmu.game.domain.rules.hitcondition.HitsForfeitTurn;
import uk.ac.mmu.game.domain.rules.wincondition.ExactHit;
import uk.ac.mmu.game.domain.rules.wincondition.WinCondition;
import uk.ac.mmu.game.domain.util.GridPosition;
import uk.ac.mmu.game.infrastructure.random.JavaStlRandom;
import uk.ac.mmu.game.usecase.GameConfigurationFactory;

public class HardCodedGameConfigurationFactory implements GameConfigurationFactory {
    @Override
    // Example to demonstrate that the code is not coupled directly to spring
    public GameConfiguration getGameConfiguration() {
		List<SpecialLinkedPositions> specialPositions = new ArrayList<>();
		specialPositions.add(
            new TwoWayTeleporter(
                new GridPosition(0,3), 
                new GridPosition(1, 0)));

		Board board = new GameBoard(5, 5, specialPositions);

		DiceRolling diceRoller = new SingleDice(new JavaStlRandom(), 6);
		WinCondition winEvaluator = new ExactHit();
		CollisionCondition collisionHandler = new HitsForfeitTurn();

        GameTurn turnSequence = new ComprehensiveGameTurn();

        return new GameConfiguration(winEvaluator, collisionHandler, diceRoller, board, turnSequence);
    }

}
