package rndbet.rndbetpredictionsbackend.matchday.domain;

import java.util.List;

public record CurrentMatchday(Integer jornada, boolean temporadaCompletada, List<MatchdayFixture> partidos) {}
