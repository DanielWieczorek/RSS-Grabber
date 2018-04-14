package de.wieczorek.rss.core;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.xnio.Options;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.wieczorek.rss.core.jgroups.ServiceMetadata;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;

@ApplicationScoped
public class Server {

    private Undertow server;

    @Inject
    @RestPort
    private Integer port;

    @Inject
    private Controller controller;

    public Server() {

    }

    public void start() {

	initServer();

	server.start();
    }

    private void initServer() {

	server = Undertow.builder().addHttpListener(port, "localhost").setHandler(new HttpHandler() {
	    @Override
	    public void handleRequest(final HttpServerExchange exchange) throws Exception {
		exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
		exchange.getResponseHeaders().put(new HttpString("Access-Control-Allow-Origin"),
			"http://localhost:4200");
		exchange.getResponseHeaders().put(new HttpString("Access-Control-Allow-Methods"), "GET");

		if (exchange.getRequestPath().equals("/status")) {
		    List<ServiceMetadata> metadata = controller.status();
		    String result = new ObjectMapper().writeValueAsString(metadata);
		    exchange.getResponseSender().send(result);
		} else {
		    exchange.setStatusCode(404);
		}

	    }
	}).setWorkerOption(Options.WORKER_IO_THREADS, 1) //
		.setWorkerOption(Options.WORKER_TASK_CORE_THREADS, 1) //
		.setWorkerOption(Options.WORKER_TASK_MAX_THREADS, 1) //
		.setServerOption(Options.WORKER_IO_THREADS, 1) //
		.build();
    }

}
