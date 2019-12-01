package de.wieczorek.rss.core.persistence;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

public final class EntityManagerHelper {

    private EntityManagerHelper() {

    }

    public static <D> D getSingleResultOrNull(TypedQuery<D> query) {
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }

    }
}
