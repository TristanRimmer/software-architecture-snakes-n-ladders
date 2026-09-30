package uk.ac.mmu.game.infrastructure.serialisation;

import uk.ac.mmu.game.domain.game.state.gameturn.ComprehensiveGameTurn;
import uk.ac.mmu.game.domain.game.state.gameturn.GameTurn;
import uk.ac.mmu.game.domain.util.ImplFactoryException;

public class GameTurnSerialiser {
    public static final String COMPREHENSIVE_GAME_TURN = "Default";

    public static GameTurn getImplementationFromString(String name) throws ImplFactoryException {
        if (name.equals(COMPREHENSIVE_GAME_TURN))
            return new ComprehensiveGameTurn();

        throw new ImplFactoryException("Invalid variant passed to game turn factory");
    }

    public static String getStringFromImplementation(GameTurn impl) {
        return COMPREHENSIVE_GAME_TURN;
    }
}
