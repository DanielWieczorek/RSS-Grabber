package de.wieczorek.rss.core;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.wieczorek.rss.core.jgroups.ServiceMetadata;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;

@ApplicationScoped
public class RssHandlerProducer {

    @Inject
    private Controller controller;

    @Produces
    private HttpHandler handler = new HttpHandler() {
	@Override
	public void handleRequest(final HttpServerExchange exchange) throws Exception {
	    exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
	    exchange.getResponseHeaders().put(new HttpString("Access-Control-Allow-Origin"), "http://localhost:4200");
	    exchange.getResponseHeaders().put(new HttpString("Access-Control-Allow-Methods"), "GET");

	    if (exchange.getRequestPath().equals("/status")) {
		requestStatusAndPutItIntoRequest(exchange);
	    } else if (exchange.getRequestPath().startsWith("/start/")) {
		String collectorName = exchange.getRequestPath()
			.substring(exchange.getRequestPath().lastIndexOf('/') + 1);
		controller.startService(collectorName);
		requestStatusAndPutItIntoRequest(exchange);
	    } else if (exchange.getRequestPath().startsWith("/stop/")) {
		String collectorName = exchange.getRequestPath()
			.substring(exchange.getRequestPath().lastIndexOf('/') + 1);
		controller.stopService(collectorName);
		requestStatusAndPutItIntoRequest(exchange);
	    } else {
		exchange.setStatusCode(404);
	    }

	}

	private void requestStatusAndPutItIntoRequest(final HttpServerExchange exchange)
		throws JsonProcessingException {
	    List<ServiceMetadata> metadata = controller.status();
	    String result = new ObjectMapper().writeValueAsString(metadata);
	    exchange.getResponseSender().send(result);
	}
    };

}
