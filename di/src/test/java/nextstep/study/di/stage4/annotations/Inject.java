package nextstep.study.di.stage4.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Inject {
}

// spring의 Autowired
// 자바 표준에서는 Inject라는 어노테이션으로 주입해 준다.
