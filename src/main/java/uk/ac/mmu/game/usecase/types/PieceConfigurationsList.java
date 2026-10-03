package uk.ac.mmu.game.usecase.types;

import java.util.List;

import uk.ac.mmu.game.domain.pieces.positiontrackers.PositionTrackingConverter;

public record PieceConfigurationsList(List<PositionTrackingConverter> converters) {};
