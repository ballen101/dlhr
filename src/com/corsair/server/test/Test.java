package com.corsair.server.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import com.corsair.cjpa.CJPABase;
import com.corsair.cjpa.CJPALineData;
import com.corsair.dbpool.CDBConnection;
import com.corsair.dbpool.CDBPool;
import com.corsair.dbpool.DBPoolParms;
import com.corsair.dbpool.util.Systemdate;
import com.corsair.server.base.ConstsSw;
import com.corsair.server.base.VclZip;
import com.corsair.server.genco.CODevloper;
import com.corsair.server.generic.Shwposition;
import com.corsair.server.generic.Shwrole;
import com.corsair.server.generic.Shwuserparmsconst;
import com.corsair.server.util.Base64;
import com.corsair.server.util.CorUtil;
import com.corsair.server.util.DesSwEx;
import com.corsair.server.util.MatrixUtil;
import com.corsair.server.weixin.WXMsgSend;
import com.corsair.server.weixin.WXUtil;

public class Test {
	private static ReadWriteLock rwl = new ReentrantReadWriteLock();
	private static List<String> ls = new ArrayList<String>();
	private static Lock lock = new ReentrantLock();

	final static char[] digits = {
			'0', '1', '2', '3', '4', '5', '6', '7',
			'8', '9', 'A', 'B', 'C', 'D', 'E', 'F',
			'G', 'H', 'J', 'K', 'L', 'M', 'N', 'P',
			'R', 'S', 'T', 'U', 'W', 'X', 'Y', 'Z' };

	private static String sctstr = "var a=1;b=2;" +
			"function add(a,b){" +
			"return a+b;" +
			"}" +
			"function cf(a,b){" +
			"return a*b" +
			"}";

	private static void f1(boolean a) {
		a = !a;
	}

	private static void f2(Boolean a) {
		a = !a;
	}

	private static boolean hasKey(List<HashMap<String, String>> list, String key) {
		for (HashMap<String, String> item : list) {
			if (item.containsKey(key))
				return true;
		}
		return false;
	}

	public static List<String> testlist = new ArrayList<String>();

