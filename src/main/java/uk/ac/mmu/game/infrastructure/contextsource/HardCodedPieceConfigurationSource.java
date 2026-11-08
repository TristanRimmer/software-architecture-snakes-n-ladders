package uk.ac.mmu.game.infrastructure.contextsource;

import java.util.List;

import uk.ac.mmu.game.domain.pieces.positiontrackers.LowerLeftOrigin;
import uk.ac.mmu.game.domain.pieces.positiontrackers.LowerRightOrigin;
import uk.ac.mmu.game.domain.pieces.positiontrackers.UpperLeftOrigin;
import uk.ac.mmu.game.domain.pieces.positiontrackers.UpperRightOrigin;
import uk.ac.mmu.game.domain.util.PieceConfigurationsList;

public class HardCodedPieceConfigurationSource {
    public static PieceConfigurationsList getPieceConfigList() {
      return new PieceConfigurationsList(
            List.of(
                  new LowerLeftOrigin(), 
                  new UpperRightOrigin(), 
                  new LowerRightOrigin(), 
                  new UpperLeftOrigin())); 
    }

}
