package de.wieczorek.rss.trading.ui;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.wieczorek.rss.core.timer.RecurrentTaskManager;
import de.wieczorek.rss.core.ui.ControllerBase;

@ApplicationScoped
public class Controller extends ControllerBase {
    private static final Logger logger = LogManager.getLogger(Controller.class.getName());

    @Inject
    private RecurrentTaskManager timer;

    public void train() {

    }

    @Override
    public void start() {
	timer.start();
    }

}