	public static void main(String[] args) throws Exception {
		// Date actd = Systemdate.getDateByStr("2015-11-01");
		// Calendar cale = Calendar.getInstance();
		// cale.setTime(actd);
		// cale.set(Calendar.DAY_OF_MONTH,
		// cale.getActualMaximum(Calendar.DAY_OF_MONTH));
		// Date actded = cale.getTime();
		//
		// System.out.println(Systemdate.getStrDateyyyy_mm_dd(actded));
		// System.out.println(CorUtil.isNumeric("123.21"));
		// System.out.println(CorUtil.isNumeric("123"));

		// String mxs = "http://www.icefall.com.cn?fdsafdsafdsa=fdsafdsa";
		// File f = new File("c:\\a.jpg");
		// MatrixUtil.toQrcodeFile(mxs, f, 800, 800, "jpg");
		// int a = 3280340;
		// System.out.println(a);
		// String s = numericToString(a, 16);
		// System.out.println(s);
		// System.out.println(stringToNumeric(s, 16));
		// for (int i = 0; i < 5; i++) {
		// Thread t = newFunctionT1();
		// t.setName("ThreadA" + i);
		// t.start();
		// //System.out.println("Thread start:" + t.getName());
		// }
		//
		// for (int i = 0; i < 5; i++) {
		// Thread t = newFunctionT2();
		// t.setName("ThreadB" + i);
		// t.start();
		// //System.out.println("Thread start:" + t.getName());
		// }

		// for (int i = 0; i < 5; i++) {
		// Thread t = newUpdateT();
		// t.setName("UT" + i);
		// t.start();
		// // System.out.println("Thread start:" + t.getName());
		// }
		//
		// for (int i = 0; i < 100; i++) {
		// Thread t = newReadT();
		// t.setName("RT" + i);
		// t.start();
		// }
		// System.out.println(WXUtil.getWXDatetime());

		// String[] fileName =
		// getFileName("D:\\MyWorks2\\zy\\webservice\\tomcat71\\webapps\\csm\\webapp\\js\\easyui\\themes\\icons\\filetype\\32");
		// for (String name : fileName) {
		// String an = name.substring(0, name.indexOf("."));
		//
		// String s = ".icon-file-" + an + "32 {\n"
		// + "background: url('icons/filetype/32/" + name +
		// "') no-repeat left center;\n"
		// + "}";
		// System.out.println(s);
		// }
		// readf();
		// System.out.println(getShortHex(200) + getShortHex(147) +
		// getShortHex(70) + getShortHex(192) + getShortHex(74) +
		// getShortHex(80));

		// 线程锁测试

		// ThreadCallSourse tcs = new ThreadCallSourse();
		// for (int i = 0; i < 10; i++) {
		// (new Thread(new TestThreadRunable(tcs, i))).start();
		// (new Thread(new TestThreadRunable2(tcs, i))).start();
		// }

		// String msg = null;
		// String rst = dotest1(msg);
		// System.out.println("rst:" + rst);
		// System.out.println("msg:" + msg);

		// TestClass1 c1 = new TestClass1();
		// TestClass1 c2 = new TestClass1();
		// System.out.println(c1.getnewid());
		// System.out.println(c2.getnewid());
		// String pstr = "003";
		// String cstr = "004002";
		// System.out.println(cstr.substring(pstr.length(), cstr.length()));
		// String funcname = "aaa.bb.cc.dd.fffffff";
		// String classname = funcname.substring(0, funcname.lastIndexOf("."));
		// String mname = funcname.substring(funcname.lastIndexOf(".") + 1);

		// System.out.println(classname + "|" + mname);

		// String s1="111222333";
		// System.out.println(s1.substring(0, 12));
		// String[] allfields = { "orgid", "orgname", "code", "superid", "extorgname", "manager", "orgtype", "idpath", "stat", "creator",
		// "createtime", "updator", "updatetime", "attribute1" };
		// System.out.println(strs2str(allfields));
		// System.out.println(rightSubstr("1", 2));

		// Class2 c2 = new Class2();
		// c2.function1();

		// String ss = "http://www.163.com/webapp/aaa.html";
		// System.out.println(ss.substring(0, ss.lastIndexOf("/")+1));

		// Date resdate = new Date();
		// String sdb = Systemdate.getStrDateByFmt(resdate, "yyyy-MM-01");
		// String sde = Systemdate.getStrDateByFmt(Systemdate.dateMonthAdd(resdate, 1), "yyyy-MM-01");
		// System.out.println(sdb + " " + sde);
		// float days = (float) 4 / 8;// 按每天8小时计算
		// System.out.println(1);
		// System.out.println(Float.valueOf("1.0"));

		// CDBPool pool = getpool();
		// CDBConnection con = pool.getCon(null);
		// CJPALineData<Shwuserparmsconst> pcs = new CJPALineData<Shwuserparmsconst>(Shwuserparmsconst.class);
		// pcs.findDataBySQL(con, "select * from shwuserparmsconst", false, false);
		//
		// String sqlstr = "SELECT * FROM `a_init_user1225`";
		// List<HashMap<String, String>> ius = pool.openSql2List(sqlstr);
		// for (HashMap<String, String> iu : ius) {
		// String userid = iu.get("userid").toString();
		// String username = iu.get("username").toString();
		// for (CJPABase jpa : pcs) {
		// Shwuserparmsconst pc = (Shwuserparmsconst) jpa;
		// sqlstr = "INSERT INTO shwuserparms(cstparmid, userid, parmvalue) VALUES ('" + pc.cstparmid.getValue() + "', '" + userid + "', '"
		// + pc.defaultvalue.getValue() + "')";
		// pool.execsql(sqlstr);
		// }
		// }
		// System.out.println("OK");
		// System.out.println(Math.round(Float.valueOf("4.0")));

		// List<String> delfs = new ArrayList<String>();
		// delfs.add("aaaa");
		// delfs.add("bbb");
		// delfs.add("ccc");
		// String[] as = new String[delfs.size()];
		// delfs.toArray(as);
		// for(String s:as){
		// System.out.println(s);
		// }

		// System.out.println(511 >> 8);
		// System.out.println(511 - ((511 >> 8) << 8));

		// int[] as = getLEndLong(23, 2);
		// for (int a : as)
		// System.out.println(Integer.toHexString(a));
		// String synid="20170322224313266732";
		// synid = synid.substring(2, synid.length());
		// System.out.println(synid);
		// Random random = new Random();
		// for (int i = 0; i < 4; i++)
		// System.out.println(random.nextInt(100));

		// JSONObject jo = new JSONObject();
		// jo.put("rst", "OK");
		// System.out.println(jo.toString());
		// String v="412,";
		// System.out.println(v.substring(0, v.length()-1));
		// String code = String.valueOf((int) ((Math.random() * 9 + 1) * 100000));
		// System.out.println(code);

		// for (int i = 1; i <= 31; i++) {
		// String d = "00" + i;
		// d = d.substring(d.length() - 2, d.length());
		// System.out.println(d);
		// }

		// jpatest();

		// long a = 1000 * 60 * 60*24 ;
		// String sd1 = "2017-07-03 12:12:12";
		// String sd2 = "2017-07-03 12:12:12";
		//
		// long rst1 = Systemdate.getDateByStr(sd1).getTime() / a;
		// long rst2 = Systemdate.getDateByStr(sd2).getTime() / a;
		//
		// System.out.println(rst1 + ":" + rst2);

		// String str="1234567890";
		// System.out.println(str.substring(1,str.length()-1));
		// System.out.println(str.substring(1,str.length()));
		// Date t = Systemdate.getDateByStr("20180415121843", "yyyyMMddHHmmss");
		// System.out.println(Systemdate.getStrDate(t));//2018-04-15 12:18:43
		// System.out.println(new Date().getTime());

		// JSONObject j1 = new JSONObject();
		// j1.put("a", "11111111");
		// j1.put("b", "33333333");
		// JSONObject j2 = new JSONObject();
		// j2.put("b", "222222222");
		// j2.putAll(j1);
		// System.out.println(j2.toString());
		// float allothours = 4f;
		// float mu = 3.5f;
		// float fhs = (allothours - allothours % mu);
		// System.out.println(fhs);

		// String md5str ="username=系统管理员&noncestr=lFdNxnt2RAtBO2I0Uqz2l3SvyXJgMsb5&timestr=20180611164735&password=q1w2e3r4";
		// md5str = DesSwEx.MD5(md5str);
		// System.out.println(md5str);

		// System.out.println(Boolean.valueOf("TRUE1"));

		// WXMsgSend.MergeConent(null, "fdsafdsafdsa{{{{parame1}},fdsa{{parame2}},fdsa{{parame4}}");

		// System.out.println("1234567890".subSequence(0, 2));
		// System.out.println(new Date().getTime());
		// System.out.println(ConstsSw.EncryUserPassword("q1w2e3"));
		// System.out.println(ConstsSw.DESryUserPassword("579944DB6F1EAD7C"));
		// String s1 = "1,2,3,4,5,";
		// String s2 = "1,3,4,";
		// System.out.print(s1.substring(0, s2.length()));// 1,2,3,
		System.out.println("hellow word");
	}

