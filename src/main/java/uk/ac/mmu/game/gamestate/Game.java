package uk.ac.mmu.game.gamestate;

import java.util.List;

import uk.ac.mmu.game.observer.GameEventPublisher;
import uk.ac.mmu.game.pieces.PieceService;

public class Game {
    private GameState state;

    private final GameConfiguration config;
    private final List<PieceService> pieces;
    private final GameEventPublisher eventPublisher;

    public Game(
        GameConfiguration config,
        List<PieceService> pieces,
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
    public List<PieceService> getPieces() {
        return this.pieces;
    }
    public GameEventPublisher getEventPublisher() {
        return this.eventPublisher;
    }
}
