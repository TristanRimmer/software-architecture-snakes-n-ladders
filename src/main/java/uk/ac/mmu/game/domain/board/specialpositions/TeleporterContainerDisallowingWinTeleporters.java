package uk.ac.mmu.game.domain.board.specialpositions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import uk.ac.mmu.game.domain.pieces.Piece;
import uk.ac.mmu.game.domain.util.GridPosition;
import uk.ac.mmu.game.domain.util.PositiveIntegerAtleastFive;

public class TeleporterContainerDisallowingWinTeleporters implements SpecialPositionContainer {
    private final PositiveIntegerAtleastFive boardWidth;
    private final PositiveIntegerAtleastFive boardHeight;
    private final List<SpecialLinkedPositions> teleporterList;
    // Note: this works because GridPosition implements a proper equals()
    private final HashMap<GridPosition, Integer> specialPositionsMap = new HashMap<>();

    public TeleporterContainerDisallowingWinTeleporters(PositiveIntegerAtleastFive boardWidth, PositiveIntegerAtleastFive boardHeight) {
        this.boardWidth = boardWidth;
        this.boardHeight = boardHeight;
        this.teleporterList = new ArrayList<>();

        // Pre-initialise the hashmap with -1s as that is the internal code for no special position
        for (int y = 0; y < this.boardWidth.getValue(); y++) {
            for (int x = 0; x < this.boardHeight.getValue(); x++) {
                // -1 means it isnt special
                specialPositionsMap.put(new GridPosition(x, y), -1);
            }
        }
    }

    @Override
    public SpecialPositionStatus evaluatePieceHittingSpecialPositions(Piece piece) {
        if (!this.specialPositionsMap.containsKey(piece.getCurrentPosition()))
            return SpecialPositionStatus.NOTHING_HAPPENED;

        Integer teleporterIndex = this.specialPositionsMap.get(piece.getCurrentPosition());
        if (teleporterIndex == -1)
            return SpecialPositionStatus.NOTHING_HAPPENED;

        SpecialLinkedPositions relevantTeleporter = this.teleporterList.get(teleporterIndex);

        GridPosition newPosition = relevantTeleporter.getPositionAfterSpecialBehaviour(piece.getCurrentPosition());
        piece.setPosition(newPosition);

        return SpecialPositionStatus.PIECE_TELEPORTED;
    }

    @Override
    public SpecialPositionContainer.TeleporterRegisterStatus registerNewSpecialPosition(SpecialLinkedPositions newPosition) {
        List<GridPosition> relevantPositions = newPosition.getEntryPoints();

        boolean anyPosOnCorner = relevantPositions
            .stream()
            .map(pos -> this.positionLandsOnWinSpot(pos))
            .reduce(false, (a,b) -> a || b);

        if (anyPosOnCorner)
            return TeleporterRegisterStatus.REJECTED;

        this.teleporterList.add(newPosition);

        for (GridPosition pos : relevantPositions) {
            this.specialPositionsMap.put(pos, 
                teleporterList.lastIndexOf(teleporterList.getLast()));
        }

        return TeleporterRegisterStatus.ACCEPTED;
    }

    @Override
    public List<SpecialLinkedPositions> getSpecialPositions() {
        return this.teleporterList;
    }

    // A special position cannot have a teleporer straight to a winning spot 
    private boolean positionLandsOnWinSpot(GridPosition pos) {
        GridPosition bottomLeft = new GridPosition(0, 0);
        GridPosition topLeft = new GridPosition(0, this.boardWidth.getValue() - 1);
        GridPosition bottomRight = new GridPosition(this.boardWidth.getValue() - 1, 0);
        GridPosition topRight = new GridPosition(this.boardWidth.getValue() - 1, this.boardHeight.getValue() - 1);

        return pos.equals(bottomLeft) || pos.equals(topLeft) || pos.equals(bottomRight) || pos.equals(topRight);
    }
}
