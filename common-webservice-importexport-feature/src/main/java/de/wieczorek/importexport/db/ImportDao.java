package de.wieczorek.importexport.db;

import java.util.Collection;

public interface ImportDao {
    Class<?> getEntityType();

    void persistAll(Collection<Object> entries);
}
