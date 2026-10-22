package uk.ac.mmu.game.infrastructure.persistence;

import java.util.ArrayList;
import java.util.List;

import uk.ac.mmu.game.domain.board.Board;
import uk.ac.mmu.game.domain.board.specialpositions.SpecialPositionContainer;
import uk.ac.mmu.game.domain.dice.variations.stream.DiceStreamFixed;
import uk.ac.mmu.game.domain.game.GameConfiguration;
import uk.ac.mmu.game.domain.game.gameturn.GameTurn;
import uk.ac.mmu.game.domain.rules.hitcondition.CollisionCondition;
import uk.ac.mmu.game.domain.rules.wincondition.WinCondition;
import uk.ac.mmu.game.domain.util.PieceConfigurationsList;
import uk.ac.mmu.game.usecase.GameIDInvalidException;
import uk.ac.mmu.game.usecase.GameRepository;
import uk.ac.mmu.game.usecase.types.GameStore;

public class InMemoryGameRepository implements GameRepository {
    // The provided gamestore contains details about a game that are immutable, but using it directly
    // will not allow the direct replaying of games. Hence this class
    private record LocalGameStore(
        WinCondition winCondition, 
        CollisionCondition collisionCondition, 
        Board board, 
        SpecialPositionContainer specialPositions,
        GameTurn turn, 
        List<Integer> diceRolls,
        PieceConfigurationsList pieceConfig,
        String tag
    ) {}

    private final List<LocalGameStore> storedGames;

    public InMemoryGameRepository() {
        this.storedGames = new ArrayList<>();
    }

    @Override
    public void saveGame(GameStore gameData) {
        this.storedGames.add(
            new LocalGameStore(
                gameData.configuration().winEvaluator(), 
                gameData.configuration().collisionEvaluator(), 
                gameData.configuration().board(), 
                gameData.configuration().specialPositions(),
                gameData.configuration().turnSequence(), 
                gameData.diceRolls(), 
                gameData.pieces(),
                gameData.tag()));
    }

    @Override
    public GameStore loadGame(int gameID) throws GameIDInvalidException {
        if (gameID < 0 || gameID >= this.storedGames.size())
            throw new GameIDInvalidException("Game with that Index/ID does not exist");

        LocalGameStore chosenGame = this.storedGames.get(gameID);

        GameStore gameStore = new GameStore(
            new GameConfiguration(
                chosenGame.winCondition(), 
                chosenGame.collisionCondition(), 
                // Can be a fixed dice stream as these are in memory and therefore cant be modifed anyway
                new DiceStreamFixed(chosenGame.diceRolls()), 
                chosenGame.board(), chosenGame.specialPositions(), chosenGame.turn()), 
            chosenGame.pieceConfig(), chosenGame.diceRolls(), chosenGame.tag());
    
        return gameStore;
    }

    @Override
    public List<String> getSavedGameOptions() {
        return this.storedGames.stream().map(game -> game.tag()).toList();
    }
}
