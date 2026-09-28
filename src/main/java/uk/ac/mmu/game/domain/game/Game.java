package uk.ac.mmu.game.domain.game;

import java.util.List;

import uk.ac.mmu.game.domain.events.GameEventPublisher;
import uk.ac.mmu.game.domain.game.state.GameState;
import uk.ac.mmu.game.domain.game.state.Ready;
import uk.ac.mmu.game.domain.pieces.Piece;

public class Game {
    private GameState state;

    private final GameConfiguration config;
    private final List<Piece> pieces;
    private final GameEventPublisher eventPublisher;

    public Game(
        GameConfiguration config,
        List<Piece> pieces,
        GameEventPublisher eventPublisher
    ) {
        this.config = config;
        this.pieces = pieces;
        this.eventPublisher = eventPublisher;
    }

    public void play() {
        this.state = new Ready();
        
        while(!this.state.isEndOfChain()) {
            this.state.execute(this);
            this.state = this.state.progessState();
        }
        // isEndOfChain terminates before that state gets executed, so run it one more time
        this.state.execute(this);
    };

    public GameConfiguration getConfig() {
        return this.config;
    }
    public List<Piece> getPieces() {
        return this.pieces;
    }
    public GameEventPublisher getEventPublisher() {
        return this.eventPublisher;
    }
}
