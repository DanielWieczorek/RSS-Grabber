package de.wieczorek.rss.core.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.xnio.Options;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.wieczorek.rss.core.business.RssEntry;
import de.wieczorek.rss.core.config.RestPort;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.BlockingHandler;
import io.undertow.util.Headers;
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

			if (exchange.getRequestPath().equals("/find")) {
			    exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
			    List<RssEntry> unclassfiedRssEntries = controller.readUnclassifiedEntries();
			    exchange.getResponseSender()
				    .send(new ObjectMapper().writeValueAsString(unclassfiedRssEntries));
			} else if (exchange.getRequestMethod().equals(HttpString.tryFromString("POST"))
				&& exchange.getRequestPath().equals("/classify")) {

			    BufferedReader reader = null;
			    StringBuilder builder = new StringBuilder();

			    try {
				exchange.startBlocking();
				reader = new BufferedReader(new InputStreamReader(exchange.getInputStream()));

				String line;
				while ((line = reader.readLine()) != null) {
				    builder.append(line);
				}
			    } catch (IOException e) {
				e.printStackTrace();
			    } finally {
				if (reader != null) {
				    try {
					reader.close();
				    } catch (IOException e) {
					e.printStackTrace();
				    }
				}
			    }

			    String body = builder.toString();

			    RssEntry entry = new ObjectMapper().readerFor(RssEntry.class).readValue(body);

			    controller.updateClassification(entry);

			}

		    }
		})).setWorkerOption(Options.WORKER_IO_THREADS, 1) //
		.setWorkerOption(Options.WORKER_TASK_CORE_THREADS, 1) //
		.setWorkerOption(Options.WORKER_TASK_MAX_THREADS, 1) //
		.setServerOption(Options.WORKER_IO_THREADS, 1) //
		.build();
    }

}
