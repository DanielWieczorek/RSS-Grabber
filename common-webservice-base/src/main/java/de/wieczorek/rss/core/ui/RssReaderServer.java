package de.wieczorek.rss.core.ui;

import java.util.EnumSet;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.DispatcherType;

import de.wieczorek.rss.core.timer.RecurrentTaskManager;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.jboss.weld.environment.servlet.Listener;

import de.wieczorek.rss.core.config.port.RestPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class RssReaderServer {
	private static final Logger logger = LoggerFactory.getLogger(RssReaderServer.class);


    private Server server;

    @Inject
    @RestPort
    private Integer port;

    public RssReaderServer() {

    }

    public void start() {
	initServer();

	try {
	    server.start();
	    server.join();
	} catch (Exception e) {
	    throw new RuntimeException(e);
	} finally {
	    server.destroy();
	}
    }

    private void initServer() {
	ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
	context.setContextPath("/");
	context.addEventListener(new Listener());

	server = new Server(port);
	server.setHandler(context);

	// Add the filter, and then use the provided FilterHolder to configure it
	FilterHolder cors = context.addFilter(CrossOriginFilter.class, "/*", EnumSet.of(DispatcherType.REQUEST));
	cors.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, "*");
	cors.setInitParameter(CrossOriginFilter.ACCESS_CONTROL_ALLOW_ORIGIN_HEADER, "*");
	cors.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "GET,POST,HEAD,OPTIONS");
	cors.setInitParameter(CrossOriginFilter.ALLOWED_HEADERS_PARAM,
		"X-Requested-With,Content-Type,Accept,Origin,Authorization");

	ServletHolder jerseyServlet = context.addServlet(org.glassfish.jersey.servlet.ServletContainer.class, "/*");
	jerseyServlet.setInitOrder(0);

		logger.info("found the following REST resource classes: "+ServiceCollector.getClasses().stream().collect(Collectors.joining(",")));

	jerseyServlet.setInitParameter("jersey.config.server.provider.classnames",
		ServiceCollector.getClasses().stream().collect(Collectors.joining(",")));

    }

}
