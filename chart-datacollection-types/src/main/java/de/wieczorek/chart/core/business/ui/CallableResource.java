package de.wieczorek.chart.core.business.ui;

import de.wieczorek.chart.core.business.ChartEntry;

import java.util.List;

public interface CallableResource {

    List<ChartEntry> ohlcv();


    List<ChartEntry> ohlcv24h();
}
