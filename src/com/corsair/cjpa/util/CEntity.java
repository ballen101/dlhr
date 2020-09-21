package com.corsair.cjpa.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.corsair.cjpa.JPAControllerBase;

/**实体注解
 * @author Administrator
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CEntity {

	String dbpool() default "";

	String tablename() default "";

	String caption() default "";

	// String controller() default "";

	Class<?> controller() default JPAControllerBase.class;

}
