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
import java.util.function.Predicate;
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
import uk.ac.mmu.game.infrastructure.random.JavaStlRandom;
import uk.ac.mmu.game.usecase.GameIDInvalidException;
import uk.ac.mmu.game.usecase.GameRepository;

public class FileSystemGameRepository implements GameRepository {
    // Private Error for failed serialisation
    private class FileSystemInternalError extends Exception {
        public String path;

        public FileSystemInternalError(String path) {
            super(path);
            this.path = path;
        }
    }

    private static final String USER_HOME_DIRECTORY = System.getProperty("user.home");

    // In other words, the current working directory
    private static final String BACKUP_HOME_DIRECTORY = "";

    private static final String DIRECTORY_OF_SAVES = "MMU_24854995_SandL";
    private static final String SAVE_FILE_CUSTOM_EXTENSION = "sandl";

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

                this.listOfSavesAsFiles.add(file.getFileName().toString());
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
                if (pieces.isEmpty())
                    continue;

                String header = pieces.getFirst();

                if (numScannedLines == 0 && !header.equals("24854995_GAME_SAVE"))
                    throw new FileSystemInternalError(file.getFileName().toString());

                switch (header) {
                    case "BoardSize" -> {
                        if (pieces.size() != 3)
                            throw new FileSystemInternalError(file.getFileName().toString());

                        Integer xSize = stringToUnsignedInt.apply(pieces.get(1));
                        Integer ySize = stringToUnsignedInt.apply(pieces.get(2));

                        if (xSize <= 0 || ySize <= 0)
                            throw new FileSystemInternalError(file.getFileName().toString());

                        boardWidth = xSize;
                        boardHeight = ySize;

                        boardSizeSet = true;
                        break;
                    }
                    case "HitCondition" -> {
                        if (pieces.size() != 2)
                            throw new FileSystemInternalError(file.getFileName().toString());

                        switch (pieces.get(1)) {
                            case "HitsDontCount" -> hitCondition = new HitsDoNothing();
                            case "HitsForfeitMove" -> hitCondition = new HitsForfeitTurn();
                            default -> throw new FileSystemInternalError(file.getFileName().toString());
                        }

                        hitConditionSet = true;
                        break;
                    }
                    case "DiceRule" -> {
                        if (pieces.size() != 2)
                            throw new FileSystemInternalError(file.getFileName().toString());

                        switch (pieces.get(1)) {
                            case "Strict" -> diceRulesetIsStrict = true;
                            case "Lenient" -> diceRulesetIsStrict = false;
                            default -> throw new FileSystemInternalError(file.getFileName().toString());
                        }
                    }
                    case "WinCondition" -> {
                        if (pieces.size() != 2)
                            throw new FileSystemInternalError(file.getFileName().toString());

                        // TODO: perhaps for things like this, rather than doing it as a switch have a
                        // final
                        // class that takes in a string and eithe throws an error or returns the
                        // structure for you (a factory lol just say that)
                        switch (pieces.get(1)) {
                            case "CrossTheFinishLine" -> winCondition = new CrossTheFinishline();
                            case "ExactHit" -> winCondition = new ExactHit();
                            default -> throw new FileSystemInternalError(file.getFileName().toString());
                        }

                        winConditionSet = true;
                        break;
                    }
                    case "TurnSequence" -> {
                        if (pieces.size() != 2)
                            throw new FileSystemInternalError(file.getFileName().toString());

                        // TODO: perhaps for things like this, rather than doing it as a switch have a
                        // final
                        // class that takes in a string and eithe throws an error or returns the
                        // structure for you (a factory lol just say that)
                        switch (pieces.get(1)) {
                            case "Default" -> turnSequence = new ComprehensiveGameTurn();
                            default -> throw new FileSystemInternalError(file.getFileName().toString());
                        }
                        turnSequenceSet = true;
                        break;
                    }
                    case "DiceRolls", "NumPieces", "NumSpecialPositions" -> {
                        if (pieces.size() != 2)
                            throw new FileSystemInternalError(file.getFileName().toString());

                        Integer numLines = stringToUnsignedInt.apply(pieces.get(1));

                        if (numLines <= 0)
                            throw new FileSystemInternalError(file.getFileName().toString());

                        multiLineHeader = pieces.get(0);
                        multiLineNum = numLines;
                        break;
                    }
                    default -> {
                        // It may just be white space/nothign of importance
                        if (multiLineNum != 0 && !multiLineHeader.isEmpty()) {
                            // We're in a multi-line list
                            switch (multiLineHeader) {
                                case "DiceRolls" -> {
                                    if (diceRolls.size() == multiLineNum) {
                                        multiLineNum = 0;
                                        multiLineHeader = "";
                                        diceRollsSet = true;
                                        break;
                                    }
                                    Integer diceRoll = stringToUnsignedInt.apply(pieces.get(0));

                                    if (diceRoll <= 0)
                                        throw new FileSystemInternalError(file.getFileName().toString());

                                    diceRolls.add(diceRoll);
                                }
                                case "NumPieces" -> {
                                    if (pieceSets.size() == multiLineNum) {
                                        multiLineNum = 0;
                                        multiLineHeader = "";
                                        piecesSet = true;
                                        break;
                                    }
                                    // TODO: perhaps for things like this, rather than doing it as a switch have a
                                    // final
                                    // class that takes in a string and eithe throws an error or returns the
                                    // structure for you (a factory lol just say that)
                                    switch (pieces.get(0)) {
                                        case "LowerLeft" -> pieceSets.add(new LowerLeftOrigin());
                                        case "LowerRight" -> pieceSets.add(new LowerRightOrigin());
                                        case "UpperLeft" -> pieceSets.add(new UpperLeftOrigin());
                                        case "UpperRight" -> pieceSets.add(new UpperRightOrigin());
                                        default -> throw new FileSystemInternalError(file.getFileName().toString());
                                    }
                                }
                                case "NumSpecialPositions" -> {
                                    if (specialPositions.size() == multiLineNum) {
                                        multiLineNum = 0;
                                        multiLineHeader = "";
                                        specialPositionsSet = true;
                                        break;
                                    }
                                    if (pieces.size() != 5)
                                        throw new FileSystemInternalError(file.getFileName().toString());

                                    Integer x1 = stringToUnsignedInt.apply(pieces.get(1));
                                    Integer y1 = stringToUnsignedInt.apply(pieces.get(2));
                                    Integer x2 = stringToUnsignedInt.apply(pieces.get(3));
                                    Integer y2 = stringToUnsignedInt.apply(pieces.get(4));

                                    if (x1 < 0 || y1 < 0 || x2 < 0 || y2 < 0)
                                        throw new FileSystemInternalError(file.getFileName().toString());

                                    // TODO: perhaps for things like this, rather than doing it as a switch have a
                                    // final
                                    // class that takes in a string and eithe throws an error or returns the
                                    // structure for you (a factory lol just say that)
                                    switch (pieces.get(0)) {
                                        case "OneWayTeleporter" ->
                                            specialPositions.add(new OneWayTeleporter(new GridPosition(x1, y1),
                                                    new GridPosition(x2, y2)));
                                        case "TwoWayTeleporter" ->
                                            specialPositions.add(new TwoWayTeleporter(new GridPosition(x1, y1),
                                                    new GridPosition(x2, y2)));
                                        default -> throw new FileSystemInternalError(file.getFileName().toString());
                                    }
                                }
                                default -> throw new FileSystemInternalError(file.getFileName().toString());
                            }
                        } else {
                            continue;
                        }
                    }
                }

