package uk.ac.mmu.game.domain.util;

import java.util.List;

import uk.ac.mmu.game.domain.pieces.positiontrackers.PositionTrackingConverter;

public record PieceConfigurationsList(List<PositionTrackingConverter> converters) {};
