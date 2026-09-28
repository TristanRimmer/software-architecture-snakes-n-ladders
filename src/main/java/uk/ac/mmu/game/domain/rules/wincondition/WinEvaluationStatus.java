package uk.ac.mmu.game.domain.rules.wincondition;

public enum WinEvaluationStatus {
    // When a piece is not close to winning
    CONTINUE,
    // When a piece would have won but didn't as a direct result of the WinCondition impl
    CLOSECALL,
    // Self Explanatory
    WON,
}
