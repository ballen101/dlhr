package com.corsair.dbpool.util;

import java.io.FileWriter;
import java.io.IOException;

/**
 * 读写文件
 * 
 * @author Administrator
 *
 */
class CFileWriter {
	private String fname;
	private FileWriter fw;

	// private long day;

	/**
	 * @return 获取文件读写器
	 */
	public FileWriter getFw() {
		return fw;
	}

	/**
	 * 写入行
	 * 
	 * @param msg
	 */
	public void Writeline(String msg) {
		if (fw != null) {
			try {
				fw.write(msg + "\r\n");
				fw.flush();// 是否需要定时调动?????????
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 构造文件读写器
	 * 
	 * @param fname
	 * @throws IOException
	 */
	public CFileWriter(String fname) throws IOException {
		this.fname = fname;
		fw = new FileWriter(fname, true);
	}

	/**
	 * 关闭文件
	 */
	public void close() {
		try {
			if (fw != null)
				fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String getFname() {
		return fname;
	}
}