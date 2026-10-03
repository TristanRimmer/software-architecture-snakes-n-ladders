package uk.ac.mmu.game.usecase;

public interface PieceConfigurationFactory {
    public PieceConfigurationsList getPieceConfigList(int boardWidth, int boardHeight);
}
