package de.wieczorek.core.timer;

import javax.enterprise.util.Nonbinding;
import javax.inject.Qualifier;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.concurrent.TimeUnit;

@Retention(RetentionPolicy.RUNTIME)
@Qualifier
public @interface RecurrentTask {
    @Nonbinding
    int interval() default 0;

    @Nonbinding
    TimeUnit unit() default TimeUnit.MINUTES;
}