	private static void jpatest() throws Exception {
		CDBPool pool = getpool();
		String sqlstr = "select * from atable_test";
		System.out.println(new CODevloper().createJPAClass(pool, "Atable_test", sqlstr));
	}

	// 获取长整形小端数组
	// 负数无效
	public static int[] getLEndLong(long l) {
		List<Integer> lis = new ArrayList<Integer>();
		while (l > 0) {
			long lt = l >> 8;
			int f = (int) (l - (lt << 8));
			lis.add(f);
			l = lt;
		}
		int[] rst = new int[lis.size()];
		for (int i = 0; i < rst.length; i++)
			rst[i] = lis.get(i);
		return rst;
	}

	// 限制位数 超过截取 不足不0 错误
	public static int[] getLEndLong(long l, int bts) {
		int[] les = getLEndLong(l);
		int[] rst = new int[bts];
		int s = les.length;
		int k = 0;
		for (int i = 0; i < bts; i++) {
			rst[i] = (i < s) ? les[i] : 0;
		}
		return rst;
	}

	// Object o = iu.get("position");
	// if (o != null) {
	// String userid = iu.get("userid").toString();
	// String ps = o.toString();
	// if (!ps.isEmpty()) {
	// String[] pps = ps.split(",");
	// for (String pp : pps) {
	// String positionid = getopid(ops, pp);
	// if (positionid == null) {
	// System.out.println(pp + ",找不到岗位ID");
	// }
	// sqlstr = "INSERT INTO shwpositionuser(positionid, userid) VALUES (" + positionid + ", " + userid + ")";
	// pool.execsql(sqlstr);
	// }
	// }
	// }

	private static String getorgid(CDBPool pool, String employee_code) throws Exception {
		String sqlstr = "SELECT orgid FROM `hr_employee` WHERE `employee_code`='" + employee_code + "'";
		List<HashMap<String, String>> oids = pool.openSql2List(sqlstr);
		if (oids.size() <= 0)
			return null;
		return oids.get(0).get("orgid").toString();
	}

	private static String getroid(CJPALineData<Shwrole> ops, String rolename) {
		for (CJPABase jpa : ops) {
			Shwrole op = (Shwrole) jpa;
			if (op.rolename.getValue().equalsIgnoreCase(rolename)) {
				return op.roleid.getValue();
			}
		}
		return null;
	}

	private static String getopid(CJPALineData<Shwposition> ops, String positiondesc) {
		for (CJPABase jpa : ops) {
			Shwposition op = (Shwposition) jpa;
			if (op.positiondesc.getValue().equalsIgnoreCase(positiondesc)) {
				return op.positionid.getValue();
			}
		}
		return null;
	}

