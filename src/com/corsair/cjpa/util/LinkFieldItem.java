package com.corsair.cjpa.util;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 关联字段项
 * 
 * @author Administrator
 *
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface LinkFieldItem {
	String mfield();

	String lfield();
}
