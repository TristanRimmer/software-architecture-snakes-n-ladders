package uk.ac.mmu.game.usecase;

import uk.ac.mmu.game.usecase.types.PieceConfigurationsList;

public interface PieceConfigurationFactory {
    public PieceConfigurationsList getPieceConfigList(int boardWidth, int boardHeight);
}
