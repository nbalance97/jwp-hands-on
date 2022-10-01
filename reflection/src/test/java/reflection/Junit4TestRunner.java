package reflection;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

class Junit4TestRunner {

    @Test
    void run() throws Exception {
        Class<Junit4Test> clazz = Junit4Test.class;
        Junit4Test instance = clazz.getConstructor().newInstance();
        Arrays.stream(clazz.getMethods())
                .filter(this::hasAnnotation)
                .forEach(method -> {
                    try {
                        method.invoke(instance);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    } catch (InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                });
        // TODO Junit4Test에서 @MyTest 애노테이션이 있는 메소드 실행
    }

    private boolean hasAnnotation(Method method) {
        return method.getDeclaredAnnotation(MyTest.class) != null;
    }
}
