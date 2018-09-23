package de.wieczorek.rss.core.jackson;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@ApplicationScoped
@Provider
public class ObjectMapperContextResolver implements ContextResolver<ObjectMapper> {

    private final ObjectMapper mapper;

    public ObjectMapperContextResolver() {
	this.mapper = createObjectMapper();
    }

    @Override
    public ObjectMapper getContext(Class<?> type) {
	return mapper;
    }

    private ObjectMapper createObjectMapper() {
	ObjectMapper mapper = new ObjectMapper().registerModule(new Jdk8Module()).registerModule(new JavaTimeModule());
	return mapper;
    }
}