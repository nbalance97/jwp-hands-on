package aop.stage0;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import aop.DataAccessException;
import aop.Transactional;
import aop.config.DataSourceConfig;
import aop.service.AppUserService;
import aop.service.UserService;

public class TransactionHandler implements InvocationHandler {

    private final PlatformTransactionManager transactionManager;
    private final AppUserService appUserService;

    public TransactionHandler(PlatformTransactionManager transactionManager, AppUserService appUserService) {
        this.transactionManager = transactionManager;
        this.appUserService = appUserService;
    }

    /**
     * @Transactional 어노테이션이 존재하는 메서드만 트랜잭션 기능을 적용하도록 만들어보자.
     */
    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
        Method realMethod = Arrays.stream(appUserService.getClass().getDeclaredMethods())
                .filter(m -> compareTwoMethodSame(m, method))
                .findAny()
                .orElseThrow();

        if (realMethod.isAnnotationPresent(Transactional.class)) {
            final var transactionStatus = transactionManager.getTransaction(new DefaultTransactionDefinition());
            try {
                Object methodReturnValue = method.invoke(appUserService, args);
                transactionManager.commit(transactionStatus);
                return methodReturnValue;
            } catch (Exception e1) {
                transactionManager.rollback(transactionStatus);
                throw new DataAccessException();
            }
        } else {
            try {
                return method.invoke(appUserService, args);
            } catch (Exception e1) {
                throw new DataAccessException();
            }
        }
    }

    private boolean compareTwoMethodSame(Method a, Method b) {
        if (!a.getName().equals(b.getName())) {
            return false;
        }
        Class<?>[] aParams = a.getParameterTypes();
        Class<?>[] bParams = b.getParameterTypes();
        if (aParams.length != bParams.length) {
            return false;
        }

        for (int i = 0; i < aParams.length; i++) {
            if (!aParams[i].getTypeName().equals(bParams[i].getTypeName())){
                return false;
            }
        }

        return true;
    }
}
