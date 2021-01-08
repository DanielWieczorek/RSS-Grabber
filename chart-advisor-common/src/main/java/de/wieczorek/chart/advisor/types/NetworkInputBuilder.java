package de.wieczorek.chart.advisor.types;

import de.wieczorek.chart.core.persistence.ChartMetricRecord;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class NetworkInputBuilder {
    public static final int STEPPING_MINUTES = 15;

    public static final int MAX_LENGTH = DataPreparator.HOURS_OF_DATA * (60 / STEPPING_MINUTES);
    public static final int VECTOR_SIZE = 4 * 9;

    public static double[][] getVectors(NetInputItem item) {
        double[][] itemVectors = new double[VECTOR_SIZE][MAX_LENGTH];
        int index = 0;

        List<LocalDateTime> dates = item.getDates().subList(item.getStartIndex(), item.getEndIndex());
        for (int k = 0; k < dates.size(); k += 15) {

            List<List<ChartMetricRecord>> records = new ArrayList<>();
            for (int m = k; m < Math.min(k + 15, dates.size()); m++) {
                records.add(item.getInputChartMetrics().get(dates.get(k)));
            }
            List<ChartMetricRecord> record = generateAveragedRecord(records);

            for (int j = 0; j < record.size(); j++) {
                Normalizer.Boundaries b = new Normalizer.Boundaries(-1, 1);
                if (record.get(j).getId() != null) {
                    b = Normalizer.getInputBoundaries(record.get(j).getId().getIndicator());
                }

                itemVectors[j * 9 + 0][index] = Normalizer.normalize(record.get(j).getValue1min(), b);
                itemVectors[j * 9 + 1][index] = Normalizer.normalize(record.get(j).getValue5min(), b);
                itemVectors[j * 9 + 2][index] = Normalizer.normalize(record.get(j).getValue15min(), b);
                itemVectors[j * 9 + 3][index] = Normalizer.normalize(record.get(j).getValue30min(), b);
                itemVectors[j * 9 + 4][index] = Normalizer.normalize(record.get(j).getValue60min(), b);
                itemVectors[j * 9 + 5][index] = Normalizer.normalize(record.get(j).getValue2hour(), b);
                itemVectors[j * 9 + 6][index] = Normalizer.normalize(record.get(j).getValue6hour(), b);
                itemVectors[j * 9 + 7][index] = Normalizer.normalize(record.get(j).getValue12hour(), b);
                itemVectors[j * 9 + 8][index] = Normalizer.normalize(record.get(j).getValue24hour(), b);
            }
            index++;

        }
        return itemVectors;
    }


    private static List<ChartMetricRecord> generateAveragedRecord(List<List<ChartMetricRecord>> records) {
        List<List<ChartMetricRecord>> foo = records.stream()
                .filter(Objects::nonNull)
                .filter(record -> record.size() == 4)
                .peek(record -> record.sort(Comparator.comparing(x -> x.getId().getIndicator())))
                .collect(Collectors.toList());


        List<ChartMetricRecord> result = Arrays.asList(new ChartMetricRecord(), new ChartMetricRecord(), new ChartMetricRecord(),
                new ChartMetricRecord());

        for (List<ChartMetricRecord> record : foo) {
            for (int i = 0; i < result.size(); i++) {
                result.get(i).setId(record.get(i).getId());
                result.get(i).setValue1min(result.get(i).getValue1min() + record.get(i).getValue1min());
                result.get(i).setValue5min(result.get(i).getValue5min() + record.get(i).getValue5min());
                result.get(i).setValue15min(result.get(i).getValue15min() + record.get(i).getValue15min());
                result.get(i).setValue30min(result.get(i).getValue30min() + record.get(i).getValue30min());
                result.get(i).setValue60min(result.get(i).getValue60min() + record.get(i).getValue60min());
                result.get(i).setValue2hour(result.get(i).getValue2hour() + record.get(i).getValue2hour());
                result.get(i).setValue6hour(result.get(i).getValue6hour() + record.get(i).getValue6hour());
                result.get(i).setValue12hour(result.get(i).getValue12hour() + record.get(i).getValue12hour());
                result.get(i).setValue24hour(result.get(i).getValue24hour() + record.get(i).getValue24hour());
            }
        }

        for (ChartMetricRecord chartMetricRecord : result) {
            chartMetricRecord.setValue1min(chartMetricRecord.getValue1min() / foo.size());
            chartMetricRecord.setValue5min(chartMetricRecord.getValue5min() / foo.size());
            chartMetricRecord.setValue15min(chartMetricRecord.getValue15min() / foo.size());
            chartMetricRecord.setValue30min(chartMetricRecord.getValue30min() / foo.size());
            chartMetricRecord.setValue60min(chartMetricRecord.getValue60min() / foo.size());
            chartMetricRecord.setValue2hour(chartMetricRecord.getValue2hour() / foo.size());
            chartMetricRecord.setValue6hour(chartMetricRecord.getValue6hour() / foo.size());
            chartMetricRecord.setValue12hour(chartMetricRecord.getValue12hour() / foo.size());
            chartMetricRecord.setValue24hour(chartMetricRecord.getValue24hour() / foo.size());
        }

        return result;
    }
}
