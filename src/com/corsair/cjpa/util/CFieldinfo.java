package com.corsair.cjpa.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.sql.Types;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.JPAControllerBase;

/**字段注解
 * @author Administrator
 *
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CFieldinfo {
	boolean iskey() default false;

	boolean notnull() default false;

	boolean autoinc() default false;

	int datetype() default Types.VARCHAR;

	int dicid() default 0;

	String dicclass() default "";// 类似于字典 但比字典更复杂的处理

	int codeid() default 0;

	String fieldname();

	String caption() default "";

	String save_dsfunc() default "";// 数据来源 通过类函数获取数据来源

	String defvalue() default "";// 默认值
	
	int precision() default 0;//数据长度
	
	int scale() default 0;//小数位数

	// Class<?> outkey() default CField.class;// 外键 插入时候 保证 外键对应的数据存在；

	CkItem[] dcfds() default {};// 删除时候 检查约束性

}
