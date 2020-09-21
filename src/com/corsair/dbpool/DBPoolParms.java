package com.corsair.dbpool;

/**
 * 连接池参数
 * 
 * @author Administrator
 *
 */
public class DBPoolParms {
	/**
	 * 连接池名称
	 */
	public String name = "";
	/**
	 * 是否默认连接池
	 */
	public boolean isdefault = false;
	/**
	 * 数据库类型
	 */
	public int dbtype = 1;
	/**
	 * 连接器
	 */
	public String dirver = "oracle.jdbc.driver.OracleDriver";
	/**
	 * 连接串
	 */
	public String url = "";
	/**
	 * 数据库名
	 */
	public String schema = "";
	/**
	 * 登录用户名
	 */
	public String user = "";
	/**
	 * 登录密码
	 */
	public String password = "";

	/**
	 * 是否加密的密码
	 */
	public boolean encpassword = false;
	/**
	 * 初始化连接数
	 */
	public int minsession = 5;
	/**
	 * 最大连接数
	 */
	public int maxsession = 500;
	/**
	 * 连接池检查一遍链接间隔时间 秒
	 */
	public int checkcontime = 10;//
	/**
	 * 数据库session超时时间 获取连接 后 超时 范围内没有处理的 // 下次链接检查的时候将重置为可用状态
	 */
	public int timeout = 60;//
}
