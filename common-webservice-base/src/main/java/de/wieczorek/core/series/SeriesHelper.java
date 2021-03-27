package de.wieczorek.core.series;

import java.util.ArrayList;
import java.util.List;

public final class SeriesHelper {

    public static <T> List<T> thinOutSeries(List<T> input, int maxLength) {
        List<T> result = new ArrayList<>();
        int stepping = Math.max(input.size() / maxLength, 1);

        int counter = 0;
        for (T item : input) {
            if (counter == 0) {
                result.add(item);
            }
            counter = (counter + 1) % stepping;
        }
        return result;
    }
}
