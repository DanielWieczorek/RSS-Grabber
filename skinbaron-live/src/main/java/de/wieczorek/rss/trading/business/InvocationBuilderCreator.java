package de.wieczorek.rss.trading.business;

import de.wieczorek.core.jackson.ObjectMapperContextResolver;
import de.wieczorek.rss.trading.config.ServiceConfiguration;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.util.Map;

@ApplicationScoped
public class InvocationBuilderCreator {

    private Client client = ClientBuilder.newClient().register(new ObjectMapperContextResolver());

    public Invocation.Builder createV1(String path, ServiceConfiguration config) {
        return client.target("https://api.skinbaron.de")
                .path(path)
                .queryParam("apikey", config.getApiKey())
                .request(MediaType.APPLICATION_JSON)
                .header("x-requested-with", "XMLHttpRequest")
                .header("content-type", MediaType.APPLICATION_JSON);
    }

    public Invocation.Builder createV2(String path, ServiceConfiguration config) {
        return client.target("https://skinbaron.de/")
                .path(path)
                //  .queryParam("apikey", config.getApiKey())
                .queryParam("")
                .request(MediaType.APPLICATION_JSON)
                .header("Cookie", "AUTHID=\"" + config.getAuthCookie() + "\"")
                .header("x-requested-with", "XMLHttpRequest")
                .header("content-type", MediaType.APPLICATION_JSON);
    }

    public Invocation.Builder createV2(String path, ServiceConfiguration config, Map<String, Object> queryParams) {
        WebTarget target = client.target("https://skinbaron.de/")
                .path(path);
        for (Map.Entry<String, Object> entry : queryParams.entrySet()) {
            target = target.queryParam(entry.getKey(), entry.getValue());
        }
        return target
                //  .queryParam("apikey", config.getApiKey())
                .queryParam("")
                .request(MediaType.APPLICATION_JSON)
                .header("Cookie", "AUTHID=\"" + config.getAuthCookie() + "\"")
                .header("x-requested-with", "XMLHttpRequest")
                .header("content-type", MediaType.APPLICATION_JSON);
    }

    public Invocation.Builder createV2Browse(String path, ServiceConfiguration config, Map<String, Object> queryParams) {
        WebTarget target = client.target("https://skinbaron.de")
                .path(path);
        for (Map.Entry<String, Object> entry : queryParams.entrySet()) {
            target = target.queryParam(entry.getKey(), entry.getValue());
        }
        return target
                //  .queryParam("apikey", config.getApiKey())
                .queryParam("")
                .request();
        //    .header("Cookie", "AUTHID=\"" + config.getAuthCookie() + "\"");
    }

}

