package com.corsair.server.retention;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ACOAction {
	/**
	 * 方法名称，给页面调用
	 * 
	 * @return
	 */
	String eventname();

	/**
	 * 是否需要验证
	 * 
	 * @return
	 */
	boolean Authentication() default false;

	/**
	 * 是否公开，无需CO授权
	 * 
	 * @return
	 */
	boolean ispublic() default false;

	/**
	 * 是否内网
	 * 
	 * @return
	 */
	boolean isIntranet() default false;

	/**
	 * 备注
	 * 
	 * @return
	 */
	String notes() default "";
}
