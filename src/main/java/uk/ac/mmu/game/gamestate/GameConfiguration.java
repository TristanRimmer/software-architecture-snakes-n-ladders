package uk.ac.mmu.game.gamestate;

import uk.ac.mmu.game.board.BoardService;
import uk.ac.mmu.game.diceroller.DiceRollingService;
import uk.ac.mmu.game.hitcondition.PieceCollisionService;
import uk.ac.mmu.game.wincondition.WinEvaluationService;

public record GameConfiguration(
    WinEvaluationService winEvaluator,
    PieceCollisionService pieceCollisionService,
    DiceRollingService diceRoller,
    BoardService board,
    GameTurn turnSequence
) {}
