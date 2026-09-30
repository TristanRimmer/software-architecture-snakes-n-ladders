package uk.ac.mmu.game.infrastructure.persistence;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import uk.ac.mmu.game.domain.board.Board;
import uk.ac.mmu.game.domain.board.GameBoard;
import uk.ac.mmu.game.domain.board.specialpositions.OneWayTeleporter;
import uk.ac.mmu.game.domain.board.specialpositions.SpecialLinkedPositions;
import uk.ac.mmu.game.domain.board.specialpositions.TwoWayTeleporter;
import uk.ac.mmu.game.domain.dice.DiceRolling;
import uk.ac.mmu.game.domain.dice.variations.SingleDice;
import uk.ac.mmu.game.domain.dice.variations.stream.DiceStreamFixed;
import uk.ac.mmu.game.domain.dice.variations.stream.DiceStreamUnbounded;
import uk.ac.mmu.game.domain.game.GameConfiguration;
import uk.ac.mmu.game.domain.game.GameStore;
import uk.ac.mmu.game.domain.game.state.gameturn.ComprehensiveGameTurn;
import uk.ac.mmu.game.domain.game.state.gameturn.GameTurn;
import uk.ac.mmu.game.domain.pieces.GamePiece;
import uk.ac.mmu.game.domain.pieces.Piece;
import uk.ac.mmu.game.domain.pieces.container.LockingPieceContainer;
import uk.ac.mmu.game.domain.pieces.container.PieceContainer;
import uk.ac.mmu.game.domain.pieces.positiontrackers.LowerLeftOrigin;
import uk.ac.mmu.game.domain.pieces.positiontrackers.LowerRightOrigin;
import uk.ac.mmu.game.domain.pieces.positiontrackers.PositionTrackingConverter;
import uk.ac.mmu.game.domain.pieces.positiontrackers.UpperLeftOrigin;
import uk.ac.mmu.game.domain.pieces.positiontrackers.UpperRightOrigin;
import uk.ac.mmu.game.domain.rules.hitcondition.CollisionCondition;
import uk.ac.mmu.game.domain.rules.hitcondition.HitsDoNothing;
import uk.ac.mmu.game.domain.rules.hitcondition.HitsForfeitTurn;
import uk.ac.mmu.game.domain.rules.wincondition.CrossTheFinishline;
import uk.ac.mmu.game.domain.rules.wincondition.ExactHit;
import uk.ac.mmu.game.domain.rules.wincondition.WinCondition;
import uk.ac.mmu.game.domain.util.GridPosition;
import uk.ac.mmu.game.domain.util.ImplFactoryException;
import uk.ac.mmu.game.infrastructure.random.JavaStlRandom;
import uk.ac.mmu.game.infrastructure.serialisation.GameTurnSerialiser;
import uk.ac.mmu.game.infrastructure.serialisation.HitConditionSerialiser;
import uk.ac.mmu.game.infrastructure.serialisation.PositionTrackingConverterSerialiser;
import uk.ac.mmu.game.infrastructure.serialisation.SpecialPositionSerialiser;
import uk.ac.mmu.game.infrastructure.serialisation.WinConditionSerialiser;
import uk.ac.mmu.game.usecase.GameIDInvalidException;
import uk.ac.mmu.game.usecase.GameRepository;

public class FileSystemGameRepository implements GameRepository {
    // Private Error for failed serialisation
    private class FileSystemInternalError extends Exception {
        public String path;
        public String reason;

        public FileSystemInternalError(String path, String reason) {
            super(path + ", " + reason);
            this.path = path;
            this.reason = reason;
        }
    }

    private static final String USER_HOME_DIRECTORY = System.getProperty("user.home");
    private static final String BACKUP_HOME_DIRECTORY = "";
    private static final String DIRECTORY_OF_SAVES = "MMU_24854995_SandL";
    private static final String SAVE_FILE_CUSTOM_EXTENSION = "sandl";

    // Regarding File Reading, there are some key words that were previously magic
    private static final String GAME_SAVE_FILE_HEADER = "24854995_Game_Save";
    private static final String BOARD_SIZE_HEADER = "BoardSize";
    private static final String SPECIAL_POSITION_HEADER = "NumSpecialPositions";
    private static final String HIT_CONDITION_HEADER = "HitCondition";
    private static final String WIN_CONDITION_HEADER = "WinCondition";
    private static final String DICE_RULE_HEADER = "DiceRule";
    private static final String DICE_RULE_STRICT = "Strict";
    private static final String DICE_RULE_LENIENT = "Lenient";

