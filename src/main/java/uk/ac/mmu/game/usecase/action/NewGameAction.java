package uk.ac.mmu.game.usecase.action;

import uk.ac.mmu.game.domain.events.GameEventPublisher;
import uk.ac.mmu.game.domain.game.Game;
import uk.ac.mmu.game.domain.game.GameConfiguration;
import uk.ac.mmu.game.domain.pieces.GamePiece;
import uk.ac.mmu.game.domain.pieces.container.LockingPieceContainer;
import uk.ac.mmu.game.domain.pieces.container.PieceContainer;
import uk.ac.mmu.game.domain.pieces.positiontrackers.PositionTrackingConverter;
import uk.ac.mmu.game.infrastructure.input.CommandLineInterface;
import uk.ac.mmu.game.infrastructure.subscribers.DiceRollRecorder;
import uk.ac.mmu.game.infrastructure.subscribers.MetadataDumper;
import uk.ac.mmu.game.usecase.GameRepository;
import uk.ac.mmu.game.usecase.types.GameStore;
import uk.ac.mmu.game.usecase.types.PieceConfigurationsList;

public final class NewGameAction implements GameAction {
    private final GameConfiguration configuration;
    private final PieceContainer pieces;
    private final GameEventPublisher publisher;
    private final GameRepository repository;
    
    public NewGameAction(
        GameConfiguration configuration,
        PieceConfigurationsList pieceConfigs,
        GameEventPublisher publisher,
        GameRepository repository
    ) {
        this.configuration = configuration;
        this.publisher = publisher;
        this.repository = repository;
        this.pieces = new LockingPieceContainer();

        for (PositionTrackingConverter ptc : pieceConfigs.converters()) {
            this.pieces.registerNewPiece(
                new GamePiece(ptc, configuration.board().getBoardWidth(), configuration.board().getBoardHeight())
            );
        }
    }

    @Override 
    public void run() {
        DiceRollRecorder diceRollTracker = new DiceRollRecorder();
        MetadataDumper dataDumper = new MetadataDumper(this.configuration, this.pieces);

        // A new game needs a dice tracker so it can save the rolls whilst adhering to SRP
        this.publisher.registerNewSubscriber(diceRollTracker);
        this.publisher.registerNewSubscriber(dataDumper);

        Game game = new Game(this.configuration, this.pieces, this.publisher);

        game.play();

        String gameName = 
            CommandLineInterface.askQuestionAndGetStringBack("Choose a name for this game save (or leave blank to stop saving): ");
        
        if (gameName.strip().isBlank())
            // They chose not to save
            return;

        repository.saveGame(
            new GameStore(
                this.configuration,
                this.pieces,
                diceRollTracker.getListOfDiceRolls(),
                gameName
            )
        );
    }

    @Override
    public boolean endOfSession() {
        return false;
    }
}
