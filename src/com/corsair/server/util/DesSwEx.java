package com.corsair.server.util;

import java.io.IOException;
import java.security.MessageDigest;

public class DesSwEx {

	public static void main(String[] args) throws Exception {
		// System.err.println(EncryStrHex("q1w2e3", "Shangwen_!@#"));
		String str = "appid=wxd930ea5d5a258f4f&body=test&device_info=1000&mch_id=10000100&nonce_str=ibuaiVcKdpRxkhJA&key=192006250b4c09247ec02edce69f6a2d";
		System.out.println(MD5(str));

	}

	// type
	// TKeyByte = array[0..5] of Byte;
	int[] TKeyByte;
	// subKey: array[0..15] of TKeyByte;
	static int[][] subKey = new int[16][6];

	public enum TDesMode {
		dmEncry, dmDESry;
	}

	final static int[] BitIP = { 57, 49, 41, 33, 25, 17, 9, 1, 59, 51, 43, 35, 27, 19, 11, 3, 61, 53, 45, 37, 29, 21, 13, 5, 63, 55, 47, 39, 31, 23, 15, 7, 56,
			48, 40, 32, 24, 16, 8, 0, 58, 50, 42, 34, 26, 18, 10, 2, 60, 52, 44, 36, 28, 20, 12, 4, 62, 54, 46, 38, 30, 22, 14, 6 };

	final static int[] BitCP = { 39, 7, 47, 15, 55, 23, 63, 31, 38, 6, 46, 14, 54, 22, 62, 30, 37, 5, 45, 13, 53, 21, 61, 29, 36, 4, 44, 12, 52, 20, 60, 28,
			35, 3, 43, 11, 51, 19, 59, 27, 34, 2, 42, 10, 50, 18, 58, 26, 33, 1, 41, 9, 49, 17, 57, 25, 32, 0, 40, 8, 48, 16, 56, 24 };

	final static int[] BitExp = { 31, 0, 1, 2, 3, 4, 3, 4, 5, 6, 7, 8, 7, 8, 9, 10, 11, 12, 11, 12, 13, 14, 15, 16, 15, 16, 17, 18, 19, 20, 19, 20, 21, 22, 23,
			24, 23, 24, 25, 26, 27, 28, 27, 28, 29, 30, 31, 0 };

	final static int[] BitPM = { 15, 6, 19, 20, 28, 11, 27, 16, 0, 14, 22, 25, 4, 17, 30, 9, 1, 7, 23, 13, 31, 26, 2, 8, 18, 12, 29, 5, 21, 10, 3, 24 };

