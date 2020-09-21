package com.corsair.server.listener;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.corsair.dbpool.util.Logsw;
import com.corsair.dbpool.util.Systemdate;
import com.corsair.server.base.ConstsSw;
import com.corsair.server.listener.SWSpecClass.SwSpecClassType;
import com.corsair.server.task.TaskCDay;
import com.corsair.server.task.TaskScanTokenTimeout;
import com.corsair.server.task.TaskScanWorkFlowPress;
import com.corsair.server.task.TaskSetUserOutWFAgent;
import com.corsair.server.util.InitSysParms;

public class SysContextListener implements ServletContextListener {
	private List<Timer> timers = new ArrayList<Timer>();

	// 重写contextInitialized
	public void contextInitialized(ServletContextEvent event) {
		long spedt = 1 * 24 * 60 * 60 * 1000;// 每天运行一次
		new InitSysParms(event.getServletContext().getRealPath("/"));
		// String path = ConstsSw._root_path + "\\WEB-INF\\lib";
		// String fname = path + "\\prmz.jar";
		// System.out.println("1111111111111111111111111111111111111:" + fname);
		// File file = new File(fname);
		// if (file.exists()) {
		// if (file.delete())
		// System.out.println("222222222222222222222:文件已删除。");
		// else
		// System.out.println("333333333333333333333:文件删除失败。");
		// } else {
		// System.out.println("文件不存在！");
		// }
		try {
			init0(event);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (ConstsSw.getAppParmBoolean("scan_user_goout")) {
			Date dt = Systemdate.dateDayAdd(new Date(), 1);
			Calendar cal = Calendar.getInstance();
			cal.setTime(dt);
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MINUTE, 0);// 第二天凌晨
			appendTask(TaskSetUserOutWFAgent.class, cal.getTime(), spedt);// 出差代理
		}
		if (ConstsSw.getAppParmBoolean("workflowpress")) {// 应用 允许提醒
			String wfpn = ConstsSw.getSysParm("WFPressNotify");
			int psc = ConstsSw.getSysParmIntDefault("WFPressScanCycle", 10);
			if ((wfpn != null) && (!wfpn.isEmpty()) && (wfpn.indexOf("0") < 0)) {// 需要提醒
				int pc = ConstsSw.getSysParmIntDefault("WFPressCycle", 0);// 提醒周期不为0
				if (pc != 0) {
					appendTask(TaskScanWorkFlowPress.class, new Date(), 60000 * psc);// 流程提醒扫描频率 10分钟
				}
			}
		}

		appendTask(TaskCDay.class, new Date(), spedt);// 刷新消息模板

		if (ConstsSw.getAppParmBoolean("scan_token_timeout")) {
			spedt = 1800000;// 每30分钟运行一次
			appendTask(TaskScanTokenTimeout.class, new Date(), spedt);
		}

	}

	private void appendTask(Class<?> cls, Date firstTime, long period) {
		try {
			if (!TimerTask.class.isAssignableFrom(cls))
				throw new Exception("务必是TimerTask的子类!");
			TimerTask task = (TimerTask) cls.newInstance();
			Timer timer = new Timer(true);
			timers.add(timer);
			timer.schedule(task, firstTime, period);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// 重写contextDestroyed
	public void contextDestroyed(ServletContextEvent event) {
		try {
			destroy0(event);
			if (ConstsSw.fmonitor != null)
				ConstsSw.fmonitor.stop();
			Logsw.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (Timer t : timers) {
			t.cancel();
		}
	}

	private void init0(ServletContextEvent event) {
		for (SWSpecClass spc : ConstsSw._allSpecClass) {
			try {
				if (spc.getClsType() == SwSpecClassType.servetContextInit) {
					Object clsObj = spc.getClsObj();
					@SuppressWarnings("rawtypes")
					Class[] ps = new Class[1];
					ps[0] = ServletContextEvent.class;
					Method method = clsObj.getClass().getMethod("contextInitialized", ps);
					if (method == null)
						throw new Exception("【" + clsObj.getClass().getName() + " contextInitialized】没有找到满足条件的方法");
					Object[] args = new Object[1];
					args[0] = event;
					method.invoke(clsObj, args);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void destroy0(ServletContextEvent event) {
		for (SWSpecClass spc : ConstsSw._allSpecClass) {
			try {
				if (spc.getClsType() == SwSpecClassType.servetContextInit) {
					Object clsObj = spc.getClsObj();
					@SuppressWarnings("rawtypes")
					Class[] ps = new Class[1];
					ps[0] = ServletContextEvent.class;
					Method method = clsObj.getClass().getMethod("contextDestroyed", ps);// contextInitialized
					if (method == null)
						throw new Exception("【" + clsObj.getClass().getName() + " contextDestroyed】没有找到满足条件的方法");
					Object[] args = new Object[1];
					args[0] = event;
					method.invoke(clsObj, args);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
