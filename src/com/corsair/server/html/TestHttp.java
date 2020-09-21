package com.corsair.server.html;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.Scriptable;

import com.corsair.server.base.ConstsSw;
import com.corsair.server.htmlex.CHtmlTemplatEx;
import com.corsair.server.util.HttpTookit;

public class TestHttp {

	/**
	 * @param args
	 * @throws Exception
	 * @throws ParserException
	 */
	public static void main(String[] args) throws Exception {
		
		String fname = "D:\\MyWorks2\\zy\\webservice\\tomcat71\\webapps\\dlhr\\webapp\\templet\\src\\hr_perm\\myh\\hr_employee_induction.html";
		CHtmlTemplatEx.buildHtml(fname);
		
		// System.out.println("11111111");
		// HttpTookit ht = new HttpTookit();
		// Map<String, String> params = new HashMap<String, String>();
		// System.out.println("222222");
		// String rst = ht.doGet("http://192.168.1.101:8080/dlhr/web/dict/getdictvalues.co?dicid=1014", null);
		// System.out.println(rst);
	}

	public static String readFile(String fname) throws IOException {
		File f = new File(fname);
		return FileUtils.readFileToString(f);
	}

	public static void message(String msg) {
		System.out.println(msg);
	}

	// List<String> rst = new ArrayList<String>();
	// String bootpath =
	// "D:\\MyWorks2\\zy\\webservice\\tomcat71\\webapps\\csm\\webapp\\templet\\src\\";
	// File root = new File(bootpath);
	// CHtmlTemplate.findallfiles(root, rst);
	// for (String s : rst) {
	// System.out.println(s);
	// }
	//
	// ByteArrayOutputStream baos = new ByteArrayOutputStream();
	// JsonFactory jf = new JsonFactory();
	// JsonGenerator jg = jf.createJsonGenerator(baos);
	// bootpath =
	// "D:\\MyWorks2\\zy\\webservice\\tomcat71\\webapps\\csm\\webapp\\templet\\src\\";
	// root = new File(bootpath);
	// jg.writeStartArray();
	// CHtmlTemplate.findallfiles2jsontree(root, jg);
	// jg.writeEndArray();
	// jg.close();
	// System.out.println(baos.toString("utf-8"));

}
