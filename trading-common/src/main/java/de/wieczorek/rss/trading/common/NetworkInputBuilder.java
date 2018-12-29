package de.wieczorek.rss.trading.common;

import java.util.List;

import de.wieczorek.rss.trading.types.Account;
import de.wieczorek.rss.trading.types.IStateHistoryHolder;
import de.wieczorek.rss.trading.types.StateEdgePart;

public final class NetworkInputBuilder {
    private NetworkInputBuilder() {

    }

    public static double[] buildInputArray(IStateHistoryHolder state, Account account) {
	List<StateEdgePart> parts = state.getAllStateParts().subList(state.getPartsStartIndex(),
		state.getPartsEndIndex());

	double[] result = new double[2 * parts.size() + 2];

	// Check if at least 2 parts
	for (int i = 0; i < parts.size(); i++) {
	    StateEdgePart currentPart = parts.get(i);
	    result[2 * i + 0] = currentPart.getSentiment() != null ? currentPart.getSentiment().getPredictedDelta()
		    : 0.0;
	    result[2 * i + 1] = currentPart.getChartEntry().getClose();
	}

	result[2 * parts.size() + 0] = account.getBtc() > 0.0 ? 1.0 : 0.0;
	result[2 * parts.size() + 1] = account.getEur() > 0.0 ? 1.0 : 0.0;

	return result;
    }
}
