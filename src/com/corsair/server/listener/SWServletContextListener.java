package com.corsair.server.listener;

import javax.servlet.ServletContextEvent;

/**
 * 环境启动停止监听
 * 
 * @author shangwen
 * 
 */
public abstract interface SWServletContextListener {
	public abstract void contextInitialized(ServletContextEvent sContextE);

	public abstract void contextDestroyed(ServletContextEvent sContextE);
}
