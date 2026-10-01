package uk.ac.mmu.game.infrastructure.persistence.FileSystem;

public class FileSystemGameRepositoryRuntimeError extends RuntimeException {
    public FileSystemGameRepositoryRuntimeError(String msg) {
        super(msg);
    }
}
