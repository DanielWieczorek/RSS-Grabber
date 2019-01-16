package de.wieczorek.importexport.ui;

import java.util.Collection;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import de.wieczorek.importexport.db.ImportDao;
import de.wieczorek.importexport.type.ImportExportData;
import de.wieczorek.rss.core.ui.Resource;

@Resource
@ApplicationScoped
@Path("feature")
public class ImportResource {

    @Inject
    private Instance<ImportDao> importDaos;

    @POST
    @SuppressWarnings("unchecked")
    @Path("import")
    public void importData(ImportExportData data) {
	importDaos.forEach(dao -> {
	    Collection<Object> entities = data.getData().get(dao.getEntityType().getName());
	    dao.persistAll(entities);
	});
    }

}