	final static int[][] sBox = {
			{ 14, 4, 13, 1, 2, 15, 11, 8, 3, 10, 6, 12, 5, 9, 0, 7, 0, 15, 7, 4, 14, 2, 13, 1, 10, 6, 12, 11, 9, 5, 3, 8, 4, 1, 14, 8, 13, 6, 2, 11, 15, 12, 9,
					7, 3, 10, 5, 0, 15, 12, 8, 2, 4, 9, 1, 7, 5, 11, 3, 14, 10, 0, 6, 13 },

			{ 15, 1, 8, 14, 6, 11, 3, 4, 9, 7, 2, 13, 12, 0, 5, 10, 3, 13, 4, 7, 15, 2, 8, 14, 12, 0, 1, 10, 6, 9, 11, 5, 0, 14, 7, 11, 10, 4, 13, 1, 5, 8, 12,
					6, 9, 3, 2, 15, 13, 8, 10, 1, 3, 15, 4, 2, 11, 6, 7, 12, 0, 5, 14, 9 },

			{ 10, 0, 9, 14, 6, 3, 15, 5, 1, 13, 12, 7, 11, 4, 2, 8, 13, 7, 0, 9, 3, 4, 6, 10, 2, 8, 5, 14, 12, 11, 15, 1, 13, 6, 4, 9, 8, 15, 3, 0, 11, 1, 2,
					12, 5, 10, 14, 7, 1, 10, 13, 0, 6, 9, 8, 7, 4, 15, 14, 3, 11, 5, 2, 12 },

			{ 7, 13, 14, 3, 0, 6, 9, 10, 1, 2, 8, 5, 11, 12, 4, 15, 13, 8, 11, 5, 6, 15, 0, 3, 4, 7, 2, 12, 1, 10, 14, 9, 10, 6, 9, 0, 12, 11, 7, 13, 15, 1, 3,
					14, 5, 2, 8, 4, 3, 15, 0, 6, 10, 1, 13, 8, 9, 4, 5, 11, 12, 7, 2, 14 },

			{ 2, 12, 4, 1, 7, 10, 11, 6, 8, 5, 3, 15, 13, 0, 14, 9, 14, 11, 2, 12, 4, 7, 13, 1, 5, 0, 15, 10, 3, 9, 8, 6, 4, 2, 1, 11, 10, 13, 7, 8, 15, 9, 12,
					5, 6, 3, 0, 14, 11, 8, 12, 7, 1, 14, 2, 13, 6, 15, 0, 9, 10, 4, 5, 3 },

			{ 12, 1, 10, 15, 9, 2, 6, 8, 0, 13, 3, 4, 14, 7, 5, 11, 10, 15, 4, 2, 7, 12, 9, 5, 6, 1, 13, 14, 0, 11, 3, 8, 9, 14, 15, 5, 2, 8, 12, 3, 7, 0, 4,
					10, 1, 13, 11, 6, 4, 3, 2, 12, 9, 5, 15, 10, 11, 14, 1, 7, 6, 0, 8, 13 },

			{ 4, 11, 2, 14, 15, 0, 8, 13, 3, 12, 9, 7, 5, 10, 6, 1, 13, 0, 11, 7, 4, 9, 1, 10, 14, 3, 5, 12, 2, 15, 8, 6, 1, 4, 11, 13, 12, 3, 7, 14, 10, 15,
					6, 8, 0, 5, 9, 2, 6, 11, 13, 8, 1, 4, 10, 7, 9, 5, 0, 15, 14, 2, 3, 12 },

			{ 13, 2, 8, 4, 6, 15, 11, 1, 10, 9, 3, 14, 5, 0, 12, 7, 1, 15, 13, 8, 10, 3, 7, 4, 12, 5, 6, 11, 0, 14, 9, 2, 7, 11, 4, 1, 9, 12, 14, 2, 0, 6, 10,
					13, 15, 3, 5, 8, 2, 1, 14, 7, 4, 10, 8, 13, 15, 12, 9, 0, 3, 5, 6, 11 } };

	final static int[] BitPMC1 = { 56, 48, 40, 32, 24, 16, 8, 0, 57, 49, 41, 33, 25, 17, 9, 1, 58, 50, 42, 34, 26, 18, 10, 2, 59, 51, 43, 35, 62, 54, 46, 38,
			30, 22, 14, 6, 61, 53, 45, 37, 29, 21, 13, 5, 60, 52, 44, 36, 28, 20, 12, 4, 27, 19, 11, 3 };

	final static int[] BitPMC2 = { 13, 16, 10, 23, 0, 4, 2, 27, 14, 5, 20, 9, 22, 18, 11, 3, 25, 7, 15, 6, 26, 19, 12, 1, 40, 51, 30, 36, 46, 54, 29, 39, 50,
			44, 32, 47, 43, 48, 38, 55, 33, 52, 45, 41, 49, 35, 28, 31 };

	// ////////////////DES加密算法////////////////////////////
	// 加密
	public static String EncryStr(String str, String key) throws Exception {
		int StrByte[] = new int[8];
		int OutByte[] = new int[8];
		int KeyByte[] = new int[8];
		int[] strInt = str2gbkarr(str);

		String StrResult;

		while (key.getBytes().length < 8)
			key = key + (char) 0;

		while (strInt.length % 8 != 0)
			strInt[strInt.length] = 0;

		for (int j = 0; j < 8; j++) {
			KeyByte[j] = key.charAt(j); // key[j + 1]
		}

		makeKey(KeyByte, subKey);

		StrResult = "";
		for (int i = 0; i < strInt.length / 8; i++) {
			for (int j = 0; j < 8; j++) {
				StrByte[j] = strInt[i * 8 + j];
			}
			desData(TDesMode.dmEncry, StrByte, OutByte);
			for (int j = 0; j < 8; j++) {
				StrResult = StrResult + (char) (OutByte[j]);
			}
		}
		return StrResult;
	}

