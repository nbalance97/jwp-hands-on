package reflection;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

class Junit3TestRunner {

    @Test
    void run() throws Exception {
        Class<Junit3Test> clazz = Junit3Test.class;
        Junit3Test instance = clazz.getConstructor().newInstance();

        Arrays.stream(clazz.getMethods())
                .filter(eachClass -> eachClass.getName().startsWith("test"))
                .forEach(method -> {
                    try {
                        method.invoke(instance);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    } catch (InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                });
        // TODO Junit3Test에서 test로 시작하는 메소드 실행
    }
}
