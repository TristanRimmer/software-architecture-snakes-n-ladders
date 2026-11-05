package uk.ac.mmu.game.domain.board;

import uk.ac.mmu.game.domain.util.BoardDimension;

public interface Board {
    BoardDimension getBoardWidth();
    BoardDimension getBoardHeight();
    int getMinimumTravelDistance();
}
