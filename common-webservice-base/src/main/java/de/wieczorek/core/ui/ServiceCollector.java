package de.wieczorek.core.ui;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.inject.spi.WithAnnotations;
import javax.ws.rs.Path;
import java.util.HashSet;
import java.util.Set;

@ApplicationScoped
public class ServiceCollector implements Extension {

    private static final Set<String> classNames = new HashSet<>();

    protected <T> void processAnnotatedType(@Observes @WithAnnotations({Path.class}) ProcessAnnotatedType<T> pat) {
        classNames.add(pat.getAnnotatedType().getJavaClass().getCanonicalName());

    }

    public static Set<String> getClasses() {
        return classNames;
    }

}
