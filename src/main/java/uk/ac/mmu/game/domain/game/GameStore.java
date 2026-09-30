package uk.ac.mmu.game.domain.game;

import java.util.List;

import uk.ac.mmu.game.domain.pieces.container.PieceContainer;

// TODO: GameStore does not need to store the diceRolls
// This class needs refactoring - GameConfiguration is immutable but the diceRoller needs to be substituted for a DiceStream
// And the DiceStream should store the dice rolls
public record GameStore(
        GameConfiguration configuration,
        PieceContainer pieces,
        List<Integer> diceRolls,
        String tag) {
}
