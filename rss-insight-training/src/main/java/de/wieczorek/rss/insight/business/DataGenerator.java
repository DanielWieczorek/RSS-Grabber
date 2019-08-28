package de.wieczorek.rss.insight.business;

import de.wieczorek.nn.IDataGenerator;
import de.wieczorek.rss.classification.types.RssEntry;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import java.util.List;

@ApplicationScoped
public class DataGenerator implements IDataGenerator<RssEntry> {
    @Override
    public List<RssEntry> generate() {
        return ClientBuilder.newClient().target("http://wieczorek.io:10020/classified")
                .request(MediaType.APPLICATION_JSON).get(new GenericType<List<RssEntry>>() {
                });
    }
}
