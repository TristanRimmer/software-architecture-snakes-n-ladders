package uk.ac.mmu.game.infrastructure.persistence.FileSystem;

// Intentionally package private
enum SavedGameHeaders {
    DICE_RULE_HEADER("DiceRule"),
    DICE_RULE_STRICT("Strict"),
    DICE_RULE_LENIENT("Lenient"),
    TURN_SEQUENCE_HEADER("TurnSequence"),
    SPECIAL_POSITION_HEADER("NumSpecialPositions"),
    DICE_ROLLS_HEADER ("DiceRolls"),
    PIECES_HEADER ("NumPieces"),
    GAME_SAVE_FILE_HEADER ("24854995_Game_Save"),
    WIN_CONDITION_HEADER ("WinCondition"),
    HIT_CONDITION_HEADER ("HitCondition"),
    BOARD_SIZE_HEADER ("BoardSize"),
    RANDOM_NUMBER_GENERATOR ("RandomNumbersSource");

    private final String header;

    SavedGameHeaders(String header) {
        this.header = header;
    }
    public String value() {
        return this.header;
    }
}
