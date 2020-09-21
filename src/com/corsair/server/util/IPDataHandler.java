package com.corsair.server.util;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class IPDataHandler {
	private DataInputStream inputStream = null;
	private long fileLength = -1;
	private int dataLength = -1;
	private Map<String, IPData> cacheMap = null;
	private byte[] allData = null;

	public IPDataHandler(String IP_DATA_PATH) throws Exception {
		File file = new File(IP_DATA_PATH);
		inputStream = new DataInputStream(new FileInputStream(file));
		fileLength = file.length();
		cacheMap = new HashMap<String, IPData>();
		if (fileLength > Integer.MAX_VALUE) {
			throw new Exception("数据文件大于2GB错误");
		}
		dataLength = (int) fileLength;
		allData = new byte[dataLength];
		inputStream.read(allData, 0, dataLength);
		dataLength = (int) getbytesTolong(allData, 0, 4, ByteOrder.BIG_ENDIAN);
	}

	private long getbytesTolong(byte[] bytes, int offerSet, int size, ByteOrder byteOrder) {
		if ((offerSet + size) > bytes.length || size <= 0) {
			return -1;
		}
		byte[] b = new byte[size];
		for (int i = 0; i < b.length; i++) {
			b[i] = bytes[offerSet + i];
		}

		ByteBuffer byteBuffer = ByteBuffer.wrap(b);
		byteBuffer.order(byteOrder);

		long temp = -1;
		if (byteBuffer.hasRemaining()) {
			temp = byteBuffer.getInt();
		}
		return temp;
	}

	private long ip2long(String ip) throws UnknownHostException {
		InetAddress address = InetAddress.getByName(ip);
		byte[] bytes = address.getAddress();
		long reslut = getbytesTolong(bytes, 0, 4, ByteOrder.BIG_ENDIAN);
		return reslut;
	}

	private int getIntByBytes(byte[] b, int offSet) {
		if (b == null || (b.length < (offSet + 3))) {
			return -1;
		}
		byte[] bytes = Arrays.copyOfRange(allData, offSet, offSet + 3);
		byte[] bs = new byte[4];
		bs[3] = 0;
		for (int i = 0; i < 3; i++) {
			bs[i] = bytes[i];
		}

		return (int) getbytesTolong(bs, 0, 4, ByteOrder.LITTLE_ENDIAN);
	}

	public IPData findGeography(String address) throws Exception {
		if (address == null) {
			throw new Exception("IP地址错误");
		}

		if (dataLength < 4 || allData == null) {
			throw new Exception("IP地址错误");
		}

		String ip = "127.0.0.1";
		try {
			ip = Inet4Address.getByName(address).getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		// String[] ipArray = StringUtils.split(ip, ".");
		// String[] ipArray = ip.split(".");
		// String[] ipArray = {"8", "8", "8", "8"};
		String[] ipArray = ip.split("\\.");
		int ipHeadValue = Integer.parseInt(ipArray[0]);
		if (ipArray.length != 4 || ipHeadValue < 0 || ipHeadValue > 255) {
			return new IPData("IP地址错误", null, null);// "illegal ip";
		}

		if (cacheMap.containsKey(ip)) {
			return cacheMap.get(ip);
		}

		long numIp = 1;
		try {
			numIp = ip2long(address);
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}

		int tempOffSet = ipHeadValue * 4 + 4;
		long start = getbytesTolong(allData, tempOffSet, 4, ByteOrder.LITTLE_ENDIAN);
		int max_len = dataLength - 1028;
		long resultOffSet = 0;
		int resultSize = 0;

		for (start = start * 8 + 1024; start < max_len; start += 8) {
			if (getbytesTolong(allData, (int) start + 4, 4, ByteOrder.BIG_ENDIAN) >= numIp) {
				resultOffSet = getIntByBytes(allData, (int) (start + 4 + 4));
				resultSize = (char) allData[(int) start + 7 + 4];
				break;
			}
		}

		if (resultOffSet <= 0) {
			return new IPData("偏移错误", null, null);// "偏移错误";
		}

		byte[] add = Arrays.copyOfRange(allData, (int) (dataLength + resultOffSet - 1024), (int) (dataLength + resultOffSet - 1024 + resultSize));
		try {
			if (add == null) {
				cacheMap.put(ip, new IPData("没查到相应数据", null, null));
			} else {
				cacheMap.put(ip, paraddr(add));
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return cacheMap.get(ip);
	}

	private IPData paraddr(byte[] add) throws UnsupportedEncodingException {
		String s = new String(add, "UTF-8");
		// System.out.println(s);
		String[] ss = s.split("	");
		// System.out.println(ss);
		IPData rst = new IPData();
		if (ss.length == 0)
			rst.setCtry("解析错误");
		if (ss.length == 1) {
			rst.setCtry(ss[0]);
		} else if (ss.length == 2) {
			rst.setCtry(ss[0]);
			rst.setPrvc(ss[1]);
		} else if (ss.length >= 3) {
			rst.setCtry(ss[0]);
			rst.setPrvc(ss[1]);
			rst.setCity(ss[2]);
		}
		return rst;
	}

	public static void main(String[] args) {
		IPDataHandler obj;
		try {
			obj = new IPDataHandler("D:/MyWorks2/zy/webservice/tomcat71/webapps/csm/WEB-INF/conf/17monipdb.dat");
			IPData idt = obj.findGeography("124.133.208.119");
			//System.out.println(idt.getCtry());
			//System.out.println(idt.getPrvc());
			//System.out.println(idt.getCity());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}