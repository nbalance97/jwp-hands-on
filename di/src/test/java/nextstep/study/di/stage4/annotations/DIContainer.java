package nextstep.study.di.stage4.annotations;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 스프링의 BeanFactory, ApplicationContext에 해당되는 클래스
 */
class
DIContainer {

    private final Set<Object> beans;

    public DIContainer(final Set<Class<?>> classes) {
        this.beans = new HashSet<>();
        initialize(classes);
    }

    private void initialize(Set<Class<?>> classes) {
        for (Class<?> clazz : classes) {
            try {
                Constructor<?> constructor = clazz.getDeclaredConstructor();
                constructor.setAccessible(true); // private 생성자에 대해서도 접근을 열어주어야 한다.
                beans.add(constructor.newInstance());
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }

        for (Class<?> clazz : classes) {
            Object instance = getBean(clazz);
            Field[] declaredFields = clazz.getDeclaredFields();

            for (Field field : declaredFields) {
                field.setAccessible(true);

                // inject 어노테이션이 존재하지 않다면 애초에 주입이 필요가 없다.
                if (!field.isAnnotationPresent(Inject.class)) {
                    continue;
                }

                // static final이라면 빈 주입하지 않음.
                int modifiers = field.getModifiers();

                if (Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers)) {
                    continue;
                }

                Object fieldValue = null;

                // 기존에 이미 설정된 값이 있다면 주입하지 않는다.
                try {
                    fieldValue = field.get(instance);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }

                if (fieldValue != null) {
                    continue;
                }


                // field.getDeclaringClass() : 필드를 선언한 클래스의 타입임. 필드의 타입 아님!!
                // field.getType()가 필드의 타입이다.이
                // 인터페이스타입.isAssignableFrom(구현타입)은 구현타입이 인터페이스의 구현타입인지 확인 가능하다.
                Object bean = beans.stream()
                        .filter(b -> field.getType().isAssignableFrom(b.getClass()))
                        .findAny()
                        .orElse(null);

                System.out.println(field.getType() + "->" + bean);

                if (bean == null) {
                    continue;
                }

                // 모든 과정을 넘어왔다면 빈을 주입할 수 있다.
                try {
                    field.set(instance, bean);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(final Class<T> aClass) {
        return (T) beans.stream()
                .filter(bean -> bean.getClass() == aClass)
                .findAny()
                .orElseThrow(() -> new NoSuchElementException("없습니다."));
    }
    public static DIContainer createContainerForPackage(final String rootPackageName) {
        Set<Class<?>> classes = ClassPathScanner.getAllClassesInPackage(rootPackageName);
        System.out.println(classes);
        // 개선점 1. 어노테이션 타입도 리스트로 관리한다면 더 좋을 거 같다.
        Set<Class<?>> classesWithAnnotation = classes.stream()
                .filter(clazz -> hasAnnotation(clazz, Repository.class) || hasAnnotation(clazz, Service.class))
                .collect(Collectors.toSet());

        System.out.println(classesWithAnnotation);

        return new DIContainer(classesWithAnnotation);
    }

    private static boolean hasAnnotation(Class<?> clazz, Class<? extends Annotation> annotation) {
        return clazz.getDeclaredAnnotation(annotation) != null;
    }
}
