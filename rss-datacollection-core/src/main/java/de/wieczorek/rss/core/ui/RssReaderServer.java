package de.wieczorek.rss.core.ui;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.xnio.Options;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.wieczorek.rss.core.config.port.RestPort;
import de.wieczorek.rss.core.jgroups.CollectorStatus;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

@ApplicationScoped
public class RssReaderServer {

    private Undertow server;

    @Inject
    @RestPort
    private Integer port;

    @Inject
    private Controller controller;

    public RssReaderServer() {

    }

    public void start() {

	initServer();

	server.start();
    }

    private void initServer() {

	server = Undertow.builder().addHttpListener(port, "localhost").setHandler(new HttpHandler() {
	    @Override
	    public void handleRequest(final HttpServerExchange exchange) throws Exception {
		exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
		if (exchange.getRequestPath().equals("/start")) {
		    controller.start();
		    exchange.getResponseSender().send("start");
		} else if (exchange.getRequestPath().equals("/stop")) {
		    controller.stop();
		    exchange.getResponseSender().send("stop");
		} else if (exchange.getRequestPath().equals("/status")) {
		    exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, null);
		    exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
		    CollectorStatus status = new CollectorStatus();
		    status.setStatus("running");
		    exchange.getResponseSender().send(new ObjectMapper().writeValueAsString(status));

		} else {
		    exchange.setStatusCode(404);
		}

	    }
	}).setWorkerOption(Options.WORKER_IO_THREADS, 1) //
		.setWorkerOption(Options.WORKER_TASK_CORE_THREADS, 1) //
		.setWorkerOption(Options.WORKER_TASK_MAX_THREADS, 1) //
		.setServerOption(Options.WORKER_IO_THREADS, 1) //
		.build();
	controller.start();
    }

}
