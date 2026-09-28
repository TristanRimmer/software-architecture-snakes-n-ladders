package uk.ac.mmu.game.domain.gamestate;

import uk.ac.mmu.game.domain.board.Board;
import uk.ac.mmu.game.domain.dice.DiceRollingService;
import uk.ac.mmu.game.domain.hitcondition.PieceCollisionService;
import uk.ac.mmu.game.domain.wincondition.WinEvaluationService;

public record GameConfiguration(
    WinEvaluationService winEvaluator,
    PieceCollisionService pieceCollisionService,
    DiceRollingService diceRoller,
    Board board,
    GameTurn turnSequence
) {}
