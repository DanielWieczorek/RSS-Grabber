package de.wieczorek.chart.core.business;

import de.wieczorek.chart.core.business.kafka.ChartEntryTopicConfiguration;
import de.wieczorek.chart.core.persistence.ChartEntryDao;
import de.wieczorek.core.kafka.KafkaSender;
import de.wieczorek.core.kafka.WithTopicConfiguration;
import de.wieczorek.core.persistence.EntityManagerContext;
import de.wieczorek.core.timer.RecurrentTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.ResponseProcessingException;
import javax.ws.rs.core.MediaType;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RecurrentTask(interval = 1, unit = TimeUnit.MINUTES)
@EntityManagerContext
@ApplicationScoped
public class ChartReader implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(ChartReader.class);

    @Inject
    private ChartEntryDao dao;


    @Inject
    @WithTopicConfiguration(configName = ChartEntryTopicConfiguration.class)
    private KafkaSender<Object> sender;

    public ChartReader() {

    }

    @Override
    public void run() {
        ChartEntry lastEntry = null;
        try {
            logger.debug("triggered reading chart entries at " + LocalDateTime.now());
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
                    entry.setOpen(Double.parseDouble((String) item.get(1)));
                    entry.setHigh(Double.parseDouble((String) item.get(2)));
                    entry.setLow(Double.parseDouble((String) item.get(3)));
                    entry.setClose(Double.parseDouble((String) item.get(4)));
                    entry.setVolumeWeightedAverage(Double.parseDouble((String) item.get(5)));
                    entry.setVolume(Double.parseDouble((String) item.get(6)));
                    entry.setTransactions((Integer) item.get(7));

                    entries.add(entry);

                });

            }

            entries.retainAll(entries.stream().filter((item) -> dao.findById(item.getDate()) == null)
                    .collect(Collectors.toList()));
            Map<LocalDateTime, ChartEntry> map = new HashMap<>();

            entries.forEach(item -> map.put(item.getDate(), item));

            dao.persistAll(map.values());
            lastEntry = entries.isEmpty() ? null : entries.get(entries.size() - 1);
            sender.send(lastEntry.getDate().toString(), lastEntry);
        } catch (ResponseProcessingException e) {
            logger.error("error while retrieving chart data: ", e.getResponse().readEntity(String.class));
        } catch (Exception e) {
            logger.error("error while retrieving chart data: ", e);
        }
        if (lastEntry != null) {
            sender.send(lastEntry.getDate().toString(), lastEntry);
        }

    }
}
