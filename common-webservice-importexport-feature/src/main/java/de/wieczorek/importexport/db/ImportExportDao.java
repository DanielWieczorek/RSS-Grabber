package de.wieczorek.importexport.db;

import java.util.Collection;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public abstract class ImportExportDao<T> {

    public abstract Class<T> getEntityType();

    public abstract List<T> findAll();

    @SuppressWarnings("unchecked")
    public void persistAllAsObject(Collection<Object> entries) {
        Collection<T> toInsert = (Collection<T>) entries;
        toInsert.removeAll(findAll());
        this.persistAll(toInsert);
    }

    protected abstract void persistAll(Collection<T> entries);

}
