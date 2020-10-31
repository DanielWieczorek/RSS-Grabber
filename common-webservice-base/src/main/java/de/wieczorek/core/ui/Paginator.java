package de.wieczorek.core.ui;

import java.time.LocalDateTime;
import java.time.temporal.TemporalAmount;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

public class Paginator<T> {

    private TemporalAmount stepping;

    private BiFunction<LocalDateTime, LocalDateTime, List<T>> chunkRetriever;

    public Paginator(TemporalAmount stepping, BiFunction<LocalDateTime, LocalDateTime, List<T>> chunkRetriever) {
        this.stepping = stepping;
        this.chunkRetriever = chunkRetriever;
    }

    public List<T> getAll() {
        LocalDateTime endDate = LocalDateTime.now().withSecond(0).withNano(0).minusMinutes(1);
        LocalDateTime startDate = endDate.minus(stepping);
        List<T> totalResult = new ArrayList<>();
        while (startDate.isAfter(LocalDateTime.MIN)) {
            var intermediateResult = chunkRetriever.apply(startDate, endDate);

            if (intermediateResult.isEmpty()) {
                break;
            }

            totalResult.addAll(intermediateResult);

            startDate = startDate.minus(stepping);
            endDate = endDate.minus(stepping);
        }
        return totalResult;
    }

}
