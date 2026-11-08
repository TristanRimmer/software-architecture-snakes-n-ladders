package uk.ac.mmu.game.domain.game;

import uk.ac.mmu.game.domain.events.GameEventPublisher;
import uk.ac.mmu.game.domain.game.state.GameState;
import uk.ac.mmu.game.domain.game.state.Ready;
import uk.ac.mmu.game.domain.pieces.GamePiece;
import uk.ac.mmu.game.domain.pieces.container.LockingPieceContainer;
import uk.ac.mmu.game.domain.pieces.container.PieceContainer;
import uk.ac.mmu.game.domain.pieces.positiontrackers.PositionTrackingConverter;
import uk.ac.mmu.game.domain.util.PieceConfigurationsList;

public class Game {
    private GameState state;

    private final GameConfiguration config;
    private final PieceConfigurationsList pieceTypes;
    private final PieceContainer pieces;
    private final GameEventPublisher eventPublisher;

    public Game(
        GameConfiguration config,
        PieceConfigurationsList pieceTypes,
        GameEventPublisher eventPublisher
    ) {
        this.config = config;
        this.pieceTypes = pieceTypes;

        this.pieces = new LockingPieceContainer();

        for (PositionTrackingConverter ptc : this.pieceTypes.converters()) {
            this.pieces.registerNewPiece(
                new GamePiece(ptc, this.config.board().getBoardWidth(), this.config.board().getBoardHeight())
            );
        }

        this.eventPublisher = eventPublisher;
    }

    public void play() {
        this.state = new Ready();
        
        while(!this.state.isEndOfChain()) {
            this.state = this.state.execute(this);
        }
        // isEndOfChain terminates before that state gets executed, so run it one more time
        this.state.execute(this);
    };

    public GameConfiguration getConfig() {
        return this.config;
    }
    public PieceContainer getPieces() {
        return this.pieces;
    }
    public GameEventPublisher getEventPublisher() {
        return this.eventPublisher;
    }
    public PieceConfigurationsList getPieceTypes() {
        return this.pieceTypes;
    }
}
