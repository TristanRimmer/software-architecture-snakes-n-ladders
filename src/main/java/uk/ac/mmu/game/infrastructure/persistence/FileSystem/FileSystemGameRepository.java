package uk.ac.mmu.game.infrastructure.persistence.FileSystem;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import uk.ac.mmu.game.domain.board.Board;
import uk.ac.mmu.game.domain.board.SquareBoard;
import uk.ac.mmu.game.domain.board.specialpositions.SpecialLinkedPositions;
import uk.ac.mmu.game.domain.board.specialpositions.SpecialPositionContainer;
import uk.ac.mmu.game.domain.board.specialpositions.TeleporterContainerDisallowingWinTeleporters;
import uk.ac.mmu.game.domain.dice.DiceRolling;
import uk.ac.mmu.game.domain.dice.GenerateRandomNumber;
import uk.ac.mmu.game.domain.dice.variations.SingleDice;
import uk.ac.mmu.game.domain.dice.variations.stream.DiceStreamFixed;
import uk.ac.mmu.game.domain.dice.variations.stream.DiceStreamUnbounded;
import uk.ac.mmu.game.domain.game.GameConfiguration;
import uk.ac.mmu.game.domain.game.gameturn.GameTurn;
import uk.ac.mmu.game.domain.pieces.positiontrackers.PositionTrackingConverter;
import uk.ac.mmu.game.domain.rules.hitcondition.CollisionCondition;
import uk.ac.mmu.game.domain.rules.wincondition.WinCondition;
import uk.ac.mmu.game.domain.util.GridPosition;
import uk.ac.mmu.game.domain.util.PieceConfigurationsList;
import uk.ac.mmu.game.infrastructure.persistence.FileSystem.componentdeserialisers.BoardDeserialiser;
import uk.ac.mmu.game.infrastructure.persistence.FileSystem.componentdeserialisers.DeserialisedObjectNotCreatedException;
import uk.ac.mmu.game.infrastructure.persistence.FileSystem.componentdeserialisers.GameSubcomponentDeserialiser;
import uk.ac.mmu.game.infrastructure.persistence.FileSystem.componentdeserialisers.diceroller.DiceRollsDeserialiser;
import uk.ac.mmu.game.infrastructure.persistence.FileSystem.componentdeserialisers.diceroller.IsDiceRuleStrictDeserialiser;
import uk.ac.mmu.game.infrastructure.persistence.FileSystem.componentdeserialisers.generaterandomnumber.GenerateRandomNumberDeserialiser;
import uk.ac.mmu.game.infrastructure.persistence.FileSystem.componentdeserialisers.generaterandomnumber.GenerateRandomNumberMapper;
import uk.ac.mmu.game.infrastructure.persistence.FileSystem.componentdeserialisers.hitcondition.HitConditionDeserialiser;
import uk.ac.mmu.game.infrastructure.persistence.FileSystem.componentdeserialisers.hitcondition.HitConditionMapper;
import uk.ac.mmu.game.infrastructure.persistence.FileSystem.componentdeserialisers.positiontrackingconverter.PiecePositionTrackerDeserialiser;
import uk.ac.mmu.game.infrastructure.persistence.FileSystem.componentdeserialisers.positiontrackingconverter.PositionTrackingConverterMapper;
import uk.ac.mmu.game.infrastructure.persistence.FileSystem.componentdeserialisers.teleporter.SpecialPositionDeserialiser;
import uk.ac.mmu.game.infrastructure.persistence.FileSystem.componentdeserialisers.teleporter.SpecialPositionMapper;
import uk.ac.mmu.game.infrastructure.persistence.FileSystem.componentdeserialisers.turnsequence.GameTurnMapper;
import uk.ac.mmu.game.infrastructure.persistence.FileSystem.componentdeserialisers.turnsequence.TurnSequenceDeserialiser;
import uk.ac.mmu.game.infrastructure.persistence.FileSystem.componentdeserialisers.wincondition.WinConditionDeserialiser;
import uk.ac.mmu.game.infrastructure.persistence.FileSystem.componentdeserialisers.wincondition.WinConditionMapper;
import uk.ac.mmu.game.usecase.GameIDInvalidException;
import uk.ac.mmu.game.usecase.GameRepository;
import uk.ac.mmu.game.usecase.types.GameStore;

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

    private final Path pathToDirectory;
    private List<String> listOfSavesAsFiles;

    public FileSystemGameRepository() {
        // Checks if it can access the home directory
        Path pathToHome = Path.of(USER_HOME_DIRECTORY);
        Path backupHomePath = Path.of(BACKUP_HOME_DIRECTORY);

        if (!Files.exists(pathToHome) && !Files.exists(backupHomePath))
            throw new FileSystemGameRepositoryRuntimeError(
                    "Could not establish a path to either the home or current working directory");

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

        System.out.println("(Established link to Game-Store Directory at " + homeWithDir.toString() + ")");

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

                this.listOfSavesAsFiles.add(this.getNameFromFile(file.getFileName().toString()));
            }
        } catch (IOException e) {
            throw new FileSystemGameRepositoryRuntimeError(
                    "Could not establish existing saves in save directory: " + e.getMessage());
        }
    }

    private GameStore serialiseGameFileToGameStore(Path file) throws FileSystemInternalError {
        // Instantiating them outside the map rather than explicitly in the map means i can avoid casts
        // when calling getObject()
        WinConditionDeserialiser winConditionDeserialiser = 
            new WinConditionDeserialiser(SavedGameHeaders.WIN_CONDITION_HEADER.value());
        HitConditionDeserialiser hitConditionDeserialiser = 
            new HitConditionDeserialiser(SavedGameHeaders.HIT_CONDITION_HEADER.value());
        TurnSequenceDeserialiser turnSequenceDeserialiser = 
            new TurnSequenceDeserialiser(SavedGameHeaders.TURN_SEQUENCE_HEADER.value());
        SpecialPositionDeserialiser specialPositionDeserialiser = 
            new SpecialPositionDeserialiser(SavedGameHeaders.SPECIAL_POSITION_HEADER.value());
        BoardDeserialiser boardDeserialiser = 
            new BoardDeserialiser(SquareBoard.class, SavedGameHeaders.BOARD_SIZE_HEADER.value());
        PiecePositionTrackerDeserialiser piecePositionTrackerDeserialiser = 
            new PiecePositionTrackerDeserialiser(SavedGameHeaders.PIECES_HEADER.value());
        GenerateRandomNumberDeserialiser randomNumberDeserialiser = 
            new GenerateRandomNumberDeserialiser(SavedGameHeaders.RANDOM_NUMBER_GENERATOR.value());
        DiceRollsDeserialiser diceRollsDeserialiser = 
            new DiceRollsDeserialiser(SavedGameHeaders.DICE_ROLLS_HEADER.value());
        IsDiceRuleStrictDeserialiser diceRuleDeserialiser = 
            new IsDiceRuleStrictDeserialiser(
                SavedGameHeaders.DICE_RULE_HEADER.value(), 
                SavedGameHeaders.DICE_RULE_STRICT.value(), 
                SavedGameHeaders.DICE_RULE_LENIENT.value());

        List<GameSubcomponentDeserialiser<?>> deserialisers = new ArrayList<>(
            List.of(
                winConditionDeserialiser, 
                hitConditionDeserialiser, 
                turnSequenceDeserialiser, 
                specialPositionDeserialiser, 
                boardDeserialiser, 
                piecePositionTrackerDeserialiser, 
                randomNumberDeserialiser,
                diceRollsDeserialiser,
                diceRuleDeserialiser
            )
        );

        try (BufferedReader reader = new BufferedReader(new FileReader(file.toString()))) {
            String currentLine;

            while ((currentLine = reader.readLine()) != null) {
                for (GameSubcomponentDeserialiser<?> obj : deserialisers) {
                    obj.loadNextLine(currentLine);
                }
            }
        } catch (IOException e) {
            throw new FileSystemInternalError(file.getFileName().toString(), "Error Reading the game file");
        }

        try {
            WinCondition winCondition = winConditionDeserialiser.getObject();
            CollisionCondition collisionCondition = hitConditionDeserialiser.getObject();
            GameTurn turnSequence = turnSequenceDeserialiser.getObject();
            boolean isStrictDiceRules = diceRuleDeserialiser.getObject();
            GenerateRandomNumber randomNumberGenerator = randomNumberDeserialiser.getObject();
            ArrayList<Integer> diceRolls = diceRollsDeserialiser.getObject();
            PieceConfigurationsList pieceMoveset =  piecePositionTrackerDeserialiser.getObject();
            ArrayList<SpecialLinkedPositions> specialPositions = specialPositionDeserialiser.getObject();
            Board board = boardDeserialiser.getObject();

            SpecialPositionContainer fileSystemSpecialPositionContainer = 
                new TeleporterContainerDisallowingWinTeleporters(board.getBoardWidth(), board.getBoardHeight());

            for (SpecialLinkedPositions positions : specialPositions) {
                fileSystemSpecialPositionContainer.registerNewSpecialPosition(positions);
            }

            DiceRolling diceRoller = isStrictDiceRules ? new DiceStreamFixed(diceRolls)
                : new DiceStreamUnbounded(diceRolls, new SingleDice(randomNumberGenerator, 6));

            return new GameStore(
                new GameConfiguration(
                    winCondition, 
                    collisionCondition, 
                    diceRoller, 
                    board, 
                    fileSystemSpecialPositionContainer, 
                    turnSequence
                ), 
                pieceMoveset, 
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
            writer.write(SavedGameHeaders.GAME_SAVE_FILE_HEADER.value());
            writer.newLine();

            writer.write(
                    SavedGameHeaders.BOARD_SIZE_HEADER.value() + " " + config.board().getBoardWidth().getValue() + " " + config.board().getBoardHeight().getValue());
            writer.newLine();

            writer.write(SavedGameHeaders.SPECIAL_POSITION_HEADER.value() + " " + config.specialPositions().getSpecialPositions().size());
            writer.newLine();

            for (SpecialLinkedPositions pos : config.specialPositions().getSpecialPositions()) {
                String string = SpecialPositionMapper.getStringFromImplemention(pos);

                for (GridPosition currPos : pos.getListOfSpecialPositions()) {
                    string = string + " " + currPos.x() + " " + currPos.y();                    
                }

                writer.write(string);
                writer.newLine();
            }

            writer.write(SavedGameHeaders.HIT_CONDITION_HEADER.value() +
                    " " + HitConditionMapper.getStringFromImplementation(config.collisionEvaluator()));
            writer.newLine();

            writer.write(SavedGameHeaders.WIN_CONDITION_HEADER.value() + " "
                    + WinConditionMapper.getStringFromImplementation(config.winEvaluator()));
            writer.newLine();

            writer.write(
                    SavedGameHeaders.TURN_SEQUENCE_HEADER.value() + " " + GameTurnMapper.getStringFromImplementation(config.turnSequence()));
            writer.newLine();

            writer.write(SavedGameHeaders.DICE_RULE_HEADER.value() + " " + SavedGameHeaders.DICE_RULE_STRICT.value());
            writer.newLine();

            writer.write(SavedGameHeaders.DICE_ROLLS_HEADER.value() + " " + gameData.diceRolls().size());
            writer.newLine();

            // INFO: On replay, the old configuration doesn't matter, so its always unseeded
            writer.write(SavedGameHeaders.RANDOM_NUMBER_GENERATOR.value() + " " + GenerateRandomNumberMapper.JAVA_STL_UNSEEDED);
            writer.newLine();

            for (Integer roll : gameData.diceRolls()) {
                writer.write(Integer.toString(roll));
                writer.newLine();
            }

            writer.write(SavedGameHeaders.PIECES_HEADER.value() + " " + gameData.pieces().converters().size());
            writer.newLine();

            for (PositionTrackingConverter piece : gameData.pieces().converters()) {
                writer.write(
                        PositionTrackingConverterMapper.getStringFromImplementation(piece));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new FileSystemGameRepositoryRuntimeError("Failed to serialise recent game: " + e);
        }

        System.out.println("Saved to " + newFile.getFileName().toString());
        this.listOfSavesAsFiles.add(gameData.tag());
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
