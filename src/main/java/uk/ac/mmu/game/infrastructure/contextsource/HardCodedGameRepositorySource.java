package uk.ac.mmu.game.infrastructure.contextsource;

import uk.ac.mmu.game.infrastructure.persistence.FileSystem.FileSystemGameRepository;
import uk.ac.mmu.game.usecase.GameRepository;

public class HardCodedGameRepositorySource {
    public static GameRepository getGameRepository() {
        // File System is more interesting
        return new FileSystemGameRepository();
    }
}
