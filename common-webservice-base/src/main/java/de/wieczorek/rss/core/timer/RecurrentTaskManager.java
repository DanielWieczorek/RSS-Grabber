package de.wieczorek.rss.core.timer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.weld.inject.WeldInstance;

@ApplicationScoped
public class RecurrentTaskManager {

    @Inject
    @RecurrentTask
    private WeldInstance<Runnable> tasks;

    private List<RecurrentTaskRunner> runners = new ArrayList<>();

    @PostConstruct
    private void init() {
	tasks.handlers().forEach((handler) -> {
	    RecurrentTask[] taskAnnotations = handler.getBean().getBeanClass()
		    .getAnnotationsByType(RecurrentTask.class);
	    Arrays.asList(taskAnnotations).forEach((annotation) -> runners
		    .add(new RecurrentTaskRunner(handler.get(), annotation.interval(), annotation.unit())));
	});
    }

    public void start() {
	System.out.println("triggered start");
	runners.forEach(RecurrentTaskRunner::start);

    }

    public void stop() {
	runners.forEach(RecurrentTaskRunner::stop);

    }

}
