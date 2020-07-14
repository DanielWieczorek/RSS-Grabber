package de.wieczorek.chart.advisor.types;

import java.util.Map;

public class Normalizer {

    private static Map<String, Boundaries> boundaryMap = Map.of(
            "aroon", new Boundaries(93, -93),
            "macd", new Boundaries(-258, 258),
            "rsi", new Boundaries(0, 100),
            "stochasticD", new Boundaries(0, 100));

    private static Boundaries outputBoundaries = new Boundaries(-118, 118);

    public static Boundaries getInputBoundaries(String indicator) {
        return boundaryMap.get(indicator);
    }


    public static Boundaries getOutputBoundaries() {
        return outputBoundaries;
    }

    public static double normalize(double in, Boundaries b) {
        return Double.isNaN(in) ? 0.0 : ((in - b.min) / (b.max - b.min)) * 2 - 1;
    }

    public static double denormalize(double in, Boundaries b) {
        return Double.isNaN(in) ? 0.0 : ((in + 1) * (b.max - b.min) + 2 * b.min) / 2;
    }

    public static class Boundaries {
        double min;
        double max;

        Boundaries(double min, double max) {
            this.min = min;
            this.max = max;
        }
    }
}
