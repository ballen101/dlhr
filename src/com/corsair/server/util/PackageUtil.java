package com.corsair.server.util;

/**
 * 
 * 关于内部匿名类的BUG 待处理.....
 * 
 * */
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.corsair.dbpool.util.Logsw;
import com.corsair.server.base.ConstsSw;
import com.corsair.server.exception.CException;
import com.corsair.server.listener.SWServletContextListener;
import com.corsair.server.listener.SWSpecClass;
import com.corsair.server.listener.SWSpecClass.SwSpecClassType;
import com.corsair.server.retention.ACO;
import com.corsair.server.retention.ACOAction;
import com.corsair.server.retention.COAcitonItem;
import com.corsair.server.retention.RContextInit;
import com.corsair.server.websocket.AWebsocket;
import com.corsair.server.websocket.CWebSocket;
import com.corsair.server.websocket.CWebsocketClas;

public class PackageUtil {
	@SuppressWarnings("finally")
	public static void initAllCOClassName() {
		List<PackageClass> alltemclasses = new ArrayList<PackageClass>();
		String strpks = ConstsSw.getAppParm("ScanCoPackages").toString();
		String[] pks = strpks.split(";");
		for (String pk : pks) {
			List<PackageClass> clses = getClassName(pk, true);
			alltemclasses.addAll(clses);
		}
		ConstsSw._allCoClassName.clear();
		List<PackageClass> allclasses = new ArrayList<PackageClass>();
		for (int i = 0; i < alltemclasses.size(); i++) {// 重新赋值，去掉重复的
			PackageClass pc = alltemclasses.get(i);
			appendPackageClass(allclasses, pc);
			// System.out.println(pc.getFname() + ":" + pc.getClaname());
		}
		for (int i = 0; i < allclasses.size(); i++) {
			PackageClass pc = allclasses.get(i);
			String cl = pc.getClaname();
			//System.out.println("33333333333333333:" + cl);
			Class<?> c = null;
			try {
				c = Class.forName(cl);
				//System.out.println("0000:" + cl + ":" + c.isAnnotationPresent(ACO.class));
				if ((c != null) && (c.isAnnotationPresent(ACO.class))) {
					//System.out.println("1111:" + cl);
					ACO ce = c.getAnnotation(ACO.class);
					Method ms[] = c.getMethods();
					for (Method m : ms) {
						if (m.isAnnotationPresent(ACOAction.class)) {
							ACOAction coe = m.getAnnotation(ACOAction.class);
							checkMparm(pc, cl, m, ce, coe);
							COAcitonItem ccm = new COAcitonItem(ce.coname() + "." + coe.eventname(), cl, m.getName(), coe);
							ccm.setFname(pc.getFname());
							ConstsSw._allCoClassName.put(ce.coname() + "." + coe.eventname(), ccm);
							// if (ConstsSw.getAppParmBoolean("Debug_Mode")) {
							// System.out.println(ce.coname() + "." + coe.eventname()
							// + ":" + cl + "." + m.getName());
							// }
						}
					}
				}
				if ((c != null) && (c.isAnnotationPresent(RContextInit.class))) {
					if (SWServletContextListener.class.isAssignableFrom(c)) {
						SWSpecClass sc = new SWSpecClass(SwSpecClassType.servetContextInit, c.newInstance());
						ConstsSw._allSpecClass.add(sc);
					} else {
						Logsw.debug("警告：类【" + cl + "】标注为【RContextInit】，但是没有实现【SWServletContextListener】接口");
					}
				}
				if ((c != null) && (c.isAnnotationPresent(AWebsocket.class))) {
					AWebsocket aws = c.getAnnotation(AWebsocket.class);
					if (aws.vport().isEmpty())
						Logsw.debug("警告：类【" + cl + "】标注为【AWebsocket】，vport 不应为空字符串");
					checkWebsocketList(cl, aws.vport());
					CWebsocketClas cwkc = new CWebsocketClas((Class<CWebSocket>) c, aws.vport());
					ConstsSw._allSocketClassName.put(aws.vport(), cwkc);
				}

			} catch (CException e) {
				//System.out.println("222222222222222222");
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				//System.out.println("1111111111111111111");
				System.out.println(e.getLocalizedMessage());
			} finally {
				continue;
			}
		}
	}

