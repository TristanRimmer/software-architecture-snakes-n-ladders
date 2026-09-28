package uk.ac.mmu.game.domain.gamestate;

import uk.ac.mmu.game.domain.board.BoardService;
import uk.ac.mmu.game.domain.dice.DiceRollingService;
import uk.ac.mmu.game.domain.hitcondition.PieceCollisionService;
import uk.ac.mmu.game.domain.wincondition.WinEvaluationService;

public record GameConfiguration(
    WinEvaluationService winEvaluator,
    PieceCollisionService pieceCollisionService,
    DiceRollingService diceRoller,
    BoardService board,
    GameTurn turnSequence
) {}
