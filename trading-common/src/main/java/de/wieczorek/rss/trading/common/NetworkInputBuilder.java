package de.wieczorek.rss.trading.common;

import java.util.Arrays;
import java.util.List;

import de.wieczorek.chart.core.persistence.ChartMetricRecord;
import de.wieczorek.rss.trading.types.Account;
import de.wieczorek.rss.trading.types.IStateHistoryHolder;
import de.wieczorek.rss.trading.types.StateEdgePart;

public final class NetworkInputBuilder {
    private NetworkInputBuilder() {

    }

    public static double[] buildInputArray(IStateHistoryHolder state, Account account) {
	List<StateEdgePart> parts = state.getAllStateParts().subList(state.getPartsStartIndex(),
		state.getPartsEndIndex());

	double[] result = new double[22 * parts.size() + 2];

	// Check if at least 2 parts
	for (int i = 0; i < parts.size(); i++) {
	    StateEdgePart currentPart = parts.get(i);
	    result[22 * i + 0] = currentPart.getSentiment() != null ? currentPart.getSentiment().getPredictedDelta()
		    : 0.0;
	    result[22 * i + 1] = currentPart.getChartEntry().getClose();

	    List<ChartMetricRecord> record = currentPart.getMetricsRecord();

	    if (record.size() != 4) {
		record = Arrays.asList(new ChartMetricRecord(), new ChartMetricRecord(), new ChartMetricRecord(),
			new ChartMetricRecord());

	    }
	    for (int j = 0; j < record.size(); j++) {
		result[22 * i + j * 5 + 2] = Double.isNaN(record.get(j).getValue1min()) ? 0.0
			: record.get(j).getValue1min();
		result[22 * i + j * 5 + 3] = Double.isNaN(record.get(j).getValue5min()) ? 0.0
			: record.get(j).getValue5min();
		result[22 * i + j * 5 + 4] = Double.isNaN(record.get(j).getValue15min()) ? 0.0
			: record.get(j).getValue15min();
		result[22 * i + j * 5 + 5] = Double.isNaN(record.get(j).getValue30min()) ? 0.0
			: record.get(j).getValue30min();
		result[22 * i + j * 5 + 6] = Double.isNaN(record.get(j).getValue60min()) ? 0.0
			: record.get(j).getValue60min();
	    }

	}

	result[22 * parts.size() + 0] = account.getBtc() > 0.0 ? 1.0 : 0.0;
	result[22 * parts.size() + 1] = account.getEur() > 0.0 ? 1.0 : 0.0;

	return result;
    }
}
