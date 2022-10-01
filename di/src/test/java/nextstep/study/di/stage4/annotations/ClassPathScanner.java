package nextstep.study.di.stage4.annotations;

import java.util.Set;

import org.assertj.core.internal.bytebuddy.matcher.SubTypeMatcher;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.FilterBuilder;

public class ClassPathScanner {

    public static Set<Class<?>> getAllClassesInPackage(final String packageName) {
        // Object.class의 서브타입으로 조회 -> 모든 클래스가 조회된다.
        // 기본적으로 Object.class의 서브타입 조회가 되지 않는다. 따라서 Scanners 커스터마이징이 필요하다.
        // 기본 필터를 사용하지 않고 텅 빈 필터를 사용하도록 한다.

        Scanners customScanner = Scanners.SubTypes.filterResultsBy(new FilterBuilder());
        Reflections reflections = new Reflections(packageName, customScanner);

        return reflections.getSubTypesOf(Object.class);
    }
}
