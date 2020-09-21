package com.corsair.server.baiduueditor;

import java.util.Map;

import org.apache.commons.codec.binary.Base64;

/**
 * BASE64方式上传文件
 * 
 * @author Administrator
 *
 */
public final class Base64Uploader {

	public static State save(String content, Map<String, Object> conf) {

		byte[] data = decode(content);

		long maxSize = ((Long) conf.get("maxSize")).longValue();

		if (!validSize(data, maxSize)) {
			return new BaseState(false, AppInfo.MAX_SIZE);
		}

		String iup = ConfigManager.configManager.getConfigValue("imageManagerUrlPrefix");

		String suffix = FileType.getSuffix("JPG");

		String savePath = PathFormat.parse((String) conf.get("savePath"),
				(String) conf.get("filename"));

		savePath = savePath + suffix;
		String physicalPath = (String) conf.get("rootPath") + savePath;

		State storageState = StorageManager.saveBinaryFile(data, physicalPath);

		if (storageState.isSuccess()) {
			String furl = PathFormat.format(iup + savePath);
			storageState.putInfo("url", furl);
			storageState.putInfo("type", suffix);
			storageState.putInfo("original", "");
		}

		return storageState;
	}

	private static byte[] decode(String content) {
		return Base64.decodeBase64(content);
	}

	private static boolean validSize(byte[] data, long length) {
		return data.length <= length;
	}

}