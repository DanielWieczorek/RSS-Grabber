package de.wieczorek.rss.core.ui;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.xnio.Options;

import de.wieczorek.rss.core.config.port.RestPort;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;

@ApplicationScoped
public class RssReaderServer {

    private Undertow server;

    @Inject
    @RestPort
    private Integer port;

    @Inject
    private HttpHandler handler;

    public RssReaderServer() {

    }

    public void start() {

	initServer();

	server.start();
    }

    private void initServer() {

	server = Undertow.builder().addHttpListener(port, "localhost").setHandler(handler)
		.setWorkerOption(Options.WORKER_IO_THREADS, 1) //
		.setWorkerOption(Options.WORKER_TASK_CORE_THREADS, 1) //
		.setWorkerOption(Options.WORKER_TASK_MAX_THREADS, 1) //
		.setServerOption(Options.WORKER_IO_THREADS, 1) //
		.build();
    }

}
