package de.wieczorek.rss.core.timer;

import de.wieczorek.rss.core.persistence.EntityManagerContext;

import javax.enterprise.inject.Stereotype;
import javax.enterprise.util.Nonbinding;
import javax.inject.Qualifier;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.concurrent.TimeUnit;

@Stereotype
@Retention(RetentionPolicy.RUNTIME)
@EntityManagerContext
@Qualifier
public @interface RecurrentTask {
    @Nonbinding
    int interval() default 0;

    @Nonbinding
    TimeUnit unit() default TimeUnit.MINUTES;
}
