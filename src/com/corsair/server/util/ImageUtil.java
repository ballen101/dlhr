package com.corsair.server.util;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;

import com.corsair.dbpool.util.Logsw;

public class ImageUtil {
	private static String DEFAULT_PREVFIX = "thumb_";
	private static Boolean DEFAULT_FORCE = false;

	/**
	 * <p>
	 * Title: thumbnailImage
	 * </p>
	 * <p>
	 * Description: 根据图片路径生成缩略图
	 * </p>
	 * 
	 * @param imagePath
	 *            原图片路径
	 * @param w
	 *            缩略图宽
	 * @param h
	 *            缩略图高
	 * @param prevfix
	 *            生成缩略图的前缀
	 * @param force
	 *            是否强制按照宽高生成缩略图(如果为false，则生成最佳比例缩略图)
	 */
	public String thumbnailImage(File imgFile, int w, int h, String prevfix, boolean force) {
		String rst = null;
		if (imgFile.exists()) {
			try {
				// ImageIO 支持的图片类型 : [BMP, bmp, jpg, JPG, wbmp, jpeg, png, PNG,
				// JPEG, WBMP, GIF, gif]
				String types = Arrays.toString(ImageIO.getReaderFormatNames());
				String suffix = null;
				// 获取图片后缀
				if (imgFile.getName().indexOf(".") > -1) {
					suffix = imgFile.getName().substring(imgFile.getName().lastIndexOf(".") + 1);
				}// 类型和图片后缀全部小写，然后判断后缀是否合法
				if (suffix == null || types.toLowerCase().indexOf(suffix.toLowerCase()) < 0) {
					Logsw.error("Sorry, the image suffix is illegal. the standard image suffix is {}." + types);
					return null;
				}
				Image img = ImageIO.read(imgFile);
				if (!force) {
					// 根据原图与要求的缩略图比例，找到最合适的缩略图比例
					int width = img.getWidth(null);
					int height = img.getHeight(null);
					if ((width * 1.0) / w < (height * 1.0) / h) {
						if (width > w) {
							h = Integer.parseInt(new java.text.DecimalFormat("0").format(height * w / (width * 1.0)));
						}
					} else {
						if (height > h) {
							w = Integer.parseInt(new java.text.DecimalFormat("0").format(width * h / (height * 1.0)));
						}
					}
				}
				BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
				Graphics g = bi.getGraphics();
				g.drawImage(img, 0, 0, w, h, Color.LIGHT_GRAY, null);
				g.dispose();
				String p = imgFile.getPath();
				// 将图片保存在原目录并加上前缀
				rst = p.substring(0, p.lastIndexOf(File.separator)) + File.separator + prevfix + imgFile.getName();
				ImageIO.write(bi, suffix, new File(rst));
			} catch (IOException e) {
				try {
					Logsw.error("generate thumbnail image failed.", e);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		} else {
			Logsw.error("the image is not exist.");
		}
		return rst;
	}

	public String thumbnailImage(String imagePath, int w, int h, String prevfix, boolean force) {
		File imgFile = new File(imagePath);
		return thumbnailImage(imgFile, w, h, prevfix, force);
	}

	public String thumbnailImage(String imagePath, int w, int h, boolean force) {
		return thumbnailImage(imagePath, w, h, DEFAULT_PREVFIX, force);
	}

	public String thumbnailImage(String imagePath, int w, int h) {
		return thumbnailImage(imagePath, w, h, DEFAULT_FORCE);
	}

	/**
	 * 在源图片上设置水印文字
	 * 
	 * @param srcImagePath
	 *            源图片路径
	 * @param alpha
	 *            透明度（0<alpha<1）
	 * @param font
	 *            字体（例如：宋体）
	 * @param fontStyle
	 *            字体格式(例如：普通样式--Font.PLAIN、粗体--Font.BOLD )
	 * @param fontSize
	 *            字体大小
	 * @param color
	 *            字体颜色(例如：黑色--Color.BLACK)
	 * @param inputWords
	 *            输入显示在图片上的文字
	 * @param x
	 *            文字显示起始的x坐标
	 * @param y
	 *            文字显示起始的y坐标
	 * @param imageFormat
	 *            写入图片格式（png/jpg等）
	 * @param toPath
	 *            写入图片路径
	 * @throws IOException
	 */
	public static void alphaWords2Image(String srcImagePath, float alpha, String font,
			int fontStyle, int fontSize, Color color, String inputWords, int x,
			int y, String imageFormat, String toPath) throws IOException {
		FileOutputStream fos = null;
		try {
			BufferedImage image = ImageIO.read(new File(srcImagePath));
			// 创建java2D对象
			Graphics2D g2d = image.createGraphics();
			// 用源图像填充背景
			g2d.drawImage(image, 0, 0, image.getWidth(), image.getHeight(),
					null, null);
			// 设置透明度
			AlphaComposite ac = AlphaComposite.getInstance(
					AlphaComposite.SRC_OVER, alpha);
			g2d.setComposite(ac);
			// 设置文字字体名称、样式、大小
			g2d.setFont(new Font(font, fontStyle, fontSize));
			g2d.setColor(color);// 设置字体颜色
			for (String line : inputWords.split("\n")) {
				if ((line != null) && (!line.isEmpty()))
					g2d.drawString(line, x, y += g2d.getFontMetrics().getHeight());
			}
			// g2d.drawString(inputWords, x, y); // 输入水印文字及其起始x、y坐标

			g2d.dispose();
			fos = new FileOutputStream(toPath);
			ImageIO.write(image, imageFormat, fos);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fos != null) {
				fos.close();
			}
		}
	}

}
