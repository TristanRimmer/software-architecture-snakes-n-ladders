package uk.ac.mmu.game.usecase;

import java.util.List;

public interface GameRepository {
    void saveGame(GameStore gameData);

    GameStore loadGame(int gameID) throws GameIDInvalidException;

    List<String> getSavedGameOptions();
}
