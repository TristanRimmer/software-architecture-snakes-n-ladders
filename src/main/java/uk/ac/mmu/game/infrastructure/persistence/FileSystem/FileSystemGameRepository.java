package uk.ac.mmu.game.infrastructure.persistence.FileSystem;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import uk.ac.mmu.game.domain.board.Board;
import uk.ac.mmu.game.domain.board.GameBoard;
import uk.ac.mmu.game.domain.board.specialpositions.SpecialLinkedPositions;
import uk.ac.mmu.game.domain.dice.DiceRolling;
import uk.ac.mmu.game.domain.dice.GenerateRandomNumber;
import uk.ac.mmu.game.domain.dice.variations.SingleDice;
import uk.ac.mmu.game.domain.dice.variations.stream.DiceStreamFixed;
import uk.ac.mmu.game.domain.dice.variations.stream.DiceStreamUnbounded;
import uk.ac.mmu.game.domain.game.GameConfiguration;
import uk.ac.mmu.game.domain.game.GameStore;
import uk.ac.mmu.game.domain.game.state.gameturn.GameTurn;
import uk.ac.mmu.game.domain.pieces.GamePiece;
import uk.ac.mmu.game.domain.pieces.Piece;
import uk.ac.mmu.game.domain.pieces.container.LockingPieceContainer;
import uk.ac.mmu.game.domain.pieces.container.PieceContainer;
import uk.ac.mmu.game.domain.pieces.positiontrackers.PositionTrackingConverter;
import uk.ac.mmu.game.domain.rules.hitcondition.CollisionCondition;
import uk.ac.mmu.game.domain.rules.wincondition.WinCondition;
import uk.ac.mmu.game.infrastructure.persistence.FileSystem.componentdeserialisers.BoardDeserialiser;
import uk.ac.mmu.game.infrastructure.persistence.FileSystem.componentdeserialisers.DeserialisedObjectNotCreatedException;
import uk.ac.mmu.game.infrastructure.persistence.FileSystem.componentdeserialisers.DiceRollsDeserialiser;
import uk.ac.mmu.game.infrastructure.persistence.FileSystem.componentdeserialisers.GamSubcomponentDeserialiser;
import uk.ac.mmu.game.infrastructure.persistence.FileSystem.componentdeserialisers.GenerateRandomNumberDeserialiser;
import uk.ac.mmu.game.infrastructure.persistence.FileSystem.componentdeserialisers.HitConditionDeserialiser;
import uk.ac.mmu.game.infrastructure.persistence.FileSystem.componentdeserialisers.IsDiceRuleStrictDeserialiser;
import uk.ac.mmu.game.infrastructure.persistence.FileSystem.componentdeserialisers.PiecePositionTrackerDeserialiser;
import uk.ac.mmu.game.infrastructure.persistence.FileSystem.componentdeserialisers.SpecialPositionDeserialiser;
import uk.ac.mmu.game.infrastructure.persistence.FileSystem.componentdeserialisers.TurnSequenceDeserialiser;
import uk.ac.mmu.game.infrastructure.persistence.FileSystem.componentdeserialisers.WinConditionDeserialiser;
import uk.ac.mmu.game.infrastructure.serialisation.GameTurnSerialiser;
import uk.ac.mmu.game.infrastructure.serialisation.HitConditionSerialiser;
import uk.ac.mmu.game.infrastructure.serialisation.PositionTrackingConverterSerialiser;
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
    private static final String DICE_RULE_HEADER = "DiceRule";
    private static final String DICE_RULE_STRICT = "Strict";
    private static final String DICE_RULE_LENIENT = "Lenient";

    private static final String TURN_SEQUENCE_HEADER = "TurnSequence";
    private static final String SPECIAL_POSITION_HEADER = "NumSpecialPositions";
    private static final String DICE_ROLLS_HEADER = "DiceRolls";
    private static final String PIECES_HEADER = "NumPieces";
    private static final String GAME_SAVE_FILE_HEADER = "24854995_Game_Save";
    private static final String WIN_CONDITION_HEADER = "WinCondition";
    private static final String HIT_CONDITION_HEADER = "HitCondition";
    private static final String BOARD_SIZE_HEADER = "BoardSize";
    private static final String RANDOM_NUMBER_GENERATOR = "RandomNumbersSource";

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
        // TODO: Could i apply builder pattern here?
        HashMap<String, GamSubcomponentDeserialiser<?>> deserialisers = new HashMap<>();

        deserialisers.put(WIN_CONDITION_HEADER, new WinConditionDeserialiser(WIN_CONDITION_HEADER));
        deserialisers.put(HIT_CONDITION_HEADER, new HitConditionDeserialiser(HIT_CONDITION_HEADER));
        deserialisers.put(DICE_RULE_HEADER, new IsDiceRuleStrictDeserialiser(DICE_RULE_HEADER, DICE_RULE_STRICT, DICE_RULE_LENIENT));
        deserialisers.put(DICE_ROLLS_HEADER, new DiceRollsDeserialiser(DICE_ROLLS_HEADER));
        deserialisers.put(TURN_SEQUENCE_HEADER, new TurnSequenceDeserialiser(TURN_SEQUENCE_HEADER));
        deserialisers.put(SPECIAL_POSITION_HEADER, new SpecialPositionDeserialiser(SPECIAL_POSITION_HEADER));
        deserialisers.put(BOARD_SIZE_HEADER, new BoardDeserialiser(GameBoard.class, BOARD_SIZE_HEADER));
        deserialisers.put(PIECES_HEADER, new PiecePositionTrackerDeserialiser(PIECES_HEADER));
        deserialisers.put(RANDOM_NUMBER_GENERATOR, new GenerateRandomNumberDeserialiser(RANDOM_NUMBER_GENERATOR));

        try (BufferedReader reader = new BufferedReader(new FileReader(file.toString()))) {
            String currentLine;

            while ((currentLine = reader.readLine()) != null) {
                for (GamSubcomponentDeserialiser<?> obj : deserialisers.values()) {
                    obj.loadNextLine(currentLine);
                }
            }
        } catch (IOException e) {
            throw new FileSystemInternalError(file.getFileName().toString(), "Error Reading the game file");
        }

        // These casts only work as the concrete implementations have a fixed type parameter
        try {
            WinCondition winCondition = (WinCondition)deserialisers.get(WIN_CONDITION_HEADER).getObject();
            CollisionCondition collisionCondition = (CollisionCondition)deserialisers.get(HIT_CONDITION_HEADER).getObject();
            GameTurn turnSequence = (GameTurn)deserialisers.get(TURN_SEQUENCE_HEADER).getObject();
            boolean isStrictDiceRules = (Boolean)deserialisers.get(DICE_RULE_HEADER).getObject();
            GenerateRandomNumber randomNumberGenerator = (GenerateRandomNumber)deserialisers.get(RANDOM_NUMBER_GENERATOR).getObject();

            // In the implementations, they are not a generic List they are specifcially an ArrayList
            ArrayList<Integer> diceRolls = (ArrayList<Integer>)deserialisers.get(DICE_ROLLS_HEADER).getObject();

            ArrayList<PositionTrackingConverter> pieceMoveset = 
                (ArrayList<PositionTrackingConverter>)deserialisers.get(PIECES_HEADER).getObject();

            ArrayList<SpecialLinkedPositions> specialPositions = 
                (ArrayList<SpecialLinkedPositions>)deserialisers.get(SPECIAL_POSITION_HEADER).getObject();

            Board board = (Board)deserialisers.get(BOARD_SIZE_HEADER).getObject();

            for (SpecialLinkedPositions positions : specialPositions) {
                board.registerNewSpecialPosition(positions);
            }

            DiceRolling diceRoller = isStrictDiceRules ? new DiceStreamFixed(diceRolls)
                : new DiceStreamUnbounded(diceRolls, new SingleDice(randomNumberGenerator, 6));

            PieceContainer pieceContainer = new LockingPieceContainer();

            for (PositionTrackingConverter p : pieceMoveset) {
                pieceContainer.registerNewPiece(new GamePiece(p, board.getBoardWidth(), board.getBoardHeight()));
            }

            return new GameStore(
                new GameConfiguration(winCondition, collisionCondition, diceRoller, board, turnSequence), 
                pieceContainer, 
                diceRolls, 
                this.getNameFromFile(file.getFileName().toString()));
        } catch (DeserialisedObjectNotCreatedException e) {
            throw new FileSystemInternalError(file.getFileName().toString(),
                    "Game File did not specify all required configurations: " + e);
        }
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
        return Path.of(this.pathToDirectory + "/" + path + "." + SAVE_FILE_CUSTOM_EXTENSION);
    }

    private String getNameFromFile(String fileName) {
        return fileName.substring(
                0,
                fileName.length() - SAVE_FILE_CUSTOM_EXTENSION.length() - 1);
    }
}
