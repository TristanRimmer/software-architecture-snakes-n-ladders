package uk.ac.mmu.game.infrastructure.serialisation;

import uk.ac.mmu.game.domain.board.specialpositions.OneWayTeleporter;
import uk.ac.mmu.game.domain.board.specialpositions.SpecialLinkedPositions;
import uk.ac.mmu.game.domain.board.specialpositions.TwoWayTeleporter;
import uk.ac.mmu.game.domain.util.GridPosition;
import uk.ac.mmu.game.domain.util.ImplFactoryException;

public class SpecialPositionSerialiser {
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
}