	// 以十六进制加密(有些时侯加密的密文含有特殊字符文本框不能显示、数据库文本字段也不能存储。以十六进制形式表示可保证绝对不会出现该种情况)

	public static String EncryStrHex(String Str, String key) throws Exception {
		String StrResult, TempResult, Temp;
		TempResult = EncryStr(Str, key);
		StrResult = "";
		for (int i = 0; i < TempResult.length(); i++) {
			Temp = Integer.toHexString(TempResult.charAt(i));
			if (Temp.getBytes().length == 1)
				Temp = "0" + Temp;
			StrResult = StrResult + Temp;
		}
		return StrResult.toUpperCase();
	}

	// 解密
	public static String DESryStr(int[] intstr, String key) throws IOException {
		int StrByte[] = new int[8];
		int OutByte[] = new int[8];
		int KeyByte[] = new int[8];
		byte[] AllOutByte = new byte[intstr.length];

		while (key.getBytes().length < 8)
			key = key + (char) 0;
		for (int j = 0; j < 8; j++)
			KeyByte[j] = key.charAt(j);

		makeKey(KeyByte, subKey);

		// String s=new String(, "ASCII");

		for (int i = 0; i < (intstr.length / 8); i++) {
			for (int j = 0; j < 8; j++) {
				StrByte[j] = intstr[i * 8 + j];
			}
			desData(TDesMode.dmDESry, StrByte, OutByte);
			for (int j = 0; j < 8; j++)
				AllOutByte[i * 8 + j] = (byte) OutByte[j];
		}

		String StrResult = new String(AllOutByte, "gb2312");

		while ((StrResult.length() > 0) && ((StrResult.charAt(StrResult.length() - 1)) == 0))
			StrResult = StrResult.substring(0, StrResult.length() - 1);// 删除最后一个字符
		return StrResult;
	}

	// 解密十六进制加密的密文
	public static String DESryStrHex(String StrHex, String key) throws IOException {
		String Temp;
		int[] intstr = new int[StrHex.length() / 2];
		for (int i = 0; i < StrHex.length() / 2; i++) {
			Temp = StrHex.substring(i * 2, i * 2 + 2);
			intstr[i] = java.lang.Integer.parseInt(Temp, 16);
			// str = str + (char)java.lang.Integer.parseInt(Temp, 16);
		}
		return DESryStr(intstr, key);
	}

	private static int[] str2gbkarr(String str) throws Exception {
		if (str == null) {
			return null;
		}

		if ((str.getBytes().length > 0) && ((byte) (str.charAt(str.length() - 1)) == 0)) {
			throw new Exception("Error: the last char is NULL char.");
		}

		while (str.getBytes().length % 8 != 0)
			str = str + (char) 0;

		byte[] strByte = str.getBytes();
		int[] strInt = new int[strByte.length];
		for (int i = 0; i < strByte.length; i++) {
			if (strByte[i] < 0)
				strInt[i] = 256 + strByte[i];
			else
				strInt[i] = strByte[i];
		}
		return strInt;
	}

	private static void initPermutation(int[] inData) {
		int newData[] = new int[8];
		for (int i = 0; i < 64; i++) {
			if ((inData[BitIP[i] >> 3] & (1 << (7 - (BitIP[i] & 7)))) != 0)
				newData[i >> 3] = (newData[i >> 3] | (1 << (7 - (i & 7))));
		}
		for (int i = 0; i < 8; i++)
			inData[i] = newData[i];
	}

	private static void conversePermutation(int[] inData) {
		int newData[] = new int[8];
		for (int i = 0; i < 64; i++)
			if ((inData[BitCP[i] >> 3] & (1 << (7 - (BitCP[i] & 7)))) != 0) {
				newData[i >> 3] = (byte) (newData[i >> 3] | (1 << (7 - (i & 7))));
				if (newData[i >> 3] < 0)
					newData[i >> 3] = newData[i >> 3] + 256;

			}
		for (int i = 0; i < 8; i++)
			inData[i] = newData[i];
	}

	private static void expand(int[] inData, int[] outData) {
		for (int i = 0; i < outData.length; i++)
			outData[i] = 0;
		for (int i = 0; i < 48; i++)
			if ((inData[BitExp[i] >> 3] & (1 << (7 - (BitExp[i] & 7)))) != 0) {
				outData[i >> 3] = (byte) (outData[i >> 3] | (1 << (7 - (i & 7))));
				if (outData[i >> 3] < 0)
					outData[i >> 3] = outData[i >> 3] + 256;
			}
	}

