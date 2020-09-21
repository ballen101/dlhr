package com.corsair.server.eai;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.corsair.dbpool.util.Logsw;

public class PropertiesHelper {
	private File file = null;

	public PropertiesHelper(String fname) {
		if (fname != null && fname.length() > 0) {
			try {
				file = new File(fname);
				if (!file.exists()) {
					try {
						file.createNewFile();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						Logsw.error(e.getMessage());
					}
				}

			} catch (Exception e) {
				Logsw.error(e.getMessage());
			}
		}
	}

	public String getProperties(String key) {
		InputStream fis = null;
		try {
			Properties prop = new Properties();
			fis = new FileInputStream(getAbsolutePath());

			prop.load(fis);

			return prop.getProperty(key);

		} catch (Exception e) {
			Logsw.error(e.getMessage());
		} finally {
			try {
				if (fis != null) {
					fis.close();
				}
			} catch (Exception e) {
				Logsw.error(e.getMessage());
			}
		}
		return "";
	}

	public void setProperties(String key, String value) {
		Properties prop = new Properties();

		FileOutputStream outputFile = null;
		InputStream fis = null;
		try {
			// 输入流和输出流要分开处理， 放一起会造成写入时覆盖以前的属性
			fis = new FileInputStream(getAbsolutePath());
			// 先载入已经有的属性文件
			prop.load(fis);

			// 追加新的属性
			prop.setProperty(key, value);

			// 写入属性
			outputFile = new FileOutputStream(getAbsolutePath());
			prop.store(outputFile, "");

			outputFile.flush();
		} catch (Exception e) {
			Logsw.error(e.getMessage());
		} finally {
			try {
				if (fis != null) {
					fis.close();
				}
			} catch (Exception e) {
				Logsw.error(e.getMessage());
			}
			try {
				if (outputFile != null) {
					outputFile.close();
				}
			} catch (Exception e) {
				Logsw.error(e.getMessage());
			}
		}
	}

	public String getAbsolutePath() {
		try {
			return file.getAbsolutePath();
		} catch (Exception e) {
			Logsw.error(e.getMessage());
		}
		return "";
	}
}
