package com.corsair.server.base;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import com.corsair.dbpool.util.Logsw;

/**
 * 读取文件
 * 
 * @author Administrator
 *
 */
public class FileReadBase {
	public BufferedReader bufread;
	public BufferedWriter bufwriter;
	File writefile;
	String filepath, filecontent, read;
	String readStr = "";

	/**
	 * 从文本文件中读取内容
	 * 
	 * @param path
	 * @return
	 */
	public String readfile(String path) //
	{
		try {
			filepath = path; // 得到文本文件的路径
			File file = new File(filepath);
			FileReader fileread = new FileReader(file);
			bufread = new BufferedReader(fileread);
			while ((read = bufread.readLine()) != null) {
				readStr = readStr + read;
			}
		} catch (Exception d) {
			Logsw.error(d.getMessage());
		}
		return readStr; // 返回从文本文件中读取内容
	}

	//
	/**
	 * 向文本文件中写入内容
	 * 
	 * @param path
	 * @param content
	 * @param append
	 */
	public void writefile(String path, String content, boolean append) {
		try {
			boolean addStr = append; // 通过这个对象来判断是否向文本文件中追加内容
			filepath = path; // 得到文本文件的路径
			filecontent = content; // 需要写入的内容
			writefile = new File(filepath);
			if (writefile.exists() == false) // 如果文本文件不存在则创建它
			{
				writefile.createNewFile();
				writefile = new File(filepath); // 重新实例化
			}
			FileWriter filewriter = new FileWriter(writefile, addStr);
			bufwriter = new BufferedWriter(filewriter);
			filewriter.write(filecontent);
			filewriter.flush();
		} catch (Exception d) {
			System.out.println(d.getMessage());
		}
	}
}
