package uk.ac.mmu.game.domain.game;

import java.util.List;

import uk.ac.mmu.game.domain.pieces.container.PieceContainer;

public record GameStore(
    GameConfiguration configuration,
    PieceContainer pieces,
    List<Integer> diceList,
    String tag
) {}
