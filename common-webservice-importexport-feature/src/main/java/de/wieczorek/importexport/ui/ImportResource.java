package de.wieczorek.importexport.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.wieczorek.importexport.db.ImportExportDao;
import de.wieczorek.importexport.type.ImportExportData;
import de.wieczorek.core.persistence.EntityManagerContext;
import de.wieczorek.core.ui.Resource;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.util.Collection;
import java.util.stream.Collectors;

@Resource
@ApplicationScoped
@EntityManagerContext
@Path("feature")
public class ImportResource {

    @Inject
    private Instance<ImportExportDao<?>> importDaos;

    @POST
    @SuppressWarnings("unchecked")
    @Path("import")
    public void importData(ImportExportData data) {
        ObjectMapper mapper = new ObjectMapper();
        importDaos.forEach(dao -> {
            Collection<Object> entities = (Collection<Object>) data.getData().get(dao.getEntityType().getName())
                    .stream().map(item -> {
                        return mapper.convertValue(item, dao.getEntityType());
                    }).collect(Collectors.toList());
            dao.persistAllAsObject(entities);
        });
    }

}
