package de.wieczorek.rss.types.ui;

import de.wieczorek.core.ui.Target;
import de.wieczorek.core.ui.TargetType;
import de.wieczorek.rss.types.RssEntry;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import java.util.List;

@ApplicationScoped
public class RssDataCollectionLocalRestCaller implements CallableResource {

    @Inject
    @Target(type = TargetType.LOCAL, port = 8020)
    private WebTarget target;

    public List<RssEntry> entriesFromLast24h() {
        return target.path("/rss-entries/24h").request(MediaType.APPLICATION_JSON).get(new GenericType<List<RssEntry>>() {
        });
    }

    public List<RssEntry> entriesAfter(long unixTimestamp) {
        return target.path("/rss-entries/" + unixTimestamp).request(MediaType.APPLICATION_JSON).get(new GenericType<List<RssEntry>>() {
        });
    }

    public List<RssEntry> allEntries() {
        return target.path("/rss-entries/").request(MediaType.APPLICATION_JSON).get(new GenericType<List<RssEntry>>() {
        });
    }
}
