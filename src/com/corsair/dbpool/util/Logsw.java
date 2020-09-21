package com.corsair.dbpool.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

/**
 * 日志
 * 
 * @author Administrator
 *
 */
public class Logsw {
	public static boolean Debug_Mode = true;
	public static boolean savedblog = false;
	public static String LogPath = "c:\\corsair\\log";
	public static CFileWriter logfile = null;
	public static CFileWriter errfile = null;
	public static CFileWriter dblogfile = null;
	public static String context = null;

	public Logsw() {

	}

	/**
	 * 关闭日志
	 */
	public static void close() {
		if (logfile != null)
			logfile.close();
		if (errfile != null)
			errfile.close();
		if (dblogfile != null)
			dblogfile.close();
	}

	/**
	 * 记录数据库日志
	 * 
	 * @param msg 日志内容
	 */
	public static void dblog(String msg) {
		String wstr = msg + " " + Systemdate.getStrDate();
		if (context != null)
			wstr = "【" + context + "】" + wstr;
		if (Debug_Mode) {
			System.out.println(wstr);
		}
		if (savedblog) {
			writefile(3, wstr);
		}
	}

	// 强制输出
	/**
	 * 输出DEBUG信息
	 * 
	 * @param msg
	 * @param forceout
	 */
	public static void debug(String msg, boolean forceout) {
		String wstr = msg + " " + Systemdate.getStrDate();
		if (context != null)
			wstr = "【" + context + "】" + wstr;
		if (Debug_Mode || forceout) {
			System.out.println(wstr);
		}
		writefile(1, wstr);
	}

	/**
	 * 输出DEBUG信息
	 * 
	 * @param msg
	 */
	public static void debug(String msg) {
		debug(msg, false);
	}

	/**
	 * 输出DEBUG信息 并抛出错误
	 * 
	 * @param e
	 */
	public static void debug(Exception e) {
		String wstr = e.getLocalizedMessage() + " " + Systemdate.getStrDate();
		if (context != null)
			wstr = "【" + context + "】" + wstr;
		if (Debug_Mode) {
			e.printStackTrace();
			System.out.println(Systemdate.getStrDate());
		}
		writefile(1, wstr);
	}

	/**
	 * 输出错误信息
	 * 
	 * @param msg
	 */
	public static void error(String msg) {
		System.out.print(msg);
		writefile(2, msg);
	}

	/**
	 * 输出错误信息
	 * 
	 * @param e
	 * @throws Exception
	 */
	public static void error(Exception e) throws Exception {
		error(getStackTrace(e));
		throw e;
	}

	/**
	 * 输出错误信息 终止错误抛出
	 * 
	 * @param e
	 */
	public static void errorNotThrow(Exception e) {
		error(getStackTrace(e));
	}

	/**
	 * 输出错误信息
	 * 
	 * @param msg 扩展信息
	 * @param e
	 * @throws Exception
	 */
	public static void error(String msg, Exception e) throws Exception {
		String wstr = "";
		if ((msg != null) && (!msg.isEmpty()))
			wstr = msg + " " + Systemdate.getStrDate();
		wstr = wstr + "\r\n" + getExceptionStackTraceMsg(e);
		if (context != null)
			wstr = "【" + context + "】" + wstr;
		error(wstr);
		throw e;
	}

	/**
	 * 获取错误字符串
	 * 
	 * @param e
	 * @return
	 */
	private static String getExceptionStackTraceMsg(Exception e) {
		final Writer result = new StringWriter();
		final PrintWriter printWriter = new PrintWriter(result);
		e.printStackTrace(printWriter);
		return printWriter.toString();
	}

	public static void main(String[] args) {
		// int count = 1000;
		// System.out.println("start");
		// long begin3 = System.currentTimeMillis();
		// for (int i = 0; i < count; i++) {
		// writefile(2, i + "测试java 文件操作\r\n");
		// }
		// long end3 = System.currentTimeMillis();
		// System.out.println("FileWriter执行耗时:" + (end3 - begin3) + " 豪秒");
		//
		// System.out.println("end");
		// String sd1 = "2017-03-01 12:30";
		// String sd2 = "2017-05-1 12:30";
		// System.out.println(Systemdate.getBetweenMonth(Systemdate.getDateByStr(sd1),
		// Systemdate.getDateByStr(sd2)));
		String fn = "D:\\MyWorks2\\zy\\webservice\\tomcat71\\webapps\\dlhr\\";
		File f = new File(fn);
		System.out.println(f.getName());

	}

	/**
	 * 获取文件对象
	 * 
	 * @param cfw
	 * @param fname
	 * @return
	 * @throws IOException
	 */
	private static CFileWriter ckfw(CFileWriter cfw, String fname) throws IOException {
		CFileWriter rst = cfw;
		if (rst == null) {
			rst = new CFileWriter(fname);
		} else if (!fname.equals(rst.getFname())) {
			rst.close();
			rst = new CFileWriter(fname);
		}
		return rst;
	}

	private static void checkfw(int type, String fname) {
		try {
			if (type == 1) {
				logfile = ckfw(logfile, fname);
			} else if (type == 2) {
				errfile = ckfw(errfile, fname);
			} else if (type == 3) {
				dblogfile = ckfw(dblogfile, fname);
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void writefile(int type, String msg) {
		String fsep = System.getProperty("file.separator");
		String fname = null;
		if (type == 1) {
			fname = fsep + "debug";
		}
		if (type == 2) {
			fname = fsep + "error";
		}
		if (type == 3) {
			fname = fsep + "dblog";
		}
		File file = new File(LogPath);
		if (!file.exists() && !file.isDirectory())
			file.mkdirs();
		fname = LogPath + fname + Systemdate.getStrDateYYMMDD() + ".log";
		checkfw(type, fname);
		// System.out.println(fname);
		if (type == 1) {
			logfile.Writeline(msg);
		} else if (type == 2) {
			errfile.Writeline(msg);
		} else if (type == 3) {
			dblogfile.Writeline(msg);
		}
	}

	/**
	 * 获取错误信息
	 * 
	 * @param aThrowable
	 * @return
	 */
	public static String getStackTrace(Throwable aThrowable) {
		if (aThrowable == null) {
			return "null";
		}
		final Writer result = new StringWriter();
		final PrintWriter printWriter = new PrintWriter(result);
		aThrowable.printStackTrace(printWriter);
		return result.toString();
	}

	/**
	 * 输出DEBUG
	 * 
	 * @param value
	 */
	public static void debug(int value) {
		Logsw.debug(String.valueOf(value));
	}
}