	private static void permutation(int[] inData) {
		int newData[] = new int[4];
		for (int i = 0; i < 32; i++)
			if ((inData[BitPM[i] >> 3] & (1 << (7 - (BitPM[i] & 7)))) != 0) {
				newData[i >> 3] = (newData[i >> 3] | (1 << (7 - (i & 7))));
				if (newData[i >> 3] < 0)
					newData[i >> 3] = newData[i >> 3] + 256;
			}
		for (int i = 0; i < 4; i++)
			inData[i] = newData[i];
	}

	private static int si(int s, int inByte) {
		int c = ((inByte & 32) | ((inByte & 30) >> 1) | ((inByte & 1) << 4));
		return (sBox[s][c] & 15);// 0F
	}

	private static void permutationChoose1(int[] inData, int[] outData) {
		for (int i = 0; i < 7; i++)
			outData[i] = 0;
		for (int i = 0; i < 56; i++)
			if ((inData[BitPMC1[i] >> 3] & (1 << (7 - (BitPMC1[i] & 7)))) != 0)
				outData[i >> 3] = (outData[i >> 3] | (1 << (7 - (i & 7))));
		/*
		 * for i := 0 to 55 do if (inData[BitPMC1[i] shr 3] and (1 shl (7 -
		 * (BitPMC1[i] and $07)))) <> 0 then outData[i shr 3] := outData[i shr
		 * 3] or (1 shl (7 - (i and $07)));
		 */
	}

	private static void permutationChoose2(int[] inData, int[] outData) {
		for (int i = 0; i < outData.length; i++)
			outData[i] = 0;
		for (int i = 0; i < 48; i++)
			if ((inData[BitPMC2[i] >> 3] & (1 << (7 - (BitPMC2[i] & 7)))) != 0) {
				outData[i >> 3] = (byte) (outData[i >> 3] | (1 << (7 - (i & 7))));
				if (outData[i >> 3] < 0)
					outData[i >> 3] = outData[i >> 3] + 256;
			}
	}

	private static void cycleMove(int[] inData, int bitMove) {
		for (int i = 0; i < bitMove; i++) {
			inData[0] = ((inData[0] << 1) | (inData[1] >> 7));
			inData[1] = ((inData[1] << 1) | (inData[2] >> 7));
			inData[2] = ((inData[2] << 1) | (inData[3] >> 7));
			inData[3] = ((inData[3] << 1) | ((inData[0] & 16) >> 4));
			inData[0] = (inData[0] & 15);
		}
	}

	private static void makeKey(int[] inKey, int[][] outKey) {
		Byte[] bitDisplace = { 1, 1, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 1 };
		int outData56[] = new int[7];
		int key28l[] = new int[4];
		int key28r[] = new int[4];
		int key56o[] = new int[7];
		permutationChoose1(inKey, outData56);
		key28l[0] = (byte) (outData56[0] >> 4);
		key28l[1] = (byte) ((outData56[0] << 4) | (outData56[1] >> 4));
		key28l[2] = (byte) ((outData56[1] << 4) | (outData56[2] >> 4));
		key28l[3] = (byte) ((outData56[2] << 4) | (outData56[3] >> 4));
		for (int i = 0; i < 4; i++)
			if (key28l[i] < 0)
				key28l[i] = key28l[i] + 256;

		key28r[0] = (outData56[3] & 15);
		key28r[1] = outData56[4];
		key28r[2] = outData56[5];
		key28r[3] = outData56[6];
		for (int i = 0; i < 16; i++) {
			cycleMove(key28l, bitDisplace[i]);
			cycleMove(key28r, bitDisplace[i]);
			key56o[0] = (byte) ((key28l[0] << 4) | (key28l[1] >> 4));
			key56o[1] = (byte) ((key28l[1] << 4) | (key28l[2] >> 4));
			key56o[2] = (byte) ((key28l[2] << 4) | (key28l[3] >> 4));
			key56o[3] = (byte) ((key28l[3] << 4) | (key28r[0]));
			key56o[4] = key28r[1];
			key56o[5] = key28r[2];
			key56o[6] = key28r[3];
			for (int j = 0; j < 7; j++)
				if (key56o[j] < 0)
					key56o[j] = key56o[j] + 256;

			permutationChoose2(key56o, outKey[i]);
		}
	}

