package uk.ac.mmu.game.domain.game;

import uk.ac.mmu.game.domain.board.Board;
import uk.ac.mmu.game.domain.dice.DiceRolling;
import uk.ac.mmu.game.domain.game.state.gameturn.GameTurn;
import uk.ac.mmu.game.domain.hitcondition.PieceCollisionService;
import uk.ac.mmu.game.domain.wincondition.WinEvaluationService;

public record GameConfiguration(
    WinEvaluationService winEvaluator,
    PieceCollisionService pieceCollisionService,
    DiceRolling diceRoller,
    Board board,
    GameTurn turnSequence
) {}
