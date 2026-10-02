package uk.ac.mmu.game.infrastructure.factories;

import uk.ac.mmu.game.domain.pieces.GamePiece;
import uk.ac.mmu.game.domain.pieces.container.LockingPieceContainer;
import uk.ac.mmu.game.domain.pieces.container.PieceContainer;
import uk.ac.mmu.game.domain.pieces.positiontrackers.LowerLeftOrigin;
import uk.ac.mmu.game.domain.pieces.positiontrackers.LowerRightOrigin;
import uk.ac.mmu.game.domain.pieces.positiontrackers.UpperLeftOrigin;
import uk.ac.mmu.game.domain.pieces.positiontrackers.UpperRightOrigin;
import uk.ac.mmu.game.usecase.PieceConfigurationFactory;

public class HardCodedPieceConfigurationFactory implements PieceConfigurationFactory {
    @Override
    // Example to demonstrate that the code is not coupled directly to spring
    public PieceContainer getPieceContainer(int boardWidth, int boardHeight) {
		PieceContainer pieces = new LockingPieceContainer();

		pieces.registerNewPiece(
            new GamePiece(new LowerLeftOrigin(), boardWidth, boardHeight));
		pieces.registerNewPiece(
            new GamePiece(new UpperRightOrigin(), boardWidth, boardHeight));
		pieces.registerNewPiece(
            new GamePiece(new LowerRightOrigin(), boardWidth, boardHeight));
		pieces.registerNewPiece(
            new GamePiece(new UpperLeftOrigin(), boardWidth, boardHeight));

        return pieces;
    }

}
