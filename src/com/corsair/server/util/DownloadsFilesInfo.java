package com.corsair.server.util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.corsair.server.base.ConstsSw;

public class DownloadsFilesInfo {
//	private String FilePath = "";
//
//	public String getDownLoadsFilesInfoXML(String XMLStr) {
//		FilePath = ConstsSw._root_filepath + "downloads";
//		// FilePath = "C:\\Downloads";
//		return getFilesInfoXML(FilePath);
//	}
//
//	public String getFilesInfoXML(String strPath) {
//		ArrayList<String[]> filelist = new ArrayList<String[]>();
//		String FileInft[] = new String[3];
//		FileInft[0] = "filename";
//		FileInft[1] = "filesize";
//		FileInft[2] = "lastupdate";
//		filelist.add(FileInft);
//		getFilesInfo(strPath, filelist);
//		return generateListXML(filelist);
//	}
//
//	private void getFilesInfo(String strPath, ArrayList<String[]> filelist) {
//		File dir = new File(strPath);
//		File[] files = dir.listFiles();
//
//		if (files == null)
//			return;
//		for (int i = 0; i < files.length; i++) {
//			if (files[i].isDirectory()) {
//				getFilesInfo(files[i].getAbsolutePath(), filelist);
//			} else {
//				String FileInft[] = new String[3];
//				String strFileName = files[i].getAbsolutePath();
//				String strSize = String.valueOf(files[i].length());
//				Date dt = new Date(files[i].lastModified());
//				SimpleDateFormat df = new SimpleDateFormat(
//						"yyyy-MM-dd HH:mm:ss"); // 设置日期格式
//				String lastUpdate = df.format(dt);
//
//				strFileName = strFileName.substring(FilePath.length());
//
//				FileInft[0] = strFileName;
//				FileInft[1] = strSize;
//				FileInft[2] = lastUpdate;
//
//				filelist.add(FileInft);
//			}
//		}
//	}
//
//	public String generateListXML(final ArrayList<String[]> flist) {
//		String result = null;
//		if (flist.isEmpty())
//			return result;
//		Document document = DocumentHelper.createDocument();
//		Element root = document.addElement(ConstsSw.xml_root_corsair);// 创建根节点
//		Element meta_root = root.addElement(ConstsSw.xml_data_metadata);
//		Element data_root = root.addElement(ConstsSw.xml_data_data);
//		Element field;
//		Element data;
//		Element row;
//
//		String fino[] = flist.get(0);
//		for (int i = 0; i < fino.length; i++) {
//			field = meta_root.addElement(ConstsSw.xml_field);
//			data = field.addElement(ConstsSw.xml_field_name);
//			data.setText(fino[i]);
//
//			data = field.addElement(ConstsSw.xml_field_type);
//			data.setText("1");
//
//			data = field.addElement(ConstsSw.xml_field_size);
//			data.setText("1024");
//		}
//
//		int id = 0;
//		for (int i = 1; i < flist.size(); i++) {
//			row = data_root.addElement(ConstsSw.xml_row);
//			row.setText(String.valueOf(++id));
//			fino = flist.get(i);
//			for (int j = 0; j < fino.length; j++) {
//				data = row.addElement(ConstsSw.xml_field_value);
//				data.setText(fino[j]);
//			}
//		}
//		result = document.asXML();
//		return result;
//	}
}
