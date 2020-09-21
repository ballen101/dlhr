package com.corsair.server.html;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.io.monitor.FileAlterationObserver;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.corsair.dbpool.util.Logsw;
import com.corsair.server.base.ConstsSw;
import com.corsair.server.listener.CFileListener;

public class CHtmlTemplate {
	private static int treeid = 0;

	// ///获取文件列表
	public static List<String> getSrcList(String path) throws Exception {
		List<String> rst = new ArrayList<String>();
		File root = new File(path);
		if (root.isFile())
			rst.add(root.getAbsolutePath());
		else
			findallfiles(root, rst);
		return rst;
	}

	public static void findallfiles(File dir, List<String> files) throws Exception {
		File[] fs = dir.listFiles();
		for (int i = 0; i < fs.length; i++) {
			File file = fs[i];
			if (file.isDirectory()) {
				findallfiles(file, files);
			} else {
				if (isHtmlFile(file))
					files.add(file.getAbsolutePath());
			}
		}
	}

	// /获取文件树
	public static String getSrc2JsonTree() throws Exception {
		treeid = 0;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		JsonFactory jf = new JsonFactory();
		JsonGenerator jg = jf.createJsonGenerator(baos);
		ArrayList<String> pahts = (ArrayList<String>) ConstsSw.getAppParm("HtmlTempSrc");

		JSONArray rst = new JSONArray();
		for (String path : pahts) {
			File file = new File(path);
			if (file.isDirectory()) {
				JSONObject jo = new JSONObject();
				jo.put("id", ++treeid);
				jo.put("text", file.getName());
				jo.put("path", file.getAbsolutePath());
				jo.put("type", "dir");
				jo.put("state", "closed");
				JSONArray children = new JSONArray();
				findallfiles2jsontree(file, children);
				jo.put("children", children);
				rst.add(jo);
			} else {
				if (isHtmlFile(file)) {
					JSONObject jo = new JSONObject();
					jo.put("id", ++treeid);
					jo.put("type", "file");
					jo.put("text", file.getName());
					jo.put("path", file.getAbsolutePath());
					rst.add(jo);
				}
			}
		}
		return rst.toString();
	}

	public static void findallfiles2jsontree(File dir, JSONArray rst) throws Exception {
		File[] fs = dir.listFiles();
		for (int i = 0; i < fs.length; i++) {
			File file = fs[i];
			if (file.isDirectory()) {
				JSONObject jo = new JSONObject();
				jo.put("id", ++treeid);
				jo.put("text", file.getName());
				jo.put("path", file.getAbsolutePath());
				jo.put("type", "dir");
				jo.put("state", "closed");
				JSONArray children = new JSONArray();
				findallfiles2jsontree(file, children);
				jo.put("children", children);
				rst.add(jo);
			} else {
				if (isHtmlFile(file)) {
					JSONObject jo = new JSONObject();
					jo.put("id", ++treeid);
					jo.put("type", "file");
					jo.put("text", file.getName());
					jo.put("path", file.getAbsolutePath());
					rst.add(jo);
				}
			}
		}
	}

	// 处理模板

	private static String gettemplatefname(String srcfname, String tempfname) throws Exception {
		String ftempfname = tempfname;
		if ((ftempfname == null) || (ftempfname.isEmpty())) {
			return null;
			// throw new Exception("文件【" + srcfname + "】没有设置模板文件！");
		}
		String fsep = System.getProperty("file.separator");
		ftempfname = ftempfname.replace("\\", fsep);
		ftempfname = ftempfname.replace("/", fsep);
		if (ftempfname.substring(0, 1).equals(fsep))
			ftempfname = ftempfname.substring(1);
		return ConstsSw._root_filepath + ftempfname;
	}

	private static String getOutFileName(String srcfname, String simplesrcfname, String wkname) throws Exception {
		String fwkname = wkname;
		if ((fwkname == null) || (fwkname.isEmpty())) {
			throw new Exception("文件【" + srcfname + "】没有设置输出路径！");
		}
		String fsep = System.getProperty("file.separator");
		fwkname = fwkname.replace("\\", fsep);
		fwkname = fwkname.replace("/", fsep);

		if (fwkname.substring(0, 1).equals(fsep))
			fwkname = fwkname.substring(1);
		if (!fwkname.substring(fwkname.length() - 1).equals(fsep))
			fwkname = fwkname + fsep;

		String dirname = ConstsSw._root_filepath + fwkname;
		File file = new File(dirname);
		if (!file.exists() && !file.isDirectory()) {
			file.mkdir();
		}
		fwkname = dirname + simplesrcfname;

		if (fwkname.equalsIgnoreCase(srcfname)) {
			throw new Exception("文件【" + srcfname + "】有相同的输出文件路径！");
		}
		return fwkname;
	}

	public static void buildHtml(String srcfname) throws Exception {
		// 文件分隔符替换
		// System.out.println("11111111111111:" + srcfname);
		File srcf = new File(srcfname);
		Document srcDoc = Jsoup.parse(srcf, "UTF-8");
		Element srcbody = srcDoc.getElementsByTag("body").get(0);
		Element srchtml = srcDoc.getElementsByTag("html").get(0);
		String tpfname = gettemplatefname(srcfname, srchtml.attr("template"));// 模板文件
		String outfname = getOutFileName(srcfname, srcf.getName(), srchtml.attr("workpath"));// 输出文件名
		if (tpfname != null) {
			File tmpFile = new File(tpfname);
			if (!tmpFile.exists() || !tmpFile.isFile()) {
				throw new Exception("模板文件【" + tpfname + "】不存在，或不是文件");
			}

			Document tmpDoc = Jsoup.parse(tmpFile, "UTF-8");
			Element divNode = tmpDoc.getElementById("main_form_div_id");
			if (divNode != null) {
				Element et = srcbody.getElementById("maindata_id");
				if (et == null) {
					throw new Exception("HTML源文件【" + srcf.getName() + "】中ID为【maindata_id】的DOM不存在");
				}
				divNode.html(et.toString());
			}

			srcbody.getElementById("maindata_id").remove();

			srcbody.append(tmpDoc.getElementsByTag("body").get(0).html());

			// srcbody.html(.html());

			// System.out.println(newhtml);
		} else {
			//
		}
		String newhtml = srcDoc.html();
		try {
			OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(outfname), "UTF-8");
			out.write(newhtml);
			out.flush();
			out.close();
		} catch (Exception e) {
			throw new Exception("文件【" + outfname + "】写入错误！");
		}
		CHtmlTemplateJS.testHtmlInit(outfname);
	}

	// 其它函数
	public static boolean isHtmlFile(File file) {
		String extf = getFileExtension(file);
		if (extf == null)
			return false;
		return (extf.equalsIgnoreCase("html") || extf.equalsIgnoreCase("htm") || extf.equalsIgnoreCase("xhtml") || extf.equalsIgnoreCase("jsp")
				|| extf.equalsIgnoreCase("asp") || extf.equalsIgnoreCase("php"));
	}

	private static String getFileExtension(File file) {
		String fileName = file.getName();
		if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0) {
			return fileName.substring(fileName.lastIndexOf(".") + 1);
		} else {
			return null;
		}
	}

}
