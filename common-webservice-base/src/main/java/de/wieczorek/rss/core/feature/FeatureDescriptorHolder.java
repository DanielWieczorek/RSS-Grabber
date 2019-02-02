package de.wieczorek.rss.core.feature;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

@ApplicationScoped
public class FeatureDescriptorHolder {

    @Inject
    private Instance<FeatureDescriptor> features;

    public List<FeatureDescriptor> getFeatures() {
	List<FeatureDescriptor> result = new ArrayList<FeatureDescriptor>();
	features.forEach(result::add);
	return result;
    }

}
