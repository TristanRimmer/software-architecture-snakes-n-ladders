package uk.ac.mmu.game.infrastructure.persistence;

import java.util.ArrayList;
import java.util.List;

import uk.ac.mmu.game.usecase.GameIDInvalidException;
import uk.ac.mmu.game.usecase.GameRepository;
import uk.ac.mmu.game.usecase.GameStore;

public class InMemoryGameRepository implements GameRepository {
    private final List<GameStore> storedGames;

    public InMemoryGameRepository() {
        this.storedGames = new ArrayList<>();
    }

    @Override
    public void saveGame(GameStore gameData) {
        this.storedGames.add(gameData);
    }

    @Override
    public GameStore loadGame(int gameID) throws GameIDInvalidException {
        if (gameID < 0 || gameID >= this.storedGames.size())
            throw new GameIDInvalidException("Game with that Index/ID does not exist");

        return this.storedGames.get(gameID);
    }

    @Override
    public List<String> getSavedGameOptions() {
        return this.storedGames.stream().map(game -> game.tag()).toList();
    }
}
