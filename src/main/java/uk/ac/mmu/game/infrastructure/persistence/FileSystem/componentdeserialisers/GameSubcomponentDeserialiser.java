package uk.ac.mmu.game.infrastructure.persistence.FileSystem.componentdeserialisers;

public interface GameSubcomponentDeserialiser<T> {
    void loadNextLine(String line);

    boolean loadedObjectSuccessfully();

    T getObject() throws DeserialisedObjectNotCreatedException; 
}
