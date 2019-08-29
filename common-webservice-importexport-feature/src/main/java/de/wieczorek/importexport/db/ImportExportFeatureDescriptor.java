package de.wieczorek.importexport.db;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.ws.rs.HttpMethod;

import de.wieczorek.rss.core.feature.FeatureAction;
import de.wieczorek.rss.core.feature.FeatureDescriptor;
import de.wieczorek.rss.core.feature.FeatureType;

@Dependent
public class ImportExportFeatureDescriptor extends FeatureDescriptor {

    public ImportExportFeatureDescriptor() {

        List<FeatureAction> actions = new ArrayList<>();

        actions.add(new FeatureAction("import", "/feature/import", HttpMethod.POST));
        actions.add(new FeatureAction("export", "/feature/export", HttpMethod.GET));

        setActions(actions);
        setType(FeatureType.IMPORT_EXPORT);
        setDescription("Feature to import or export data");
    }

}
