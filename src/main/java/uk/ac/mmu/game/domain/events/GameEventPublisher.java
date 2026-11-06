package uk.ac.mmu.game.domain.events;

import java.util.ArrayList;

import uk.ac.mmu.game.domain.events.types.BetterGameStateTransition;
import uk.ac.mmu.game.domain.events.types.ConfigurableStateRelatedEvent;
import uk.ac.mmu.game.domain.events.types.DiceRolled;
import uk.ac.mmu.game.domain.events.types.GameEvent;
import uk.ac.mmu.game.domain.events.types.GameRelatedEvent;
import uk.ac.mmu.game.domain.events.types.HitConditionsPreventedMove;
import uk.ac.mmu.game.domain.events.types.PieceMove;
import uk.ac.mmu.game.domain.events.types.PieceRelatedEvent;
import uk.ac.mmu.game.domain.events.types.PieceWon;
import uk.ac.mmu.game.domain.events.types.PiecesHit;
import uk.ac.mmu.game.domain.events.types.SpecialPositionMovedPiece;
import uk.ac.mmu.game.domain.events.types.TemporaryNewPiecePosition;
import uk.ac.mmu.game.domain.events.types.TurnChange;
import uk.ac.mmu.game.domain.events.types.WinConditionPreventedWin;

public class GameEventPublisher {
    private ArrayList<ConfigurableStateRelatedEventSubscriber> configurableStateSubscribers;
    private ArrayList<GameRelatedEventSubscriber> gameRelatedEventSubscribers;
    private ArrayList<PieceRelatedEventSubscriber> pieceRelatedEventSubscribers;

    public GameEventPublisher() {
        this.configurableStateSubscribers = new ArrayList<>();
        this.gameRelatedEventSubscribers = new ArrayList<>();
        this.pieceRelatedEventSubscribers = new ArrayList<>();
    }

    public void registerNewSubscriber(GameEventSubscriber subscriber) {
        switch (subscriber) {
            case ConfigurableStateRelatedEventSubscriber sub -> {
                this.configurableStateSubscribers.add(sub);
            }
            case GameRelatedEventSubscriber sub -> {
                this.gameRelatedEventSubscribers.add(sub);
            }
            case PieceRelatedEventSubscriber sub -> {
                this.pieceRelatedEventSubscribers.add(sub);
            }
        }
    }

    public void publish(GameEvent event) {
        switch (event) {
            case ConfigurableStateRelatedEvent evt -> {
                this.configurableStateNotify(evt);
            }
            case PieceRelatedEvent evt -> {
                this.pieceRelatedNotify(evt);
            }
            case GameRelatedEvent evt -> {
                this.gameRelatedNotify(evt);
            }
        }
    }

    private void configurableStateNotify(ConfigurableStateRelatedEvent event) {
        for (ConfigurableStateRelatedEventSubscriber sub : this.configurableStateSubscribers) {
            switch (event) {
                case HitConditionsPreventedMove e -> sub.notify(e);
                case WinConditionPreventedWin e -> sub.notify(e);
                case SpecialPositionMovedPiece e -> sub.notify(e);
            }
        }
    }
    private void gameRelatedNotify(GameRelatedEvent event) {
        for (GameRelatedEventSubscriber sub : this.gameRelatedEventSubscribers) {
            switch (event) {
                case BetterGameStateTransition e -> sub.notify(e);
                case DiceRolled e -> sub.notify(e);
                case TurnChange e -> sub.notify(e);
                case PieceWon e -> sub.notify(e);
            }
        }
    }
    private void pieceRelatedNotify(PieceRelatedEvent event) {
        for (PieceRelatedEventSubscriber sub : this.pieceRelatedEventSubscribers) {
            switch (event) {
                case PieceMove e -> sub.notify(e);
                case PiecesHit e -> sub.notify(e);
                case PieceWon e -> sub.notify(e);
                case SpecialPositionMovedPiece e -> sub.notify(e);
                case TemporaryNewPiecePosition e -> sub.notify(e);
            }
        }
    }
}
