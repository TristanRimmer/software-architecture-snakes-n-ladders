package uk.ac.mmu.game.infrastructure.factories;

import java.util.List;

import uk.ac.mmu.game.domain.pieces.positiontrackers.LowerLeftOrigin;
import uk.ac.mmu.game.domain.pieces.positiontrackers.LowerRightOrigin;
import uk.ac.mmu.game.domain.pieces.positiontrackers.UpperLeftOrigin;
import uk.ac.mmu.game.domain.pieces.positiontrackers.UpperRightOrigin;
import uk.ac.mmu.game.usecase.PieceConfigurationFactory;
import uk.ac.mmu.game.usecase.types.PieceConfigurationsList;

public class HardCodedPieceConfigurationFactory implements PieceConfigurationFactory {

    @Override
    public PieceConfigurationsList getPieceConfigList(int boardWidth, int boardHeight) {
      return new PieceConfigurationsList(
            List.of(
                  new LowerLeftOrigin(), 
                  new UpperRightOrigin(), 
                  new LowerRightOrigin(), 
                  new UpperLeftOrigin())); 
    }

}
