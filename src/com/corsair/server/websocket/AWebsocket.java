package com.corsair.server.websocket;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author Administrator
 *
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface AWebsocket {
	String vport();
}
