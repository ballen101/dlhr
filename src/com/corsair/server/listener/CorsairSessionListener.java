package com.corsair.server.listener;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import com.corsair.dbpool.CDBConnection;
import com.corsair.dbpool.util.Logsw;
import com.corsair.dbpool.util.Systemdate;
import com.corsair.server.base.CSContext;
import com.corsair.server.base.ConstsSw;
import com.corsair.server.base.Login;
import com.corsair.server.csession.CSession;
import com.corsair.server.generic.Shwsessionct;
import com.corsair.server.util.TerminalType;
import com.corsair.server.websocket.CWebSocketPool;

public class CorsairSessionListener implements HttpSessionListener, ServletRequestListener {
	public static int sessioncount = 0;
	private HttpServletRequest request;
	private static int sessioncount_h = 0;// 一小时内访问量
	private static String per_ymh = null;// 前一个小时字符串
	private SimpleDateFormat date_formater = new SimpleDateFormat("yyyy-MM-dd HH"); //
	private Shwsessionct sct = null;
	private ThreadSaveCT tsct = null;
	private int sleeptime = 1000 * 60;// 10分钟

	public CorsairSessionListener() {
		// TODO Auto-generated constructor stub】
		// Logsw.debug("CorsairSessionListener create..................................");
		per_ymh = date_formater.format(new Date());
		// tsct = new ThreadSaveCT();
		// tsct.start();
	}

	public void sessionCreated(HttpSessionEvent hse) {
		sessioncount++;
		sessioncount_h++;
		Logsw.debug("add session1111111111111:" + hse.getSession().getId());
	}

	public class ThreadSaveCT extends Thread {
		public void run() {
			while (!isInterrupted()) {
				String ymh = date_formater.format(new Date());
				// Logsw.debug("ymh:" + ymh + ";per_ymh:" + per_ymh + ";在线数量:" + sessioncount + ";上一小时访问量:" + sessioncount_h);
				if (!ymh.equals(per_ymh)) {// 新的一个小时
					try {
						savect();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					// sessioncount_h = 0;// 重置计数
					sessioncount_h = sessioncount;// 重置计数 为在线数量
					per_ymh = ymh;// 更新前一小时标志
				}
				try {
					sleep(sleeptime);// 延迟
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					// e.printStackTrace();
				}
			}
		}

	}

	/**
	 * 保存小时访问量
	 * 
	 * 如果有多个应用,都是锁定最后一条数据，所以行锁 和 表锁 完全一样
	 * 
	 * @throws Exception
	 */
	private void savect() throws Exception {
		// System.out.println("savect1111111111111111111111");
		if (sct == null)
			sct = new Shwsessionct();

		CDBConnection con = sct.pool.getCon(this);
		con.startTrans();
		try {
			String sqlstr = "SELECT * FROM shwsessionct WHERE ymhh='" + per_ymh + "' for update ";
			sct.findBySQL4Update(con, sqlstr, false);
			sct.ymhh.setValue(per_ymh);
			sct.scnum.setAsInt(sct.scnum.getAsIntDefault(0) + sessioncount_h);
			sct.acactive.setAsInt(sct.acactive.getAsIntDefault(0) + sessioncount);
			sct.save(con);
			con.submit();
		} catch (Exception e) {
			con.rollback();
			throw e;
		} finally {
			con.close();
		}
	}

	public void sessionDestroyed(HttpSessionEvent hse) {
		// TODO Auto-generated method stub
		try {
			Login.dologinout();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sessioncount--;
		Logsw.debug("remove session:" + hse.getSession().getId());
	}

	@Override
	public void requestDestroyed(ServletRequestEvent arg0) {
		// TODO Auto-generated method stub
		try {
			// System.out.println("requestDestroyed:"+((HttpServletRequest)
			// arg0.getServletRequest()).getSession().getId());
			CSContext.removeSession();
			CSContext.removeRequest();
			CSContext.removeParms();
			CSContext.removePostdata();
			CSContext.removeisMultipartContent();
			CSContext.removeservlet();
			CSContext.removeactiontype();
			CSContext.removeTermType();
		} catch (Exception e) {
			try {
				Logsw.error(e);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

	@Override
	public void requestInitialized(ServletRequestEvent arg0) {
		try {
			// System.out.println("requestInitialized:" + ((HttpServletRequest)
			// arg0.getServletRequest()).getSession().getId());
			request = (HttpServletRequest) arg0.getServletRequest();
			String userAgent = request.getHeader("user-agent");
			CSContext.setTermType(TerminalType.getTerminalType(userAgent));
			CSContext.setSession(request.getSession(true));
			CSContext.setRequest(request);
		} catch (Exception e) {
			try {
				Logsw.error(e);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
}
