package uk.ac.mmu.game.domain.board.specialpositions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import uk.ac.mmu.game.domain.pieces.Piece;
import uk.ac.mmu.game.domain.util.GridPosition;
import uk.ac.mmu.game.domain.util.PositiveIntegerAtleastFive;

/*
    Almost identical to the other TeleporterContainer but it doesnt validate against the corners because its for reading in from files
*/
public class LenientTeleporterContainer implements SpecialPositionContainer {
    private final PositiveIntegerAtleastFive boardWidth;
    private final PositiveIntegerAtleastFive boardHeight;
    private final List<SpecialLinkedPositions> teleporterList;
    private final HashMap<GridPosition, Integer> specialPositionsMap = new HashMap<>();

    public LenientTeleporterContainer(PositiveIntegerAtleastFive boardWidth, PositiveIntegerAtleastFive boardHeight) {
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
}
