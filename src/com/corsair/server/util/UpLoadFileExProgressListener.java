package com.corsair.server.util;

import org.apache.commons.fileupload.ProgressListener;
import com.corsair.server.base.CSContext;

/**
 * 上传文件进度监听器
 * 
 * @author Administrator
 *
 */
public class UpLoadFileExProgressListener implements ProgressListener {
	private double megaBytes = -1;

	public UpLoadFileExProgressListener() {
	}

	@Override
	public void update(long pBytesRead, long pContentLength, int pItems) {
		double mBytes = pBytesRead / 1024;
		double total = pContentLength / 1024;
		if (megaBytes == mBytes) {
			return;
		}
		megaBytes = mBytes;
		if (pContentLength == -1) {
			// System.out.println("So far, " + pBytesRead +
			// " bytes have been read.");
		} else {
			double read = (mBytes / total);
			// NumberFormat nf = NumberFormat.getPercentInstance();
			CSContext.pushParm("uploadfile", String.valueOf(read));
		}
	}
}
