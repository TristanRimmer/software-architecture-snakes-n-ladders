package uk.ac.mmu.game.infrastructure.implmappers;

import uk.ac.mmu.game.domain.board.specialpositions.OneWayTeleporter;
import uk.ac.mmu.game.domain.board.specialpositions.SpecialLinkedPositions;
import uk.ac.mmu.game.domain.board.specialpositions.TwoWayTeleporter;
import uk.ac.mmu.game.domain.util.GridPosition;

public class SpecialPositionMapper {
    public static final String TWO_WAY_TELEPORER = "TwoWayTeleporter";
    public static final String ONE_WAY_TELEPORTER = "OneWayTeleporter";

    public static SpecialLinkedPositions getImplementationFromString(String name, GridPosition posOne,
            GridPosition posTwo) throws ImplFactoryException {
        return switch (name) {
            case TWO_WAY_TELEPORER -> new TwoWayTeleporter(posOne, posTwo);
            case ONE_WAY_TELEPORTER -> new OneWayTeleporter(posOne, posTwo);
            default -> throw new ImplFactoryException("Invalid option provided to Special Position factory");
        };
    }
    public static String getStringFromImplemention(SpecialLinkedPositions impl) {
        return switch(impl) {
            case OneWayTeleporter o -> ONE_WAY_TELEPORTER;
            case TwoWayTeleporter t -> TWO_WAY_TELEPORER;
        };
    }
}