    private static final String DICE_ROLLS_HEADER = "DiceRolls";
    private static final String PIECES_HEADER = "NumPieces";
    private static final String TURN_SEQUENCE_HEADER = "TurnSequence";
    // TODO: Random Number Generator

    private final Path pathToDirectory;
    private List<String> listOfSavesAsFiles;

    public FileSystemGameRepository() {
        // Checks if it can access the home directory
        Path pathToHome = Path.of(USER_HOME_DIRECTORY);
        Path backupHomePath = Path.of(BACKUP_HOME_DIRECTORY);

        if (!Files.exists(pathToHome) && !Files.exists(backupHomePath))
            throw new FileSystemGameRepositoryRuntimeError(
                    "Could not establish a path to either the home or current working directory");

        // If here, at least one worked
        Path chosenHome = Files.exists(pathToHome) ? pathToHome : backupHomePath;
        Path homeWithDir = Path.of(chosenHome + "/" + DIRECTORY_OF_SAVES);

        if (!Files.exists(homeWithDir)) {
            try {
                Files.createDirectory(homeWithDir);
            } catch (IOException e) {
                throw new FileSystemGameRepositoryRuntimeError(
                        "Could not create a directory for storing game saves: " + e.getMessage());
            }
        }

        // If here, there is an established path to the save directory
        this.pathToDirectory = homeWithDir;
        this.listOfSavesAsFiles = new ArrayList<>();

        try {
            List<Path> possibleSaves = Files.list(this.pathToDirectory).toList();

            for (Path file : possibleSaves) {
                if (Files.isDirectory(file))
                    continue;

                if (!file.getFileName().toString().endsWith("." + SAVE_FILE_CUSTOM_EXTENSION))
                    continue;

                // A bit ugly - just trimming the extension and . off
                this.listOfSavesAsFiles.add(this.getNameFromFile(file.getFileName().toString()));
            }
        } catch (IOException e) {
            throw new FileSystemGameRepositoryRuntimeError(
                    "Could not establish existing saves in save directory: " + e.getMessage());
        }
    }

