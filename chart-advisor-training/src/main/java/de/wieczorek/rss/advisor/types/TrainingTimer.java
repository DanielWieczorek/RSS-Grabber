package de.wieczorek.rss.advisor.types;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;

import de.wieczorek.chart.core.persistence.ChartMetricRecord;
import org.nd4j.linalg.factory.Nd4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.wieczorek.chart.core.business.ChartEntry;
import de.wieczorek.rss.core.jackson.ObjectMapperContextResolver;
import de.wieczorek.rss.core.timer.RecurrentTask;

@RecurrentTask(interval = 0, unit = TimeUnit.MINUTES)
@ApplicationScoped
public class TrainingTimer implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(TrainingTimer.class);

    @Inject
    private TradingNeuralNetworkTrainer network;

    public TrainingTimer() {

    }

    @Override
    public void run() {
	try {

		List<ChartMetricRecord> metrics = ClientBuilder.newClient().register(new ObjectMapperContextResolver())
				.target("http://wieczorek.io:13000/metric/all").request(MediaType.APPLICATION_JSON)
				.get(new GenericType<List<ChartMetricRecord>>() {
				});

		List<ChartEntry> chartEntries = ClientBuilder.newClient().register(new ObjectMapperContextResolver())
				.target("http://wieczorek.io:12000/ohlcv").request(MediaType.APPLICATION_JSON)
				.get(new GenericType<List<ChartEntry>>() {
				});

		if (metrics != null && chartEntries != null) {

			List<NetInputItem> data = new DataPreparator().withChartData(chartEntries).withMetrics(metrics).getData();
			List<NetInputItem> positive = data.stream().filter(item -> item.getOutputDelta() > 0).collect(Collectors.toList());
			List<NetInputItem> negative = data.stream().filter(item -> item.getOutputDelta() < 0).collect(Collectors.toList());

			int sizePerList = Math.min(positive.size(), negative.size());

			Collections.shuffle(positive);
			Collections.shuffle(negative);
			List<NetInputItem> trainingData = new ArrayList<>(positive.stream().limit(sizePerList).collect(Collectors.toList()));
			trainingData.addAll(negative.stream().limit(sizePerList).collect(Collectors.toList()));
			Collections.shuffle(trainingData);

			List<TrainingNetInputItem> netInput = new ArrayList<>();


			for (int i = 0; i < trainingData.size(); i++) {
				NetInputItem item = trainingData.get(i);

				int maxLength = 24 * 4 + 1;
				int vectorSize = 20;
				double[][] itemVectors = new double[vectorSize][maxLength];
				int index = 0;


				Map<ChartEntry, List<ChartMetricRecord>> allRecords = item.getInputChartMetrics();
				List<ChartEntry> ce = allRecords.keySet().stream().collect(Collectors.toList());

				ce.sort(Comparator.comparing(entry -> entry.getDate()));
				for (int k = 0; k < ce.size(); k += 15) {

					List<ChartMetricRecord> record = allRecords.get(ce.get(k));
					if (record.size() != 4) {
						record = Arrays.asList(new ChartMetricRecord(), new ChartMetricRecord(), new ChartMetricRecord(),
								new ChartMetricRecord());

					}
					for (int j = 0; j < record.size(); j++) {
						itemVectors[j * 5 + 0][index] = Double.isNaN(record.get(j).getValue1min()) ? 0.0
								: record.get(j).getValue1min();
						itemVectors[j * 5 + 1][index] = Double.isNaN(record.get(j).getValue5min()) ? 0.0
								: record.get(j).getValue5min();
						itemVectors[j * 5 + 2][index] = Double.isNaN(record.get(j).getValue15min()) ? 0.0
								: record.get(j).getValue15min();
						itemVectors[j * 5 + 3][index] = Double.isNaN(record.get(j).getValue30min()) ? 0.0
								: record.get(j).getValue30min();
						itemVectors[j * 5 + 4][index] = Double.isNaN(record.get(j).getValue60min()) ? 0.0
								: record.get(j).getValue60min();
					}
					index++;

				}
				netInput.add(new TrainingNetInputItem(Nd4j.create(itemVectors), item.getOutputDelta()));
				System.out.println("preparing data "+ i);

			}


			for (int i = 0; i < 10000; i++) {
				network.train(netInput, 5);
			}
		}

	} catch (Exception e) {
	    logger.error("error while training network: ", e);
	}
    }
}
