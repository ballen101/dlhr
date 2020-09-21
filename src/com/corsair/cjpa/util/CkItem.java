package com.corsair.cjpa.util;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.corsair.cjpa.CJPABase;

/**
 * @author Administrator
 *
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface CkItem {
	Class<?> entity();

	String fdname();
}
