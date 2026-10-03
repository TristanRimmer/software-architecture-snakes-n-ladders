package uk.ac.mmu.game.usecase;

import java.util.List;

import uk.ac.mmu.game.usecase.types.GameStore;

public interface GameRepository {
    void saveGame(GameStore gameData);

    GameStore loadGame(int gameID) throws GameIDInvalidException;

    List<String> getSavedGameOptions();
}
