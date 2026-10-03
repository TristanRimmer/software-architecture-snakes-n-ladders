package uk.ac.mmu.game.infrastructure.factories;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import uk.ac.mmu.game.domain.board.Board;
import uk.ac.mmu.game.domain.board.GameBoard;
import uk.ac.mmu.game.domain.board.specialpositions.OneWayTeleporter;
import uk.ac.mmu.game.domain.board.specialpositions.SpecialLinkedPositions;
import uk.ac.mmu.game.domain.board.specialpositions.TwoWayTeleporter;
import uk.ac.mmu.game.domain.dice.DiceRolling;
import uk.ac.mmu.game.domain.dice.GenerateRandomNumber;
import uk.ac.mmu.game.domain.dice.variations.SingleDice;
import uk.ac.mmu.game.domain.dice.variations.TwoDice;
import uk.ac.mmu.game.domain.game.GameConfiguration;
import uk.ac.mmu.game.domain.game.gameturn.ComprehensiveGameTurn;
import uk.ac.mmu.game.domain.game.gameturn.GameTurn;
import uk.ac.mmu.game.domain.pieces.positiontrackers.LowerLeftOrigin;
import uk.ac.mmu.game.domain.pieces.positiontrackers.LowerRightOrigin;
import uk.ac.mmu.game.domain.pieces.positiontrackers.UpperLeftOrigin;
import uk.ac.mmu.game.domain.pieces.positiontrackers.UpperRightOrigin;
import uk.ac.mmu.game.domain.rules.hitcondition.CollisionCondition;
import uk.ac.mmu.game.domain.rules.hitcondition.HitsDoNothing;
import uk.ac.mmu.game.domain.rules.hitcondition.HitsForfeitTurn;
import uk.ac.mmu.game.domain.rules.wincondition.CrossTheFinishline;
import uk.ac.mmu.game.domain.rules.wincondition.ExactHit;
import uk.ac.mmu.game.domain.rules.wincondition.WinCondition;
import uk.ac.mmu.game.domain.util.GridPosition;
import uk.ac.mmu.game.infrastructure.implmappers.HitConditionMapper;
import uk.ac.mmu.game.infrastructure.implmappers.WinConditionMapper;
import uk.ac.mmu.game.infrastructure.persistence.FileSystem.FileSystemGameRepository;
import uk.ac.mmu.game.infrastructure.persistence.InMemoryGameRepository;
import uk.ac.mmu.game.infrastructure.random.JavaStlRandom;
import uk.ac.mmu.game.usecase.GameRepository;
import uk.ac.mmu.game.usecase.types.PieceConfigurationsList;

@Configuration
public class SpringBootConfiguration {
    @Bean
    public GameRepository gameRepository(
        @Value("${game.repository}") String type 
    ) {
        return switch (type) {
            case "filesystem" -> new FileSystemGameRepository();
            case "memory" -> new InMemoryGameRepository();
            default -> throw new IllegalArgumentException("Unknown Repository: options are 'filesystem' and 'memory");
        };
    }
    @Bean 
    public WinCondition winCondition(
        @Value("${game.wincondition}") String type) {
        return switch (type) {
            case WinConditionMapper.CROSS_THE_FINISH_LINE -> new CrossTheFinishline();
            case WinConditionMapper.EXACT_HIT -> new ExactHit();
            default -> throw new IllegalArgumentException("Unknown wincondition: " + type);
        };
    }
    @Bean
    public CollisionCondition hitCondition(
        @Value("${game.hitcondition}") String type) {
        return switch (type) {
            case HitConditionMapper.HITS_DO_NOTHING -> new HitsDoNothing();
            case HitConditionMapper.HITS_FORFEIT_TURN -> new HitsForfeitTurn();
            default -> throw new IllegalArgumentException("Unknown hitcondition: " + type);
        };
    }
    @Bean
    public PieceConfigurationsList pieceConfiguration(
        @Value("${game.numberofpieces}") String pieceCount) {
        return switch (pieceCount) {
            case "2" -> new PieceConfigurationsList(List.of(new LowerLeftOrigin(), new UpperRightOrigin())); 
            case "4" -> new PieceConfigurationsList(List.of(new LowerLeftOrigin(), new UpperRightOrigin(), new LowerRightOrigin(), new UpperLeftOrigin())); 
            default -> throw new IllegalArgumentException(
                "Spring Boot App only wants 2 or 4. If you want this to change, use a file system repository and modify it in there.");
        };
    }

    @Bean 
    public Board board(
        @Value("${game.numberofpieces}") String pieceCount
    ) {
        return switch (pieceCount) {
            case "2" -> new GameBoard(5, 5);
            case "4" -> new GameBoard(6, 6); 
            default -> throw new IllegalArgumentException(
                "Spring Boot App only wants 2 or 4 pieces. If you want this to change, use a file system repository and modify it in there.");
        };
    }
    @Bean
    public DiceRolling diceRolling(
        @Value("${game.numberofdice}") String diceCount,
        GenerateRandomNumber rng
    ) {
        return switch (diceCount) {
            case "1" -> new SingleDice(rng, 6);
            case "2" -> new TwoDice(rng, 6); 
            default -> throw new IllegalArgumentException(
                "Spring Boot App only wants 1 or 2 dice. ");
        };
    }
    @Bean 
    public GenerateRandomNumber randomNumberGenerator() {
        return new JavaStlRandom();
    }
    @Bean
    public GameTurn turnSequence() {
        return new ComprehensiveGameTurn();
    }
    @Bean 
    public List<SpecialLinkedPositions> getSpecialPositions(
        @Value("${game.teleporterstrategy}") String type,
        @Value("${game.numberofpieces}") String pieceCount
    ) {
        if (!pieceCount.equals("2") && !pieceCount.equals("4"))
            throw new IllegalArgumentException(
                "Spring Boot App only wants 2 or 4 pieces. If you want this to change, use a file system repository and modify it in there.");
        
        // 2 Fixed-sets of grid point pairs
        GridPosition posA1 = new GridPosition(1, 1);
        GridPosition posA2 = new GridPosition(4, 1);
        GridPosition posB1 = new GridPosition(3, 2);
        GridPosition posB2 = new GridPosition(3, 2);

        return switch (type) {
            case "twowayteleporter" -> List.of(
                new TwoWayTeleporter(posA1, posA2),
                new TwoWayTeleporter(posB1, posB2)
            );
            case "onewayteleporter" -> List.of(
                new OneWayTeleporter(posA1, posA2),
                new OneWayTeleporter(posB1, posB2)
            );
            case "mix" -> List.of(
                new OneWayTeleporter(posA1, posA2),
                new TwoWayTeleporter(posB1, posB2)
            );
            case "noteleporter" -> new ArrayList<>();
            default -> throw new IllegalArgumentException("Invalid teleporterstrategy choice: " + type);
        };
    }
    @Bean 
    public GameConfiguration gameConfiguration(
        WinCondition winEvaluator,
        CollisionCondition collisionEvaluator,
        DiceRolling diceRoller,
        Board board,
        GameTurn turnSequence,
        List<SpecialLinkedPositions> specialPositions
    ) {
        for (SpecialLinkedPositions p : specialPositions)
            if (!board.registerNewSpecialPosition(p))
                System.out.println("Skipping Teleporter as its positions aren't valid");

        return new GameConfiguration(winEvaluator, collisionEvaluator, diceRoller, board, turnSequence);
    }
}

