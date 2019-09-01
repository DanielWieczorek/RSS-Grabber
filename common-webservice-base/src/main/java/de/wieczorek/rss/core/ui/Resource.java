package de.wieczorek.rss.core.ui;

import de.wieczorek.rss.core.persistence.EntityManagerContext;

import javax.enterprise.inject.Stereotype;
import javax.inject.Qualifier;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Qualifier
@Stereotype
@EntityManagerContext
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD})
public @interface Resource {

}
