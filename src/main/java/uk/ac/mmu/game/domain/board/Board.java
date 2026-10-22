package uk.ac.mmu.game.domain.board;

import uk.ac.mmu.game.domain.util.PositiveIntegerAtleastFive;

public interface Board {
    PositiveIntegerAtleastFive getBoardWidth();
    PositiveIntegerAtleastFive getBoardHeight();
    int getMinimumTravelDistance();
}
