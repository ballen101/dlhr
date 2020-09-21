package com.corsair.server.listener;

import java.io.File;

import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationObserver;

import com.corsair.server.html.CHtmlTemplate;

public class CFileListener implements FileAlterationListener {

	@Override
	public void onDirectoryChange(File file) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDirectoryCreate(File file) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDirectoryDelete(File file) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onFileChange(File file) {
		// TODO Auto-generated method stub
		buildhtml(file);
	}

	@Override
	public void onFileCreate(File file) {
		buildhtml(file);
	}

	private void buildhtml(File file) {
		try {
			String fileName = file.getPath();
			int i = fileName.lastIndexOf(".");
			if (i > -1 && i < fileName.length()) {
				String extention = fileName.substring(i + 1); // --扩展名
				if ((extention == null) || (extention.isEmpty()))
					return;
				if (CHtmlTemplate.isHtmlFile(new File(fileName))) {
					CHtmlTemplate.buildHtml(file.getPath());
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onFileDelete(File file) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStart(FileAlterationObserver file) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStop(FileAlterationObserver file) {
		// TODO Auto-generated method stub

	}

}
