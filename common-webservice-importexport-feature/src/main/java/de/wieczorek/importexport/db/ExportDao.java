package de.wieczorek.importexport.db;

import java.util.Collection;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public interface ExportDao {
    Class<?> getEntityType();

    Collection<? extends Object> findAll();
}
