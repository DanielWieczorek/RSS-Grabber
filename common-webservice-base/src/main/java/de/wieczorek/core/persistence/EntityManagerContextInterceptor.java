package de.wieczorek.core.persistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private static final Logger logger = LoggerFactory.getLogger(EntityManagerContextInterceptor.class);


    @AroundInvoke
    public Object intercept(InvocationContext ctx) throws Exception {
        try {
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
        } catch (Exception e) {
            logger.error("while creating transaction: ", e);
            throw e;
        }
    }
}
