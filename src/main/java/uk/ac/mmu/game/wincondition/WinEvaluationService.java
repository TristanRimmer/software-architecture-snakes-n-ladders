package uk.ac.mmu.game.wincondition;

import uk.ac.mmu.game.board.BoardDimensions;
import uk.ac.mmu.game.pieces.PieceService;

public interface WinEvaluationService {
    // TODO/WARN: This may be a better function signature - would require a small modification of Piece
    // boolean hasPieceWon(int currentDisplacement, int winningDisplacement);

    // TODO: refactor to work as above: right now this requires that the pieces are correct handling part of the win condition
    
    // By doing this, the impl can auto apply the correct behavioural changes to Piece, as well as entirely removing winnignDisplacemnt from existing the Piece
    boolean hasPieceWon(PieceService piece, BoardDimensions boardProperties);
}
