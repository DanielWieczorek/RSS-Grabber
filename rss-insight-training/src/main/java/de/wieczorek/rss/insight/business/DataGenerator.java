package de.wieczorek.rss.insight.business;

import de.wieczorek.nn.IDataGenerator;
import de.wieczorek.rss.classification.types.ClassifiedRssEntry;
import de.wieczorek.rss.classification.types.ui.RssAdvisorRemoteRestCaller;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

@ApplicationScoped
public class DataGenerator implements IDataGenerator<ClassifiedRssEntry> {
    @Inject
    private RssAdvisorRemoteRestCaller rssAdvisorCaller;

    @Override
    public List<ClassifiedRssEntry> generate() {
        return rssAdvisorCaller.classified();
    }
}
