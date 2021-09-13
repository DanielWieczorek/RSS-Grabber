package de.wieczorek.rss.trading.common.oracle;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class AveragesCache {

    public static final AveragesCache INSTANCE = new AveragesCache();
    private Map<AverageKey, Double> cache = new ConcurrentHashMap<>(Integer.MAX_VALUE, 0.1f, 16);

    private AveragesCache() {

    }

    public Optional<Double> get(int startOffset, int endOffset, int valuesSourceIndex, int typeIndex) {
        AverageKey key = new AverageKey();
        key.startOffset = startOffset;
        key.endOffset = endOffset;
        key.typeIndex = typeIndex;
        key.valuesSourceIndex = valuesSourceIndex;

        return Optional.ofNullable(cache.get(key));
    }

    public void put(int startOffset, int endOffset, int valuesSourceIndex, int typeIndex, Double value) {
        AverageKey key = new AverageKey();
        key.startOffset = startOffset;
        key.endOffset = endOffset;
        key.typeIndex = typeIndex;
        key.valuesSourceIndex = valuesSourceIndex;

        cache.put(key, value);
    }

    public void invalidate() {
        cache.clear();
    }

    private static class AverageKey {
        private int startOffset;
        private int endOffset;
        private int typeIndex;
        private int valuesSourceIndex;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            AverageKey that = (AverageKey) o;

            if (startOffset != that.startOffset) return false;
            if (endOffset != that.endOffset) return false;
            if (typeIndex != that.typeIndex) return false;
            return valuesSourceIndex == that.valuesSourceIndex;
        }

        @Override
        public int hashCode() {
            int result = startOffset;
            result = 31 * result + endOffset;
            result = 31 * result + typeIndex;
            result = 31 * result + valuesSourceIndex;
            return result;
        }
    }

}
