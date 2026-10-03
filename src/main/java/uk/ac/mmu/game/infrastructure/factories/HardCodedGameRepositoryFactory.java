package uk.ac.mmu.game.infrastructure.factories;

import uk.ac.mmu.game.infrastructure.persistence.FileSystem.FileSystemGameRepository;
import uk.ac.mmu.game.usecase.GameRepository;
import uk.ac.mmu.game.usecase.GameRepositoryFactory;

public class HardCodedGameRepositoryFactory implements GameRepositoryFactory {

    @Override
    public GameRepository getGameRepository() {
        // File System is more interesting
        // return new InMemoryGameRepository();
        return new FileSystemGameRepository();
    }
}
