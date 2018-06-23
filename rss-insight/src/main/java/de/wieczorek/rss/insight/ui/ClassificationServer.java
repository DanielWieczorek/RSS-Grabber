package de.wieczorek.rss.insight.ui;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.xnio.Options;

import de.wieczorek.rss.insight.config.RestPort;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.BlockingHandler;
import io.undertow.util.HttpString;

@ApplicationScoped
public class ClassificationServer {

    private Undertow server;

    @Inject
    @RestPort
    private Integer port;

    @Inject
    private Controller controller;

    public ClassificationServer() {

    }

    public void start() {

	initServer();

	server.start();
    }

    private void initServer() {

	server = Undertow.builder().addHttpListener(port, "localhost")
		.setHandler(new BlockingHandler(new HttpHandler() {
		    @Override
		    public void handleRequest(final HttpServerExchange exchange) throws Exception {

			exchange.getResponseHeaders().put(new HttpString("Access-Control-Allow-Origin"),
				"http://localhost:4200");
			exchange.getResponseHeaders().put(new HttpString("Access-Control-Allow-Methods"), "GET,POST");
			exchange.getResponseHeaders().put(new HttpString("Access-Control-Allow-Headers"),
				"Content-Type");

			if (exchange.getRequestPath().equals("/train")) {
			    controller.trainNeuralNetwork();
			}
		    }
		})).setWorkerOption(Options.WORKER_IO_THREADS, 1) //
		.setWorkerOption(Options.WORKER_TASK_CORE_THREADS, 1) //
		.setWorkerOption(Options.WORKER_TASK_MAX_THREADS, 1) //
		.setServerOption(Options.WORKER_IO_THREADS, 1) //
		.build();
    }

}
