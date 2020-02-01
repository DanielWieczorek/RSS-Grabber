package de.wieczorek.importexport.ui;

import de.wieczorek.importexport.db.ImportExportDao;
import de.wieczorek.importexport.type.ImportExportData;
import de.wieczorek.core.persistence.EntityManagerContext;
import de.wieczorek.core.ui.Resource;
import org.jboss.weld.inject.WeldInstance;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Resource
@ApplicationScoped
@EntityManagerContext
@Path("feature")
public class ExportResource {

    @Inject
    @Any
    private WeldInstance<ImportExportDao<?>> exportDaos;

    @Inject
    @Resource
    private WeldInstance<Object> res;

    @SuppressWarnings("rawtypes")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("export")
    public ImportExportData exportData() {
        ImportExportData data = new ImportExportData();
        Map<String, Collection> map = new HashMap<>();
        data.setData(map);

        exportDaos.forEach(dao -> {
            map.put(dao.getEntityType().getName(), dao.findAll());
        });

        return data;
    }

}
