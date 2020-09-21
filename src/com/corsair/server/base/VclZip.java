package com.corsair.server.base;

import java.io.ByteArrayOutputStream;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterOutputStream;

import com.corsair.server.util.Base64;

/*  */
/**
 * 与delphi函数对应的压缩解压
 * 
 * @author Administrator
 *
 */
public class VclZip {
	public static final int BUFFER = 1024;

	// 压缩 并将压缩结果用 base64编码后输出
	/*
	 * public String zipString1(String src) throws UnsupportedEncodingException
	 * { if (src == null) return null; // System.out.println("ZIP..." + (new
	 * systemdate()).getdate1()); // 创建输入位流 ，并将二进制流输入 buffer流
	 * ByteArrayInputStream in = new ByteArrayInputStream(src.getBytes());
	 * BufferedInputStream inStream = new BufferedInputStream(in); //
	 * 创建输出位流，并与输出Buffer流接口 ByteArrayOutputStream out = new
	 * ByteArrayOutputStream(); BufferedOutputStream outStream = new
	 * BufferedOutputStream(out); // 創建压缩器并初始化参数 开始压缩
	 * SevenZip.Compression.LZMA.Encoder encoder = new
	 * SevenZip.Compression.LZMA.Encoder(); encoder.SetAlgorithm(2); //
	 * Algorithm = 2 encoder.SetDictionarySize(1 << 23);
	 * encoder.SetNumFastBytes(128); // Fb := 128; 16 encoder.SetMatchFinder(1);
	 * encoder.SetLcLpPb(3, 0, 2); // Lc := 3; Lp := 0; Pb := 2;
	 * encoder.SetEndMarkerMode(false); // Eos := false; try {
	 * encoder.WriteCoderProperties(outStream); long fileSize =
	 * src.getBytes().length; for (int i = 0; i < 8; i++) outStream.write((int)
	 * (fileSize >>> (8 * i)) & 0xFF); encoder.Code(inStream, outStream, -1, -1,
	 * null); } catch (IOException e) { e.printStackTrace(); return null; } //
	 * 关闭流// 从输出流中取出字符串 try { outStream.flush(); inStream.close();
	 * outStream.close(); } catch (IOException e) { e.printStackTrace(); return
	 * null; } // 编码 此处不能转换成字符串 byte[] bs = out.toByteArray(); //
	 * System.out.println("ZIPED" + (new systemdate()).getdate1()); //
	 * System.out.println("encodeBase64..." + (new // systemdate()).getdate1());
	 * long t1 = System.currentTimeMillis(); String result = new
	 * String(Base64.encodeBase64(bs)); long t2 = System.currentTimeMillis();
	 * ConstsSw.debug_base64_time = ConstsSw.debug_base64_time + t2 - t1; //
	 * System.out.println("encodeBase64ed" + (new systemdate()).getdate1());
	 * return result; }
	 * // 解压 public String unzipString1(String src) throws Exception {
	 * if (src == null) return null;
	 * // 创建输入流 long t1 = System.currentTimeMillis(); byte srcb[] =
	 * Base64.decodeBase64(src.getBytes()); long t2 =
	 * System.currentTimeMillis(); ConstsSw.debug_base64_time =
	 * ConstsSw.debug_base64_time + t2 - t1; ByteArrayInputStream in = new
	 * ByteArrayInputStream(srcb); BufferedInputStream inStream = new
	 * BufferedInputStream(in); // 创建输出流 ByteArrayOutputStream out = new
	 * ByteArrayOutputStream(); BufferedOutputStream outStream = new
	 * BufferedOutputStream(out); // 对输入流进行解压 int propertiesSize = 5; byte[]
	 * properties = new byte[propertiesSize]; try { if
	 * (inStream.read(properties, 0, propertiesSize) != propertiesSize) throw
	 * new Exception("input .lzma file is too short");
	 * SevenZip.Compression.LZMA.Decoder decoder = new
	 * SevenZip.Compression.LZMA.Decoder(); if
	 * (!decoder.SetDecoderProperties(properties)) throw new
	 * Exception("Incorrect stream properties"); long outSize = 0; for (int i =
	 * 0; i < 8; i++) { int v = inStream.read(); if (v < 0) throw new
	 * Exception("Can't read stream size"); outSize |= ((long) v) << (8 * i); }
	 * if (!decoder.Code(inStream, outStream, outSize)) throw new
	 * Exception("Error in data stream");
	 * } catch (Exception e) { throw new Exception("解压数据错误:" + e.getMessage());
	 * } // 关闭输入输入流 try { inStream.close(); outStream.close(); } catch
	 * (IOException e) { e.printStackTrace(); return null; } // 获取数据 src =
	 * out.toString(); return src; }
	 */
	// 解压
	public static String gunzip(String encdata) throws Exception {
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			InflaterOutputStream zos = new InflaterOutputStream(bos);
			byte[] bts = encdata.getBytes();
			zos.write(Base64.decodeBase64(bts));
			zos.close();
			return new String(bos.toByteArray());
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new Exception("解压数据错误");
		}
	}

	// 压缩
	public static String gzip(String data) {
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			DeflaterOutputStream zos = new DeflaterOutputStream(bos);
			zos.write(data.getBytes());
			zos.close();
			return new String(Base64.encodeBase64(bos.toByteArray()));
		} catch (Exception ex) {
			ex.printStackTrace();
			return "ZIP_ERR";
		}
	}

}
