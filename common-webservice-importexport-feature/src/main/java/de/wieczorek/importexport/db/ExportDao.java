package de.wieczorek.importexport.db;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public interface ExportDao {
    Class<?> getEntityType();

    List<Object> findAll();
}