	private static CDBPool getpool() {
		DBPoolParms pm = new DBPoolParms();
		pm.dirver = "com.mysql.jdbc.Driver";
		pm.url = "jdbc:mysql://127.0.0.1:13306/dlhr?characterEncoding=utf-8&amp;autoReconnect=true&amp;useSSL=true";
		pm.schema = "dlhr";
		pm.user = "root";
		pm.password = "q1w2e3";
		pm.minsession = 5;
		pm.maxsession = 10;
		pm.isdefault = true;
		CDBPool pool = new CDBPool(pm);
		return pool;
	}

	private static String rightSubstr(String s, int l) {
		return s.substring(s.length() - l, s.length());
	}

	private static String strs2str(String[] fds) {
		String rst = "";
		for (String fd : fds) {
			rst = rst + fd + ",";
		}
		if (!rst.isEmpty())
			rst = rst.substring(0, rst.length() - 1);

		return rst;
	}

	public static String dotest1(String msg) {
		msg = "11111";
		return "22222";
	}

	public static String getShortHex(long value) {
		String s = Long.toHexString(value).toUpperCase();
		if (s.length() < 2)
			s = "0" + s;
		return s;
	}

	public static void readf() {
		try {
			// read file content from file
			StringBuffer sb = new StringBuffer("");

			FileReader reader = new FileReader("d://filetype.txt");
			BufferedReader br = new BufferedReader(reader);
			String str = null;
			while ((str = br.readLine()) != null) {

				int idx = str.indexOf(":");

				System.out.println(str.substring(0, idx));
				System.out.println(str.substring(idx + 1));
			}

			br.close();
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String[] getFileName(String path) {
		File file = new File(path);
		String[] fileName = file.list();
		return fileName;
	}

	private static final void printIp() {

	}

	public static String numericToString(int i, int system) {
		long num = 0;
		if (i < 0) {
			num = ((long) 2 * 0x7fffffff) + i + 2;
		} else {
			num = i;
		}
		char[] buf = new char[32];
		int charPos = 32;
		while ((num / system) > 0) {
			buf[--charPos] = digits[(int) (num % system)];
			num /= system;
		}
		buf[--charPos] = digits[(int) (num % system)];
		return new String(buf, charPos, (32 - charPos));
	}

	public static int stringToNumeric(String s, int system) {
		char[] buf = new char[s.length()];
		s.getChars(0, s.length(), buf, 0);
		long num = 0;
		for (int i = 0; i < buf.length; i++) {
			for (int j = 0; j < digits.length; j++) {
				if (digits[j] == buf[i]) {
					num += j * Math.pow(system, buf.length - i - 1);
					break;
				}
			}
		}
		return (int) num;
	}

	private static Thread newFunctionT1() {
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				Test.funciton1();
			}
		});
		return t;
	}

	private static Thread newFunctionT2() {
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				Test.funciton2();
			}
		});
		return t;
	}

	private static void funciton1() {
		Test.lock.lock();
		try {
			String n = Thread.currentThread().getName();
			System.out.println(n + "进入funciton1");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println(n + "退出funciton1");
		} finally {
			Test.lock.unlock();
		}
	}

	private static void funciton2() {
		Test.lock.lock();
		try {
			String n = Thread.currentThread().getName();
			System.out.println(n + "进入funciton2");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println(n + "退出funciton2");
		} finally {
			Test.lock.unlock();
		}
	}

	// ///////////////////////////////////////
	// ///////////////////////////////////////
	// ///////////////////////////////////////

	// ///////////////////////////////////////
	private static Thread newUpdateT() {
		Thread Tupdate = new Thread(new Runnable() {
			@Override
			public void run() {
				rwl.writeLock().lock();// 取到写锁
				try {
					for (int i = 0; i < 50; i++) {
						try {
							Thread.sleep(5);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						if (!ls.contains(i)) {
							ls.add(String.valueOf(i));
						}
						System.out.println(Thread.currentThread().getName() + "写入");
					}
				} finally {
					rwl.writeLock().unlock();// 释放写锁
				}
			}
		});
		return Tupdate;
	}

	private static Thread newReadT() {
		Thread Tupdate = new Thread(new Runnable() {
			@Override
			public void run() {
				for (int i = 0; i < 50; i++) {
					if (!ls.contains(i)) {
						int idx = ls.indexOf(i);
						if (idx >= 0) {
							String s = ls.get(idx);
							System.out.println(Thread.currentThread().getName() + "读取:" + s + ";sum:" + ls.size());
						} else
							System.out.println(Thread.currentThread().getName() + "读取NULL;sum:" + ls.size());
					} else
						System.out.println(Thread.currentThread().getName() + "读取NULL;sum:" + ls.size());

				}
			}
		});
		return Tupdate;
	}

	public static String read(String fileName, String encoding) {
		StringBuffer fileContent = new StringBuffer();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), encoding));
			String line = null;
			while ((line = br.readLine()) != null) {
				fileContent.append(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (br != null)
				try {
					br.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		return fileContent.toString();
	}
}
