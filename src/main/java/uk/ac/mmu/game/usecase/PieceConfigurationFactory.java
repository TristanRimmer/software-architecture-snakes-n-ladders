package uk.ac.mmu.game.usecase;

import uk.ac.mmu.game.domain.pieces.container.PieceContainer;

public interface PieceConfigurationFactory {
    public PieceContainer getPieceContainer(int boardWidth, int boardHeight);
}
