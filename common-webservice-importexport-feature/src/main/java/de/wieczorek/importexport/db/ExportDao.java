package de.wieczorek.importexport.db;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public interface ExportDao {
    Class<?> getEntityType();

    List<Object> findAll();
}
