package de.wieczorek.rss.core.persistence;

import javax.annotation.Priority;
import javax.enterprise.context.Dependent;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

@Interceptor
@EntityManagerContext
@Priority(value = Interceptor.Priority.APPLICATION)
@Dependent
public class EntityManagerContextInterceptor {


    @AroundInvoke
    public Object intercept(InvocationContext ctx) throws Exception {
        EntityManagerProvider.recreateEntityManager();

        Object result = ctx.proceed();
        EntityManagerProvider.destroyEntityManager();
        return result;
    }
}