                numScannedLines++;
            }
        } catch (IOException e) {
            throw new FileSystemInternalError(file.getFileName().toString());
        }

        if (!boardSizeSet || !winConditionSet || !hitConditionSet || !piecesSet || !specialPositionsSet
                || !diceRollsSet || !turnSequenceSet)
            throw new FileSystemInternalError(file.getFileName().toString());

        Board board = new GameBoard(boardWidth, boardHeight, specialPositions);
        DiceRolling diceRoller = diceRulesetIsStrict ? new DiceStreamFixed(diceRolls)
                : new DiceStreamUnbounded(diceRolls, new SingleDice(new JavaStlRandom(), 6));

        GameConfiguration configuration = new GameConfiguration(winCondition, hitCondition, diceRoller, board,
                turnSequence);

        PieceContainer pieceContainer = new LockingPieceContainer();

        for (PositionTrackingConverter p : pieceSets) {
            pieceContainer.registerNewPiece(new GamePiece(p, boardWidth, boardHeight));
        }

        return new GameStore(configuration, pieceContainer, diceRolls, file.getFileName().toString());
    }

    @Override
    public void saveGame(GameStore gameData) {
        String fileName = gameData.tag();

        GameConfiguration config = gameData.configuration();

        // Initial check to see if it needs an additional extension
        boolean fileNameIsntUnique = this.listOfSavesAsFiles.contains(
                this.mapFileNameToPath(fileName).getFileName().toString());

        int copyNumber = fileNameIsntUnique ? 1 : 0;

        while (fileNameIsntUnique) {
            if (this.listOfSavesAsFiles.contains(
                    this.mapFileNameToPath(fileName + " (" + copyNumber + ")").getFileName().toString())) {
                copyNumber += 1;
            } else {
                fileNameIsntUnique = false;
            }
        }

        fileName = (copyNumber == 0) ? fileName : fileName + " (" + copyNumber + ")";

        Path newFile = this.mapFileNameToPath(fileName);

        try (BufferedWriter writer = Files.newBufferedWriter(newFile)) {
            writer.write("24854995_GAME_SAVE");
            writer.newLine();

            writer.write("BoardSize " + config.board().getBoardWidth() + " " + config.board().getBoardHeight());
            writer.newLine();

            writer.write("HitCondition");
            writer.newLine();

            writer.write("WinCondition");
            writer.newLine();

            writer.write("DiceRule Strict");
            writer.newLine();
            writer.write("DiceRolls " + gameData.diceRolls().size());
            writer.newLine();

            writer.write("NumPieces " + gameData.pieces().getNumPieces());

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
            throw new GameIDInvalidException("Game Was unable to be serialised: " + e.path);
        }
    }

    @Override
    public List<String> getSavedGameOptions() {
        return this.listOfSavesAsFiles;
    }

    private Path mapFileNameToPath(String path) {
        return Path.of(this.pathToDirectory + "/" + path + "." + this.SAVE_FILE_CUSTOM_EXTENSION);
    }
}