	private static void checkMparm(PackageClass pc, String clsname, Method method, ACO ce, ACOAction coe) throws CException {
		Class<?>[] pts = method.getParameterTypes();
		if (pts.length != 0) {
			throw new CException(method.getName() + "注解为COAction 的 function 不能有参数");
		}
		String key = ce.coname() + "." + coe.eventname();
		COAcitonItem ccm = (COAcitonItem) ConstsSw._allCoClassName.get(key);
		if (ccm != null) {
			String errmsg = "文件<" + pc.getFname() + "><" + clsname + "." + method.getName() + "> 与文件<"
					+ ccm.getFname() + "><" + ccm.getClassname() + "." + ccm.getMethodname() + ">";
			throw new CException("CO名称 <" + key + "> 重复! " + errmsg);
		}
		/*
		 * if (coe.postparms()) { if ((pts.length != 1) || (!pts[0].isAssignableFrom(HashMap.class))) { throw new CorsairException(method.getName() +
		 * "注解postparms=true时候，只允许一个参数HashMap<String,String>参数"); } } else { if (pts.length != 0) { throw new CorsairException(method.getName() +
		 * "注解postparms=false时候，不能有参数"); } }
		 */
	}

	private static void checkWebsocketList(String clsname, String vport) throws CException {
		if ((vport == null) || (vport.isEmpty()))
			throw new CException("【" + clsname + "】虚拟端口vport不能为空 ");
		CWebsocketClas cwsc = (CWebsocketClas) ConstsSw._allSocketClassName.get(vport);
		if (cwsc != null) {
			String errmsg = "【" + clsname + "】 与 <" + cwsc.getSocketclass().getName() + "】";
			throw new CException("虚拟端口vport <" + vport + "> 重复! " + errmsg);
		}
	}

	/**
	 * 获取某包下所有类
	 * 
	 * @param packageName
	 *            包名
	 * @param childPackage
	 *            是否遍历子包
	 * @return 类的完整名称
	 */

