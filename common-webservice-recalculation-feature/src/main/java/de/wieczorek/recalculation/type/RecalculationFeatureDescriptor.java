package de.wieczorek.recalculation.type;

import de.wieczorek.core.feature.FeatureAction;
import de.wieczorek.core.feature.FeatureDescriptor;
import de.wieczorek.core.feature.FeatureType;

import javax.enterprise.context.Dependent;
import javax.ws.rs.HttpMethod;
import java.util.ArrayList;
import java.util.List;

@Dependent
public class RecalculationFeatureDescriptor extends FeatureDescriptor {

    public RecalculationFeatureDescriptor() {

        List<FeatureAction> actions = new ArrayList<>();

        actions.add(new FeatureAction("start", "/recalculation/import", HttpMethod.GET));
        actions.add(new FeatureAction("stop", "/recalculation/export", HttpMethod.GET));

        setActions(actions);
        setType(FeatureType.RECALCULATION);
        setDescription("Feature to start and stop the recalculation");
    }

}
