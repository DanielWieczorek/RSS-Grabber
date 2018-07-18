package de.wieczorek.rss.insight.ui;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.undertow.server.HttpHandler;
import io.undertow.util.HttpString;

@ApplicationScoped
public class RssHandlerProducer {

    @Inject
    private Controller controller;

    @Produces
    private HttpHandler handler = (exchange) -> {
	exchange.getResponseHeaders().put(new HttpString("Access-Control-Allow-Origin"), "http://localhost:4200");
	exchange.getResponseHeaders().put(new HttpString("Access-Control-Allow-Methods"), "GET,POST");
	exchange.getResponseHeaders().put(new HttpString("Access-Control-Allow-Headers"), "Content-Type");

	if (exchange.getRequestPath().equals("/train")) {
	    controller.trainNeuralNetwork();
	} else if (exchange.getRequestPath().equals("/sentiment")) {
	    exchange.getResponseSender().send(new ObjectMapper().writeValueAsString(controller.predict()));
	}
    };

}
