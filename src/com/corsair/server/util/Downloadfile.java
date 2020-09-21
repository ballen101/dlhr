package com.corsair.server.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.corsair.server.base.ConstsSw;


public class Downloadfile extends HttpServlet {

	/**
	 * 
	 */
//	private static final long serialVersionUID = 1L;
//
//	public Downloadfile() {
//		super();
//	}
//
//	@Override
//	public void doGet(HttpServletRequest request, HttpServletResponse response)
//			throws ServletException, IOException {
//		String result = "";
//		String fname = "";
//		request.setCharacterEncoding("utf-8");
//		String down_type = request.getParameter("downtype");// 下载类型
//		String Path = request.getParameter("Path");// 子文件夹
//		String FileName = request.getParameter("fname");// 子文件下的 路径及文件名
//		String openInbro = request.getParameter("oib");// 直接在浏览器里面打开 yes no
//		try {
//			if ((down_type == null) || (down_type.equals(""))) {
//				result = "参数错误";
//				System.out.println(result);
//				return;
//			}
//			if ((FileName == null) || (FileName.equals(""))) {
//				result = "参数错误";
//				System.out.println(result);
//				return;
//			}
//			String fsep = System.getProperty("file.separator");
//			if (Path == null)
//				Path = "";
//			if (openInbro == null)
//				openInbro = "FALSE";
//			openInbro = openInbro.toUpperCase();
//			String FilePath = "";
//			if (down_type.equals(ConstsSw.DownLoad_ATTI_WS_File)) {
//				FilePath = this.getServletContext().getRealPath("/")
//						+ "attifiles" + Path;
//			}
//			if (down_type.equals(ConstsSw.DownLoad_Common_WS_File)) {
//				FilePath = ConstsSw.geAppParmStr("UDFilePath") + Path;
//			}
//			//System.out.println(ConstsSw.spms_fudpath);
//			//System.out.println(FilePath);
//
//			fname = FilePath + fsep + FileName;
//
//			if (!(new File(fname)).exists()) {
//				result = "文件" + fname + "不存在！";
//				System.out.println(result);
//				return;
//			}
//			// System.out.println(fname);
//			if (openInbro.endsWith("FALSE")) {
//				response.setContentType("application/octet-stream");
//				response.setHeader(
//						"content-disposition",
//						"attachment; filename="
//								+ java.net.URLEncoder.encode(FileName, "UTF-8"));
//			} else if (openInbro.endsWith("TRUE")) {
//				response.setContentType("text/html");
//				response.setHeader(
//						"content-disposition",
//						"attachment; filename="
//								+ java.net.URLEncoder.encode(FileName, "UTF-8"));
//			}
//			OutputStream os = response.getOutputStream();
//			FileInputStream fis = new FileInputStream(fname);
//			byte[] b = new byte[1024];
//			int i = 0;
//			while ((i = fis.read(b)) > 0) {
//				os.write(b, 0, i);
//			}
//			fis.close();
//			os.flush();
//			os.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//		}
//	}
//
//	@Override
//	public String getServletInfo() {
//		return "File Down load Server!";
//	}

}
