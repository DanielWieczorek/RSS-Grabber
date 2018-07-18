package de.wieczorek.rss.core;

import java.util.Date;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.wieczorek.rss.core.business.RssEntry;
import de.wieczorek.rss.core.jgroups.CollectorStatus;
import de.wieczorek.rss.core.ui.Controller;
import io.undertow.server.HttpHandler;
import io.undertow.util.Headers;

@ApplicationScoped
public class RssHandlerProducer {

    @Inject
    private Controller controller;

    @Produces
    private HttpHandler handler = (exchange) -> {
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
	    status.setStatus(controller.isStarted() ? "running" : "stopped");
	    exchange.getResponseSender().send(new ObjectMapper().writeValueAsString(status));

	} else if (exchange.getRequestPath().equals("/rss-entries/24h")) {
	    exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, null);
	    exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
	    exchange.getResponseSender().send(new ObjectMapper().writeValueAsString(controller.readEntries24h()));
	} else if (exchange.getRequestPath().startsWith("/rss-entries/")) {
	    exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, null);
	    exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
	    List<RssEntry> entries = controller.readEntriesAfter(
		    new Date(Long.valueOf(exchange.getRequestPath().replace("/rss-entries/", "").replaceAll("/", ""))));
	    exchange.getResponseSender().send(new ObjectMapper().writeValueAsString(entries));
	} else {
	    exchange.setStatusCode(404);
	}
    };

}
