package com.corsair.server.util;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.Query;
import javax.management.ReflectionException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.NodeList;

import com.corsair.server.base.ConstsSw;

//容器属性
public class CServletProperty {

	/**
	 * @return
	 *         http://192.168.108.1:8080
	 *         http://192.168.88.1:8080
	 *         http://192.168.0.4:8080
	 *         http://fe80:0:0:0:713b:1b00:b0e:8217%6:8080
	 *         http://fe80:0:0:0:215c:163c:bf24:b790%8:8080
	 *         http://fe80:0:0:0:c9e3:7564:3257:c80f%13:8080
	 *         http://fe80:0:0:0:34ee:ba0:3f57:fffb%9:8080
	 *         http://2001:0:9d38:953c:34ee:ba0:3f57:fffb:8080
	 *         https://192.168.108.1:8443
	 *         https://192.168.88.1:8443
	 *         https://192.168.0.4:8443
	 *         https://fe80:0:0:0:713b:1b00:b0e:8217%6:8443
	 *         https://fe80:0:0:0:215c:163c:bf24:b790%8:8443
	 *         https://fe80:0:0:0:c9e3:7564:3257:c80f%13:8443
	 *         https://fe80:0:0:0:34ee:ba0:3f57:fffb%9:8443
	 *         https://2001:0:9d38:953c:34ee:ba0:3f57:fffb:8443
	 * @throws MalformedObjectNameException
	 * @throws NullPointerException
	 * @throws UnknownHostException
	 * @throws AttributeNotFoundException
	 * @throws InstanceNotFoundException
	 * @throws MBeanException
	 * @throws ReflectionException
	 */
	public static List<String> getEndPoints() throws MalformedObjectNameException,
			NullPointerException, UnknownHostException, AttributeNotFoundException,
			InstanceNotFoundException, MBeanException, ReflectionException {
		MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
		Set<ObjectName> objs = mbs.queryNames(new ObjectName("*:type=Connector,*"), Query.match(Query.attr("protocol"), Query.value("HTTP/1.1")));
		ArrayList<String> endPoints = new ArrayList<String>();
		for (Iterator<ObjectName> i = objs.iterator(); i.hasNext();) {
			ObjectName obj = i.next(); // 下载
			String scheme = mbs.getAttribute(obj, "scheme").toString();
			String port = obj.getKeyProperty("port");
			//System.out.println(scheme + ":" + port);
		}
		return endPoints;
	}

	/**
	 * @param scheme: http  https
	 * @return
	 * @throws Exception
	 */
	public static String getServerPort(String scheme) throws Exception {
		MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
		Set<ObjectName> objs = mbs.queryNames(new ObjectName("*:type=Connector,*"), Query.match(Query.attr("protocol"), Query.value("HTTP/1.1")));
		for (Iterator<ObjectName> i = objs.iterator(); i.hasNext();) {
			ObjectName obj = i.next(); // 下载
			if (scheme.equals(mbs.getAttribute(obj, "scheme").toString()))
				return obj.getKeyProperty("port");
		}
		return null;
	}

	private static String getServletHttp11Port() throws Exception {
		String fsep = System.getProperty("file.separator");
		String tpath = System.getProperty("user.dir");// /D:\MyWorks2\zy\webservice\tomcat71\bin 部分人品不好的电脑有问题
		String svf = tpath.substring(0, tpath.lastIndexOf(fsep)) + fsep + "conf" + fsep + "server.xml";
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		org.w3c.dom.Document document = db.parse(new File(svf));
		org.w3c.dom.Element root = document.getDocumentElement();
		NodeList ns = root.getElementsByTagName("Service");
		if (ns.getLength() > 0) {
			org.w3c.dom.Element n = (org.w3c.dom.Element) ns.item(0);
			ns = n.getElementsByTagName("Connector");
			for (int i = 0; i < ns.getLength(); i++) {
				n = (org.w3c.dom.Element) ns.item(i);
				if ("HTTP/1.1".equalsIgnoreCase(n.getAttribute("protocol"))) {
					return n.getAttribute("port");
				}
			}
		}
		return null;
	}

	private static String getServletHttp11PortEx() throws Exception {
		String fsep = System.getProperty("file.separator");
		String tpath = ConstsSw._service_path;// D:\MyWorks2\zy\webservice\tomcat71\
		String svf = tpath + "conf" + fsep + "server.xml";
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		org.w3c.dom.Document document = db.parse(new File(svf));
		org.w3c.dom.Element root = document.getDocumentElement();
		NodeList ns = root.getElementsByTagName("Service");
		if (ns.getLength() > 0) {
			org.w3c.dom.Element n = (org.w3c.dom.Element) ns.item(0);
			ns = n.getElementsByTagName("Connector");
			for (int i = 0; i < ns.getLength(); i++) {
				n = (org.w3c.dom.Element) ns.item(i);
				if ("HTTP/1.1".equalsIgnoreCase(n.getAttribute("protocol"))) {
					return n.getAttribute("port");
				}
			}
		}
		return null;
	}

	public static String getContextPath() {
		String fsep = System.getProperty("file.separator");
		// D:/MyWorks2/zy/webservice/tomcat71/webapps/dlhr/WEB-INF/classes/
		String path = Thread.currentThread().getContextClassLoader().getResource("").getPath();
		path = path.replace("\\", fsep);
		path = path.replace("/", fsep);
		int idx = path.indexOf("WEB-INF");
		path = path.substring(0, idx - 1);// D:/MyWorks2/zy/webservice/tomcat71/webapps/dlhr
		idx = path.lastIndexOf(fsep);
		path = path.substring(idx + 1, path.length());
		return path;
	}
}
