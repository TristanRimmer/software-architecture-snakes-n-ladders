package uk.ac.mmu.game.infrastructure.persistence;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import uk.ac.mmu.game.domain.game.GameStore;
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
    private List<GameStore> listOfSaves;

    public FileSystemGameRepository() {
        // Checks if it can access the home directory
        Path pathToHome = Path.of(USER_HOME_DIRECTORY);
        Path backupHomePath = Path.of(BACKUP_HOME_DIRECTORY);

        if (!Files.exists(pathToHome) && !Files.exists(backupHomePath))
            throw new FileSystemGameRepositoryRuntimeError(
            "Could not establish a path to either the home or current working directory");

        // If here, at least one worked
        Path chosenHome = Files.exists(pathToHome) ? pathToHome : backupHomePath;
        Path homeWithDir = Path.of(chosenHome + DIRECTORY_OF_SAVES);

        if (!Files.exists(homeWithDir)) {
            try {
                Files.createDirectory(homeWithDir);
            } catch (IOException e) {
                throw new FileSystemGameRepositoryRuntimeError("Could not create a directory for storing game saves: " + e.getMessage());
            }
        }

        // If here, there is an established path to the save directory
        this.pathToDirectory = homeWithDir;
        this.listOfSavesAsFiles = new ArrayList<>();
        this.listOfSaves = new ArrayList<>();

        try {
            List<Path> possibleSaves = Files.list(this.pathToDirectory).toList();   

            for (Path file : possibleSaves) {
                if (Files.isDirectory(file))
                    continue;

                if (!file.getFileName().toString().endsWith("." + SAVE_FILE_CUSTOM_EXTENSION))
                    continue;

                // Try serialise it and skip otherwise
                try {
                    this.listOfSaves.add(this.serialiseGameFileToGameStore(file));
                    this.listOfSavesAsFiles.add(file.getFileName().toString());
                } catch (FileSystemInternalError e) {
                    System.out.println("Warning: Could not serialise saved game at path " + e.path + ". Skipping..");   
                }  
            }
        } catch (IOException e) {
            throw new FileSystemGameRepositoryRuntimeError("Could not establish existing saves in save directory: " + e.getMessage());
        }      
    }

    private GameStore serialiseGameFileToGameStore(Path file) throws FileSystemInternalError {
        /**
         * File Layout:
         * Line 1: 24854995_GAME_SAVE // Similar to BMPs, its a tag
         * // The following should be any order
         * BoardSize X Y
         * HitCondition [hitConditionName]
         * WinCondition [winConditionName]
         * 
         * NumPieces [NumPiece] // Immediately followed by the piece movement pattern. In this comment, assume NumPiece to be 4
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
         * DiceRolls [NumDiceRolls] // Pretend its 7
         * 6 2 4 1 6 2 2
         */
        throw new FileSystemInternalError(file.getFileName().toString());
    }

    @Override
    public void saveGame(GameStore gameData) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'saveGame'");
    }

    @Override
    public GameStore loadGame(int gameID) throws GameIDInvalidException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'loadGame'");
    }

    @Override
    public List<String> getSavedGameOptions() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getSavedGameOptions'");
    }

}