    // TODO: implement choose random generator
    private GameStore serialiseGameFileToGameStore(Path file) throws FileSystemInternalError {
        /**
         * File Layout:
         * Line 1: 24854995_GAME_SAVE // Similar to BMPs, its a tag
         * // The following should be any order
         * BoardSize X Y
         * HitCondition [hitConditionName]
         * WinCondition [winConditionName]
         * 
         * NumPieces [NumPiece] // Immediately followed by the piece movement pattern.
         * In this comment, assume NumPiece to be 4
         * BottomLeft
         * TopRight
         * BottomRight
         * TopLeft
         * 
         * NumSpecialPositions [NumSpecial] // like above, assume its 3
         * OneWayTeleporter X1 Y1 X2 Y2 // eg 2 3 0 1
         * TwoWayTeleporter X1 Y1 X2 Y2
         * TwoWayTeleporter X1 Y1 X2 Y2
         * 
         * DiceRule [Strict/Lenient]
         * DiceRolls [NumDiceRolls] // Pretend its 7
         * 6 2 4 1 6 2 2
         * 
         * TurnSequence [turnSequence]
         */
        int numScannedLines = 0;
        boolean boardSizeSet = false;
        boolean winConditionSet = false;
        boolean hitConditionSet = false;
        boolean piecesSet = false;
        boolean specialPositionsSet = false;
        boolean diceRollsSet = false;
        boolean turnSequenceSet = false;
        // Prefer true by default
        boolean diceRulesetIsStrict = true;

        final Function<String, List<String>> splitter = string -> Stream.of(string.strip().split(" "))
                .filter(val -> !val.strip().isEmpty()).toList();
        final Function<String, Integer> stringToUnsignedInt = (stringyInt) -> {
            try {
                return Integer.valueOf(stringyInt.strip());
            } catch (NumberFormatException _e) {
                return -1;
            }
        };

        Integer boardWidth = -1;
        Integer boardHeight = -1;
        List<Integer> diceRolls = new ArrayList<>();
        List<PositionTrackingConverter> pieceSets = new ArrayList<>();
        List<SpecialLinkedPositions> specialPositions = new ArrayList<>();
        WinCondition winCondition = null;
        CollisionCondition hitCondition = null;
        GameTurn turnSequence = null;

        String multiLineHeader = "";
        int multiLineNum = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(file.toString()))) {
            String currentLine;

            while ((currentLine = reader.readLine()) != null) {
                List<String> pieces = splitter.apply(currentLine);

                if (pieces.isEmpty()) {
                    numScannedLines++;
                    continue;
                }

                String header = pieces.getFirst().strip();

                if (numScannedLines == 0 && !header.equals(GAME_SAVE_FILE_HEADER))
                    throw new FileSystemInternalError(file.getFileName().toString(),
                            "First Line Identifier Invalid, got " + header);

                switch (header) {
                    case BOARD_SIZE_HEADER -> {
                        if (pieces.size() != 3)
                            throw new FileSystemInternalError(file.getFileName().toString(),
                                    "Improper Board Size configuration");

                        Integer xSize = stringToUnsignedInt.apply(pieces.get(1));
                        Integer ySize = stringToUnsignedInt.apply(pieces.get(2));

                        if (xSize <= 0 || ySize <= 0)
                            throw new FileSystemInternalError(file.getFileName().toString(),
                                    "Board Size Dimensions are Improper");

                        boardWidth = xSize;
                        boardHeight = ySize;
                        boardSizeSet = true;

                        break;
                    }
                    case HIT_CONDITION_HEADER -> {
                        if (pieces.size() != 2)
                            throw new FileSystemInternalError(file.getFileName().toString(),
                                    "Improper Hit Condition configuration");
                        try {
                            hitCondition = HitConditionSerialiser.getImplementationFromString(pieces.get(1));
                            hitConditionSet = true;
                        } catch (ImplFactoryException e) {
                            throw new FileSystemInternalError(file.getFileName().toString(),
                                    " WinConditionInitialisationException WinConditionInitialisationExceptionInvalid HitCondition option");
                        }
                        break;
                    }
                    case DICE_RULE_HEADER -> {
                        if (pieces.size() != 2)
                            throw new FileSystemInternalError(file.getFileName().toString(),
                                    "Improper Dice Rule Configuration");

                        switch (pieces.get(1)) {
                            case DICE_RULE_STRICT -> diceRulesetIsStrict = true;
                            case DICE_RULE_LENIENT -> diceRulesetIsStrict = false;
                            default -> throw new FileSystemInternalError(file.getFileName().toString(),
                                    "Invalid Dice Rule Option");
                        }
                    }
                    case WIN_CONDITION_HEADER -> {
                        if (pieces.size() != 2)
                            throw new FileSystemInternalError(file.getFileName().toString(),
                                    "Improper Win Condition Configuration");
                        try {
                            winCondition = WinConditionSerialiser.getImplementationFromString(pieces.get(1));
                            winConditionSet = true;
                        } catch (ImplFactoryException e) {
                            throw new FileSystemInternalError(file.getFileName().toString(),
                                    "Invalid Win Condition Option");
                        }
                        break;
                    }
                    case TURN_SEQUENCE_HEADER -> {
                        if (pieces.size() != 2)
                            throw new FileSystemInternalError(file.getFileName().toString(),
                                    "Invalid turn sequence configuration");
                        try {
                            turnSequence = GameTurnSerialiser.getImplementationFromString(pieces.get(1));
                            turnSequenceSet = true;
                        } catch (ImplFactoryException e) {
                            throw new FileSystemInternalError(file.getFileName().toString(),
                                    "Invalid Turn Sequence option");
                        }
                        break;
                    }
                    case DICE_ROLLS_HEADER, PIECES_HEADER, SPECIAL_POSITION_HEADER -> {
                        if (pieces.size() != 2)
                            throw new FileSystemInternalError(file.getFileName().toString(),
                                    "Improper Configuration for " + pieces.getFirst());

                        Integer numLines = stringToUnsignedInt.apply(pieces.get(1));

                        if (numLines < 0)
                            throw new FileSystemInternalError(file.getFileName().toString(),
                                    "Improper number of entries specified for " + pieces.getFirst());

                        multiLineHeader = numLines == 0 ? multiLineHeader : pieces.get(0);
                        multiLineNum = numLines;

                        if (numLines == 0) {
                            switch (pieces.get(0)) {
                                case SPECIAL_POSITION_HEADER -> specialPositionsSet = true;
                                case PIECES_HEADER -> piecesSet = true;
                                case DICE_ROLLS_HEADER -> diceRollsSet = true;
                                default -> throw new FileSystemInternalError(file.getFileName().toString(),
                                        "Invalid Multi-Line specification");
                            }
                        }
                        break;
                    }
                    default -> {
                        if (multiLineNum == 0 || multiLineHeader.isEmpty()) {
                            numScannedLines++;
                            continue;
                        }
                        // We're in a multi-line list
                        switch (multiLineHeader) {
                            case DICE_ROLLS_HEADER -> {
                                Integer diceRoll = stringToUnsignedInt.apply(pieces.get(0));

                                if (diceRoll <= 0)
                                    throw new FileSystemInternalError(file.getFileName().toString(),
                                            "Improper Dice Number Value");

                                diceRolls.add(diceRoll);

                                if (diceRolls.size() == multiLineNum) {
                                    multiLineNum = 0;
                                    multiLineHeader = "";
                                    diceRollsSet = true;
                                    break;
                                }
                                break;
                            }
                            case PIECES_HEADER -> {
                                try {
                                    pieceSets.add(PositionTrackingConverterSerialiser
                                            .getImplementationFromString(pieces.get(0)));
                                } catch (ImplFactoryException e) {
                                    throw new FileSystemInternalError(file.getFileName().toString(),
                                            "Invalid Piece Movement Pattern: " + e);
                                }
                                if (pieceSets.size() == multiLineNum) {
                                    multiLineNum = 0;
                                    multiLineHeader = "";
                                    piecesSet = true;
                                    break;
                                }
                                break;
                            }
                            case SPECIAL_POSITION_HEADER -> {
                                if (pieces.size() != 5)
                                    throw new FileSystemInternalError(file.getFileName().toString(),
                                            "Improper Special Position Description");

                                Integer x1 = stringToUnsignedInt.apply(pieces.get(1));
                                Integer y1 = stringToUnsignedInt.apply(pieces.get(2));
                                Integer x2 = stringToUnsignedInt.apply(pieces.get(3));
                                Integer y2 = stringToUnsignedInt.apply(pieces.get(4));

                                if (x1 < 0 || y1 < 0 || x2 < 0 || y2 < 0)
                                    throw new FileSystemInternalError(file.getFileName().toString(),
                                            "Improper special position coordinate specification");
                                try {
                                    specialPositions.add(SpecialPositionSerialiser
                                            .getImplementationFromString(pieces.get(0), new GridPosition(x1, y1),
                                                    new GridPosition(x2, y2)));
                                } catch (ImplFactoryException e) {
                                    throw new FileSystemInternalError(file.getFileName().toString(),
                                            "Invalid Piece Movement Pattern: " + e);
                                }
                                if (specialPositions.size() == multiLineNum) {
                                    multiLineNum = 0;
                                    multiLineHeader = "";
                                    specialPositionsSet = true;
                                    break;
                                }
                                break;
                            }
                            default -> throw new FileSystemInternalError(file.getFileName().toString(),
                                    "Invalid Multi-Line specification");
                        }
                    }
                }
                numScannedLines++;
            }
        } catch (IOException e) {
            throw new FileSystemInternalError(file.getFileName().toString(), "Error Reading the game file");
        }

        if (!boardSizeSet || !winConditionSet || !hitConditionSet || !piecesSet || !specialPositionsSet
                || !diceRollsSet || !turnSequenceSet) {
            System.out.println("Board Configuration correct: " + boardSizeSet);
            System.out.println("Win Condition correct: " + winConditionSet);
            System.out.println("Hit Condition correct: " + hitConditionSet);
            System.out.println("Piece Configuration correct: " + piecesSet);
            System.out.println("Board Special Position Configuration correct: " + specialPositionsSet);
            System.out.println("Dice Rolls Loaded correct: " + diceRollsSet);
            System.out.println("Turn Sequence configuration correct: " + turnSequenceSet);

            throw new FileSystemInternalError(file.getFileName().toString(),
                    "Game File did not specify all required configurations");
        }
        Board board = new GameBoard(boardWidth, boardHeight, specialPositions);
        DiceRolling diceRoller = diceRulesetIsStrict ? new DiceStreamFixed(diceRolls)
                : new DiceStreamUnbounded(diceRolls, new SingleDice(new JavaStlRandom(), 6));

        GameConfiguration configuration = new GameConfiguration(winCondition, hitCondition, diceRoller, board,
                turnSequence);

        PieceContainer pieceContainer = new LockingPieceContainer();

        for (PositionTrackingConverter p : pieceSets) {
            pieceContainer.registerNewPiece(new GamePiece(p, boardWidth, boardHeight));
        }

        return new GameStore(configuration, pieceContainer, diceRolls,
                this.getNameFromFile(file.getFileName().toString()));
    }

    @Override
    public void saveGame(GameStore gameData) {
        String fileName = gameData.tag();

        GameConfiguration config = gameData.configuration();

        // Initial check to see if it needs an additional extension
        boolean fileNameIsntUnique = this.listOfSavesAsFiles.contains(fileName);

        System.out.println(this.listOfSavesAsFiles.getLast() + ", " + fileName);

        int copyNumber = fileNameIsntUnique ? 1 : 0;

        while (fileNameIsntUnique) {
            if (this.listOfSavesAsFiles.contains(
                    fileName + " (" + copyNumber + ")")) {
                copyNumber += 1;
            } else {
                fileNameIsntUnique = false;
            }
        }

        fileName = (copyNumber == 0) ? fileName : fileName + " (" + copyNumber + ")";

        Path newFile = this.mapFileNameToPath(fileName);

        try (BufferedWriter writer = Files.newBufferedWriter(newFile)) {
            writer.write(GAME_SAVE_FILE_HEADER);
            writer.newLine();

            writer.write(
                    BOARD_SIZE_HEADER + " " + config.board().getBoardWidth() + " " + config.board().getBoardHeight());
            writer.newLine();

            writer.write(SPECIAL_POSITION_HEADER + " " + 0);
            writer.newLine();

            writer.write(HIT_CONDITION_HEADER +
                    " " + HitConditionSerialiser.getStringFromImplementation(config.collisionEvaluator()));
            writer.newLine();

            writer.write(WIN_CONDITION_HEADER + " "
                    + WinConditionSerialiser.getStringFromImplementation(config.winEvaluator()));
            writer.newLine();

            writer.write(
                    TURN_SEQUENCE_HEADER + " " + GameTurnSerialiser.getStringFromImplementation(config.turnSequence()));
            writer.newLine();

            writer.write(DICE_RULE_HEADER + " " + DICE_RULE_STRICT);
            writer.newLine();

            writer.write(DICE_ROLLS_HEADER + " " + gameData.diceRolls().size());
            writer.newLine();

            for (Integer roll : gameData.diceRolls()) {
                writer.write(Integer.toString(roll));
                writer.newLine();
            }

            writer.write(PIECES_HEADER + " " + gameData.pieces().getNumPieces());
            writer.newLine();

            for (Piece piece : gameData.pieces().getPiecesInOriginalOrder()) {
                writer.write(
                        PositionTrackingConverterSerialiser.getStringFromImplementation(piece.getTrackingConverter()));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new FileSystemGameRepositoryRuntimeError("Failed to serialise recent game: " + e);
        }

        System.out.println("Saved to " + newFile.getFileName().toString());
        this.listOfSavesAsFiles.add(newFile.getFileName().toString());
    }

    @Override
    public GameStore loadGame(int gameID) throws GameIDInvalidException {
        if (gameID < 0 || gameID > this.listOfSavesAsFiles.size())
            throw new GameIDInvalidException("Game Index/ID out of range");

        String file = this.listOfSavesAsFiles.get(gameID);

        try {
            return this.serialiseGameFileToGameStore(this.mapFileNameToPath(file));
        } catch (FileSystemInternalError e) {
            throw new GameIDInvalidException("Game Was unable to be serialised: " + e.path + " due to " + e.reason);
        }
    }

    @Override
    public List<String> getSavedGameOptions() {
        return this.listOfSavesAsFiles;
    }

    private Path mapFileNameToPath(String path) {
        return Path.of(this.pathToDirectory + "/" + path + "." + this.SAVE_FILE_CUSTOM_EXTENSION);
    }

    private String getNameFromFile(String fileName) {
        return fileName.substring(
                0,
                fileName.length() - this.SAVE_FILE_CUSTOM_EXTENSION.length() - 1);
    }
}
