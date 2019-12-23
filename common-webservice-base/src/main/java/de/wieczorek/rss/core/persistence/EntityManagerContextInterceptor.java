package de.wieczorek.rss.core.persistence;

import javax.annotation.Priority;
import javax.enterprise.context.Dependent;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.persistence.EntityTransaction;

@Interceptor
@EntityManagerContext
@Priority(value = Interceptor.Priority.APPLICATION)
@Dependent
public class EntityManagerContextInterceptor {


    @AroundInvoke
    public Object intercept(InvocationContext ctx) throws Exception {
        EntityManagerProvider.recreateEntityManager();
        EntityTransaction transaction = EntityManagerProvider.getEntityManager().getTransaction();
        transaction.begin();

        Object result = ctx.proceed();

        if (transaction.getRollbackOnly()) {
            transaction.rollback();
        } else {
            transaction.commit();
        }

        EntityManagerProvider.destroyEntityManager();
        return result;
    }
}
