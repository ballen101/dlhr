package com.corsair.server.util;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Hashtable;

import javax.imageio.ImageIO;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.EncodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

/**
 * @author shangwen
 *         Version 1是21 x 21的矩阵，
 *         Version 2是 25 x 25的矩阵，
 *         Version 3是29的尺寸，
 *         每增加一个version，就会增加4的尺寸，公式是：(V-1)4 + 21（V是版本号） 最高Version 40，(40-1)4+21 = 177，所以最高是177 x 177的正方形
 */
public class MatrixUtil {

	private final String CHARSET = "utf-8";
	private final int BLACK = 0xFF000000;
	private final int WHITE = 0xFFFFFFFF;

	private String text = null; // 转二维码、条码的文本
	private String textext = null;// 扩展文本用于显示在二维码图片下面
	private String logofilename = null;// logo文件名
	private int qcwidth = 300;// 二维码、条码大小
	private int qcheight = 300;// 二维码、条码大小
	private int logowidth = 65;// logo大小
	private int logoheight = 65;// logo大小

	public MatrixUtil() {
	}

	public MatrixUtil(String text) {
		this.text = text;
	}

	public MatrixUtil(String text, String textext) {
		this.text = text;
		this.textext = textext;
	}

	/**
	 * 二维码图片写入文件
	 * 
	 * @param text
	 * @param file
	 * @param w
	 * @param h
	 * @param format
	 *            文件格式字符串 如.jpg .png等
	 * @return
	 * @throws Exception
	 */
	public boolean toQrcodeFile(File file, String format) throws Exception {
		BitMatrix matrix = toQRCodeMatrix();
		if (matrix != null) {
			try {
				writeToFile(matrix, format, file);
				return true;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * 二维码图片写入流
	 * 
	 * @param stream
	 * @param format
	 * @return
	 * @throws Exception
	 */
	public boolean toQrcodeStream(OutputStream stream, String format) throws Exception {
		BitMatrix matrix = toQRCodeMatrix();
		if (matrix != null) {
			try {
				writeToStream(matrix, format, stream);
				return true;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return false;
	}

	// 条码
	public boolean toBarCodeFile(File file, String format) throws Exception {
		BitMatrix matrix = toBarCodeMatrix();
		if (matrix != null) {
			try {
				writeToFile(matrix, format, file);
				return true;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return false;
	}

	private BitMatrix toQRCodeMatrix() {
		int width = qcwidth;
		width = (width < 300) ? 300 : width;// 最小300
		width = (width > 2048) ? 2048 : width;// 最最大2048
		int height = qcheight;
		height = (height < 300) ? 300 : height;// 最小300
		height = (height > 2048) ? 2048 : height;// 最最大2048

		// 二维码的图片格式
		Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
		// 内容所使用编码
		hints.put(EncodeHintType.CHARACTER_SET, CHARSET);
		hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
		hints.put(EncodeHintType.MARGIN, 1);
		BitMatrix bitMatrix = null;
		try {
			bitMatrix = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, width, height, hints);
		} catch (WriterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 生成二维码
		// File outputFile = new File("d:"+File.separator+"new.gif");
		// MatrixUtil.writeToFile(bitMatrix, format, outputFile);
		return bitMatrix;
	}

	/**
	 * 根据点矩阵生成黑白图。
	 * 
	 * @throws Exception
	 */
	private BufferedImage toBufferedImage(BitMatrix matrix) throws Exception {
		int width = matrix.getWidth();
		int height = matrix.getHeight();
		BufferedImage image = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				image.setRGB(x, y, matrix.get(x, y) ? BLACK : WHITE);
			}
		}
		if ((logofilename != null) && (!logofilename.isEmpty()))
			insertLogImage(image, logofilename, true);
		if ((textext != null) && (!textext.isEmpty()))
			insertExtText(image, textext);
		return image;
	}

	/**
	 * 将字符串编成一维条码的矩阵
	 */
	private BitMatrix toBarCodeMatrix() {
		int width = qcwidth;
		width = (width < 200) ? 200 : width;// 最小
		width = (width > 1024) ? 1024 : width;// 最最大
		int height = qcheight;
		height = (height < 20) ? 20 : height;// 最小
		height = (height > 200) ? 200 : height;// 最最大
		try {
			// 文字编码
			Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
			hints.put(EncodeHintType.CHARACTER_SET, CHARSET);
			BitMatrix bitMatrix = new MultiFormatWriter().encode(text,
					BarcodeFormat.CODE_128, width, height, hints);
			return bitMatrix;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 根据矩阵、图片格式，生成文件。
	 * 
	 * @throws Exception
	 */
	private void writeToFile(BitMatrix matrix, String format, File file)
			throws Exception {
		BufferedImage image = toBufferedImage(matrix);
		if (!ImageIO.write(image, format, file)) {
			throw new IOException("Could not write an image of format "
					+ format + " to " + file);
		}
	}

	/**
	 * 将矩阵写入到输出流中。
	 * 
	 * @throws Exception
	 */
	private void writeToStream(BitMatrix matrix, String format,
			OutputStream stream) throws Exception {
		BufferedImage image = toBufferedImage(matrix);
		if (!ImageIO.write(image, format, stream)) {
			throw new IOException("Could not write an image of format " + format);
		}
	}

	/**
	 * 解码，需要javase包。
	 */
	public String decode(File file) {
		BufferedImage image;
		try {
			if (file == null || file.exists() == false) {
				throw new Exception(" File not found:" + file.getPath());
			}
			image = ImageIO.read(file);
			LuminanceSource source = new BufferedImageLuminanceSource(image);
			BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
			Result result;
			// 解码设置编码方式为：utf-8，
			Hashtable<DecodeHintType, String> hints = new Hashtable<DecodeHintType, String>();
			hints.put(DecodeHintType.CHARACTER_SET, CHARSET);

			result = new MultiFormatReader().decode(bitmap, hints);

			return result.getText();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 插入LOGO
	 * 
	 * @param source
	 *            二维码图片
	 * @param logoPath
	 *            LOGO图片地址
	 * @param needCompress
	 *            是否压缩
	 * @throws Exception
	 */
	private void insertLogImage(BufferedImage source, String logoPath, boolean needCompress) throws Exception {
		File file = new File(logoPath);
		if (!file.exists()) {
			throw new Exception("logo file not found.");
		}

		int lwidth = logowidth;
		lwidth = (lwidth < 50) ? 50 : lwidth;// 最小50
		lwidth = (lwidth > 75) ? 75 : lwidth;// 最最大75
		int lheight = logoheight;
		lheight = (lheight < 50) ? 50 : lheight;// 最小50
		lheight = (lheight > 75) ? 75 : lheight;// 最最大75

		Image src = ImageIO.read(new File(logoPath));
		// int width = src.getWidth(null);
		// int height = src.getHeight(null);
		if (needCompress) { // 压缩LOGO
			// if (width > lwidth) {
			// width = lwidth;
			// }
			// if (height > lheight) {
			// height = lheight;
			// }
			Image image = src.getScaledInstance(lwidth, lheight, Image.SCALE_SMOOTH);
			BufferedImage tag = new BufferedImage(lwidth, lheight, BufferedImage.TYPE_INT_RGB);
			Graphics g = tag.getGraphics();
			g.drawImage(image, 0, 0, null); // 绘制缩小后的图
			g.dispose();
			src = image;
		}
		// 插入LOGO
		Graphics2D graph = source.createGraphics();
		int x = (qcwidth - lwidth) / 2;
		int y = (qcheight - lheight) / 2;
		graph.drawImage(src, x, y, lwidth, lheight, null);
		Shape shape = new RoundRectangle2D.Float(x, y, lwidth, lheight, 6, 6);
		graph.setStroke(new BasicStroke(3f));
		graph.draw(shape);
		graph.dispose();
	}

	private void insertExtText(BufferedImage source, String extstr) throws Exception {
		Graphics2D graph = source.createGraphics();
		// g.drawImage(srcImg, 0, 0, srcImgWidth, srcImgHeight, null);
		graph.setColor(Color.BLACK); // 根据图片的背景设置水印颜色
		Font f = new Font("宋体", Font.PLAIN, 20);
		graph.setFont(f); // 设置字体
		Point wh = getTextWH(graph, f, extstr);
		int x = (qcwidth - wh.x) / 2;
		int y = qcheight - 2;// - wh.y;//留两个像素编剧好了
		// 设置水印的坐标
		graph.drawString(extstr, x, y); // 画出水印
		graph.dispose();
	}

	/**
	 * 计算一段文字的宽度和高度
	 * 
	 * @param graph
	 * @param font
	 * @param content
	 * @return
	 */
	public static Point getTextWH(Graphics2D graph, Font font, String content) {
		FontMetrics metrics = graph.getFontMetrics(font);
		int width = 0;
		for (int i = 0; i < content.length(); i++) {
			width += metrics.charWidth(content.charAt(i));
		}
		int height = metrics.getHeight();
		return new Point(width, height);
	}

	// get and set

	public String getText() {
		return text;
	}

	public String getTextext() {
		return textext;
	}

	public String getLogofilename() {
		return logofilename;
	}

	public int getQcwidth() {
		return qcwidth;
	}

	public int getQcheight() {
		return qcheight;
	}

	public int getLogowidth() {
		return logowidth;
	}

	public int getLogoheight() {
		return logoheight;
	}

	public MatrixUtil setText(String text) {
		this.text = text;
		return this;
	}

	public MatrixUtil setTextext(String textext) {
		this.textext = textext;
		return this;
	}

	public MatrixUtil setLogofilename(String logofilename) {
		this.logofilename = logofilename;
		return this;
	}

	public MatrixUtil setQcwidth(int qcwidth) {
		this.qcwidth = qcwidth;
		return this;
	}

	public MatrixUtil setQcheight(int qcheight) {
		this.qcheight = qcheight;
		return this;
	}

	public MatrixUtil setLogowidth(int logowidth) {
		this.logowidth = logowidth;
		return this;
	}

	public MatrixUtil setLogoheight(int logoheight) {
		this.logoheight = logoheight;
		return this;
	}

}
