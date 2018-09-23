package de.wieczorek.chart.core.business;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.wieczorek.chart.core.persistence.RssEntryDao;
import de.wieczorek.rss.core.timer.RecurrentTask;

@RecurrentTask(interval = 1, unit = TimeUnit.MINUTES)
@ApplicationScoped
public class ChartReader implements Runnable {
    private static final Logger logger = LogManager.getLogger(ChartReader.class.getName());

    @Inject
    private RssEntryDao dao;

    public ChartReader() {

    }

    @Override
    public void run() {
	try {
	    OhlcApiRequestResult result = ClientBuilder.newClient()
		    .target("https://api.kraken.com/0/public/OHLC?pair=XBTEUR").request(MediaType.APPLICATION_JSON)
		    .get(OhlcApiRequestResult.class);

	    List<ChartEntry> entries = new ArrayList<>();

	    if (result.getErrors().isEmpty()) {
		((List<List<?>>) result.getResults().get("XXBTZEUR")).forEach((List<?> item) -> {
		    ChartEntry entry = new ChartEntry();
		    entry.setDate(LocalDateTime
			    .ofInstant(Instant.ofEpochSecond((Integer) item.get(0)), ZoneId.systemDefault())
			    .withSecond(0).withNano(0));
		    entry.setOpen(Double.valueOf((String) item.get(1)));
		    entry.setHigh(Double.valueOf((String) item.get(2)));
		    entry.setLow(Double.valueOf((String) item.get(3)));
		    entry.setClose(Double.valueOf((String) item.get(4)));
		    entry.setVolumeWeightedAverage(Double.valueOf((String) item.get(5)));
		    entry.setVolume(Double.valueOf((String) item.get(6)));
		    entry.setTransactions((Integer) item.get(7));

		    entries.add(entry);
		});

	    }

	    entries.retainAll(entries.stream().filter((item) -> dao.findById(item.getDate()) == null)
		    .collect(Collectors.toList()));

	    dao.persistAll(entries);

	} catch (Exception e) {
	    logger.error("error while retrieving chart data: ", e);
	}
    }
}
