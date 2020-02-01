package de.wieczorek.importexport.db;

import de.wieczorek.core.feature.FeatureAction;
import de.wieczorek.core.feature.FeatureDescriptor;
import de.wieczorek.core.feature.FeatureType;

import javax.enterprise.context.Dependent;
import javax.ws.rs.HttpMethod;
import java.util.ArrayList;
import java.util.List;

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
