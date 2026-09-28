package uk.ac.mmu.game.gamestate;

import java.util.ArrayList;

import uk.ac.mmu.game.board.BoardService;
import uk.ac.mmu.game.diceroller.DiceRollingService;
import uk.ac.mmu.game.hitcondition.PieceCollisionService;
import uk.ac.mmu.game.observer.GameEventPublisher;
import uk.ac.mmu.game.pieces.PieceService;
import uk.ac.mmu.game.wincondition.WinEvaluationService;

public class Game {
    private GameState state;

    // These are the properties that will vary per game and should be created on initialisation
    private BoardService board;
    private ArrayList<PieceService> pieces;
    private DiceRollingService diceRoller;
    private WinEvaluationService winEvaluator;
    private PieceCollisionService collisionService;
    private GameEventPublisher eventPublisher;

    public Game(
        BoardService board, 
        ArrayList<PieceService> pieces, 
        DiceRollingService diceRoller, 
        WinEvaluationService winEvaluator, 
        PieceCollisionService collisionService,
        GameEventPublisher eventPublisher
    ) {
        this.board = board;
        this.pieces = pieces;
        this.diceRoller = diceRoller;
        this.winEvaluator = winEvaluator;
        this.collisionService = collisionService;
        this.eventPublisher = eventPublisher;
    }

    public void play() {
        this.state = new Ready();
        this.state.execute(this);
        // Progresses to the InPlay state
        this.state = this.state.progessState();
        this.state.execute(this);
        // Progresses to the final state
        this.state = this.state.progessState();
        this.state.execute(this);
    };

    public BoardService getBoard() {
        return this.board;
    }
    public ArrayList<PieceService> getPieces() {
        return this.pieces;
    }
    public DiceRollingService getDiceRoller() {
        return this.diceRoller;
    }
    public WinEvaluationService getWinEvaluator() {
        return this.winEvaluator;
    }
    public PieceCollisionService getCollisionService() {
        return this.collisionService;
    }
    public GameEventPublisher getEventPublisher() {
        return this.eventPublisher;
    }
}
