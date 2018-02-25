package de.wieczorek.rss.core;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

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
		} else {
		    exchange.setStatusCode(404);
		}

	    }
	}).build();

    }

}