	private static List<PackageClass> getClassName(String packageName, boolean childPackage) {
		List<PackageClass> fileNames = new ArrayList<PackageClass>();
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		String packagePath = packageName.replace(".", "/").replace("%20", " ");
		try {
			Enumeration<URL> urls = loader.getResources(packagePath);
			while (urls.hasMoreElements()) {
				URL url = urls.nextElement();
				List<PackageClass> cls = getCalssNamesByURL(url, childPackage);
				if (cls != null)
					fileNames.addAll(cls);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		List<PackageClass> jarcls = getClassNameByJars(((URLClassLoader) loader).getURLs(), packagePath, childPackage);
		fileNames.addAll(jarcls);
		return fileNames;
	}

	private static List<PackageClass> getCalssNamesByURL(URL url, boolean childPackage) {
		List<PackageClass> fileNames = null;
		if (url != null) {
			String type = url.getProtocol();
			if (type.equals("file")) {
				fileNames = getClassNameByFile(url.getPath(), null, childPackage);
			} else if (type.equals("jar")) {
				fileNames = getClassNameByJar(url.getPath(), childPackage);
			}
		}
		return fileNames;
	}

	private static void appendPackageClass(List<PackageClass> pcs, PackageClass pc) {
		boolean isexists = false;
		for (PackageClass tpc : pcs) {
			if ((tpc.getFname().equalsIgnoreCase(pc.getFname())) && (tpc.getClaname().equalsIgnoreCase(pc.getClaname()))) {
				isexists = true;
				break;
			}
		}
		// System.out.println("appendPackageClass:" + pc.getClaname() + "--isexists:" + isexists);
		if (!isexists) {
			pcs.add(pc);
		}
	}

	/**
	 * 从项目文件获取某包下所有类
	 * 
	 * @param filePath
	 *            文件路径
	 * @param className
	 *            类名集合
	 * @param childPackage
	 *            是否遍历子包
	 * @return 类的完整名称
	 */
	private static List<PackageClass> getClassNameByFile(String filePath, List<PackageClass> className, boolean childPackage) {
		List<PackageClass> myClassName = new ArrayList<PackageClass>();
	
		File file = new File(filePath.replace("%20", " "));//去掉空格
		File[] childFiles = file.listFiles();
		for (File childFile : childFiles) {
			if (childFile.isDirectory()) {
				if (childPackage) {
					myClassName.addAll(getClassNameByFile(childFile.getPath(), myClassName, childPackage));
				}
			} else {
				String childFilePath = childFile.getPath();
				if (childFilePath.endsWith(".class")) {
					childFilePath = childFilePath.substring(childFilePath.indexOf("\\classes") + 9, childFilePath.lastIndexOf("."));
					childFilePath = childFilePath.replace("\\", ".");
					PackageClass pc = new PackageClass(filePath, childFilePath);
					// System.out.println("childFilePath:" + childFilePath);
					appendPackageClass(myClassName, pc);
				}
			}
		}

		return myClassName;
	}

	/**
	 * 从jar获取某包下所有类
	 * 
	 * @param jarPath
	 *            jar文件路径
	 * @param childPackage
	 *            是否遍历子包
	 * @return 类的完整名称
	 */
	private static List<PackageClass> getClassNameByJar(String jarPath, boolean childPackage) {
		List<PackageClass> myClassName = new ArrayList<PackageClass>();
		String[] jarInfo = jarPath.split("!");
		String jarFilePath = jarInfo[0].substring(jarInfo[0].indexOf("/"));
		String packagePath = jarInfo[1].substring(1);
		try {
			JarFile jarFile = new JarFile(jarFilePath);
			Enumeration<JarEntry> entrys = jarFile.entries();
			while (entrys.hasMoreElements()) {
				JarEntry jarEntry = entrys.nextElement();
				String entryName = jarEntry.getName();
				if (entryName.endsWith(".class")) {
					if (childPackage) {
						if (entryName.startsWith(packagePath)) {
							entryName = entryName.replace("/", ".").substring(0, entryName.lastIndexOf("."));
							// System.out.println("entryName1:" + entryName);
							PackageClass pc = new PackageClass(jarFilePath, entryName);
							appendPackageClass(myClassName, pc);
						}
					} else {
						int index = entryName.lastIndexOf("/");
						String myPackagePath;
						if (index != -1) {
							myPackagePath = entryName.substring(0, index);
						} else {
							myPackagePath = entryName;
						}
						if (myPackagePath.equals(packagePath)) {
							entryName = entryName.replace("/", ".").substring(0, entryName.lastIndexOf("."));
							// System.out.println("entryName2:" + entryName);
							PackageClass pc = new PackageClass(jarFilePath, entryName);
							appendPackageClass(myClassName, pc);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return myClassName;
	}

	/**
	 * 从所有jar中搜索该包，并获取该包下所有类
	 * 
	 * @param urls
	 *            URL集合
	 * @param packagePath
	 *            包路径
	 * @param childPackage
	 *            是否遍历子包
	 * @return 类的完整名称
	 */
	private static List<PackageClass> getClassNameByJars(URL[] urls, String packagePath, boolean childPackage) {
		List<PackageClass> myClassName = new ArrayList<PackageClass>();
		if (urls != null) {
			for (int i = 0; i < urls.length; i++) {
				URL url = urls[i];
				String urlPath = url.getPath().replace("%20", " ");
				// 不必搜索classes文件夹
				if (urlPath.endsWith("classes/")) {
					continue;
				}
				String jarPath = urlPath + "!/" + packagePath;
				myClassName.addAll(getClassNameByJar(jarPath, childPackage));
			}
		}
		return myClassName;
	}
}
