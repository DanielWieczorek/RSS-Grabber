package de.wieczorek.core.feature;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

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
