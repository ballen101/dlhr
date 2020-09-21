package com.corsair.server.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class CFileUtiil {
	protected static byte[] buf = new byte[1024];

	/**
	 * 文件夹压缩。输入路径不能与输出路径相同 zip("c:\\webserver\\test.zip","c:\\test1");
	 * 
	 * @param zipFileName
	 * @param inputFile
	 * @throws Exception
	 */
	public static void zipFold(String zipFileName, String inputFilePath) throws Exception {
		zip(zipFileName, new File(inputFilePath));
	}

	private static void zip(String zipFileName, File inputFile) throws Exception {
		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFileName));
		out.setLevel(5);
		zipFold(out, inputFile, "");
		out.close();
	}

	private static void zipFold(ZipOutputStream out, File f, String base) throws Exception {
		if (f.isDirectory()) {
			File[] fl = f.listFiles();
			base = base.length() == 0 ? "" : base + "/";
			for (int i = 0; i < fl.length; i++) {
				zipFold(out, fl[i], base + fl[i].getName());
			}
		}
		else {
			out.putNextEntry(new ZipEntry(base));
			FileInputStream in = new FileInputStream(f);
			byte[] bf = new byte[1048576];// 1M
			while (true) {
				int b = in.read(bf);
				if (b == -1) {
					in.close();
					break;
				} else {
					out.write(bf, 0, b);
				}
			}
		}
	}

	public static void delete(File file) {
		if (!file.exists())
			return;
		if (file.isFile()) {
			file.delete();
		} else {
			for (File f : file.listFiles()) {
				delete(f);
			}
			file.delete();
		}
	}
}