	private static void encry(int[] inData, int[] subKey, int[] outData) {
		int outBuf[] = new int[6];
		int buf[] = new int[8];
		expand(inData, outBuf);
		for (int i = 0; i < 6; i++) {
			outBuf[i] = (byte) (outBuf[i] ^ subKey[i]);
			if (outBuf[i] < 0)
				outBuf[i] = outBuf[i] + 256;
		}
		buf[0] = (byte) (outBuf[0] >> 2); // xxxxxx -> 2
		buf[1] = (byte) (((outBuf[0] & 3) << 4) | (outBuf[1] >> 4)); // 4 <- xx
		buf[2] = (byte) (((outBuf[1] & 15) << 2) | (outBuf[2] >> 6)); // 2 <-
		buf[3] = (byte) (outBuf[2] & 63); // xxxxxx
		buf[4] = (byte) (outBuf[3] >> 2); // xxxxxx
		buf[5] = (byte) (((outBuf[3] & 3) << 4) | (outBuf[4] >> 4)); // xx xxxx
		buf[6] = (byte) (((outBuf[4] & 15) << 2) | (outBuf[5] >> 6)); // xxxx xx
		buf[7] = (byte) (outBuf[5] & 63); // xxxxxx
		for (int i = 0; i < 8; i++) {
			if (buf[i] < 0)
				buf[i] = buf[i] + 256;
			buf[i] = si(i, buf[i]);
		}
		for (int i = 0; i < 4; i++) {
			outBuf[i] = (byte) ((buf[i * 2] << 4) | buf[i * 2 + 1]);
			if (outBuf[i] < 0)
				outBuf[i] = outBuf[i] + 256;
		}
		permutation(outBuf);
		for (int i = 0; i < 4; i++)
			outData[i] = outBuf[i];
	}

	private static void desData(TDesMode desMode, int[] inData, int[] outData) {
		// inData, outData 都为8Bytes，否则出错
		int temp[] = new int[4];
		int buf[] = new int[4];
		for (int i = 0; i < 8; i++)
			outData[i] = inData[i];
		initPermutation(outData);
		if (desMode == TDesMode.dmEncry) {
			for (int i = 0; i < 16; i++) {
				for (int j = 0; j < 4; j++)
					temp[j] = outData[j]; // temp = Ln
				for (int j = 0; j < 4; j++)
					outData[j] = outData[j + 4]; // Ln+1 = Rn
				encry(outData, subKey[i], buf); // Rn ==Kn==> buf
				for (int j = 0; j < 4; j++)
					outData[j + 4] = (temp[j] ^ buf[j]); // Rn+1 = Ln^buf
			}
			for (int j = 0; j < 4; j++)
				temp[j] = outData[j + 4];
			for (int j = 0; j < 4; j++)
				outData[j + 4] = outData[j];
			for (int j = 0; j < 4; j++)
				outData[j] = temp[j];
		} else if (desMode == TDesMode.dmDESry) {
			for (int i = 15; i >= 0; i--) {
				for (int j = 0; j < 4; j++)
					temp[j] = outData[j];
				for (int j = 0; j < 4; j++)
					outData[j] = outData[j + 4];
				encry(outData, subKey[i], buf);
				for (int j = 0; j < 4; j++)
					outData[j + 4] = (temp[j] ^ buf[j]);
			}
			for (int j = 0; j < 4; j++)
				temp[j] = outData[j + 4];
			for (int j = 0; j < 4; j++)
				outData[j + 4] = outData[j];
			for (int j = 0; j < 4; j++)
				outData[j] = temp[j];
		}
		conversePermutation(outData);
	}

	public final static String MD5(String s) {
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
		try {
			byte[] btInput = s.getBytes("UTF-8");
			// 获得MD5摘要算法的 MessageDigest 对象
			MessageDigest mdInst = MessageDigest.getInstance("MD5");
			// 使用指定的字节更新摘要
			mdInst.update(btInput);
			// 获得密文
			byte[] md = mdInst.digest();
			// 把密文转换成十六进制的字符串形式
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			return new String(str);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
