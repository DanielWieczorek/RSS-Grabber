package de.wieczorek.rss.trading.business;

import de.wieczorek.core.ui.ControllerBase;
import de.wieczorek.rss.trading.business.data.SimulationDataGeneratorBuilder;
import de.wieczorek.rss.trading.common.oracle.DefaultOracle;
import de.wieczorek.rss.trading.common.oracle.OracleConfigurationDao;
import de.wieczorek.rss.trading.common.trading.TradingSimulationResult;
import de.wieczorek.rss.trading.common.trading.TradingSimulator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class Controller extends ControllerBase {
    private static final Logger logger = LoggerFactory.getLogger(Controller.class);

    @Inject
    private OracleConfigurationDao oracleConfigurationDao;

    @Inject
    private TradingSimulator simulator;

    @Inject
    private SimulationDataGeneratorBuilder dataGeneratorBuilder;

    public TradingSimulationResult simulate(String offset) {
        return simulator.simulate(dataGeneratorBuilder.produceGenerator(offset), new DefaultOracle(oracleConfigurationDao.read()));
    }
}
