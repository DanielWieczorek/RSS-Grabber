package de.wieczorek.rss.trading.common.oracle;

import de.wieczorek.rss.trading.common.oracle.average.AverageType;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class AveragesCache {

    public static final AveragesCache INSTANCE = new AveragesCache();
    private Map<AverageKey, Double> cache = new ConcurrentHashMap<>();

    private AveragesCache() {

    }

    public Optional<Double> get(LocalDateTime startDate, LocalDateTime endDate, AverageType type) {
        AverageKey key = new AverageKey();
        key.endDate = endDate;
        key.startDate = startDate;
        key.type = type;

        return Optional.ofNullable(cache.get(key));
    }

    public void put(LocalDateTime startDate, LocalDateTime endDate, AverageType type, Double value) {
        AverageKey key = new AverageKey();
        key.endDate = endDate;
        key.startDate = startDate;
        key.type = type;

        cache.put(key, value);
    }

    public void invalidate() {
        cache.clear();
    }

    private static class AverageKey {
        private LocalDateTime endDate;
        private LocalDateTime startDate;
        private AverageType type;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            AverageKey that = (AverageKey) o;
            return Objects.equals(endDate, that.endDate) &&
                    Objects.equals(startDate, that.startDate) &&
                    type == that.type;
        }

        @Override
        public int hashCode() {
            return Objects.hash(endDate, startDate, type);
        }
    }

}
