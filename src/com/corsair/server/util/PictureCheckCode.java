package com.corsair.server.util;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;

import com.corsair.server.base.CSContext;
import com.corsair.server.csession.CSession;

//
/**
 * 验证码
 * 
 * @author Administrator
 *
 */
public class PictureCheckCode {
	private Random rand = new Random();

	/**
	 * 生成随机颜色
	 * 
	 * @param start
	 *            [int]
	 * @param end
	 *            [int]
	 * @return Color [object]
	 */
	private Color getRandColor(int start, int end) {
		int randNum;
		if (start > 255)
			start = 255;
		if (end > 255)
			end = 255;
		if (start > end)
			randNum = start - end;
		else
			randNum = end - start;
		int r = start + rand.nextInt(randNum);
		int g = start + rand.nextInt(randNum);
		int b = start + rand.nextInt(randNum);
		return new Color(r, g, b);
	}

	/**
	 * 着色\旋转\缩放
	 * 
	 * @param word
	 *            文字
	 * @param g
	 *            图片对象
	 */
	private void coloredAndRotation(String word, int i, Graphics g) {
		int padding_left = 10, padding_top = 20;// 边距
		/** 着色 **/
		g.setColor(new Color(20 + rand.nextInt(110), 20 + rand.nextInt(110), 20 + rand.nextInt(110)));
		/** 旋转 **/
		Graphics2D g2d = (Graphics2D) g;
		AffineTransform trans = new AffineTransform();
		trans.rotate(rand.nextInt(20) * 3.14 / 180, 15 * i + 8, 7);// 20 角度
		/** 缩放 **/
		float scaleSize = rand.nextFloat() + 0.9f;//
		if (scaleSize > 1f)
			scaleSize = 1f;
		trans.scale(scaleSize, scaleSize);
		g2d.setTransform(trans);
		g.drawString(word, 15 * i + padding_left, padding_top);
	}

	/**
	 * 生成100条干扰线
	 * 
	 * @param g2d
	 * @param width
	 * @param height
	 */
	private void getRandLine(Graphics2D g2d, int width, int height) {
		for (int i = 0; i < 100; i++) {
			int x = rand.nextInt(width - 1);
			int y = rand.nextInt(height - 1);
			int z = rand.nextInt(6) + 1;
			int w = rand.nextInt(12) + 1;
			BasicStroke bs = new BasicStroke(2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL);
			Line2D line = new Line2D.Double(x, y, x + z, y + w);
			g2d.setStroke(bs);
			g2d.draw(line);
		}
	}

	/**
	 * 获取随机文字
	 * 
	 * @param length
	 *            [int] 验证码长度
	 * @param g
	 *            [Graphics] 图片对象
	 * @return String
	 * @case1:A-Z
	 * @case2:chinese
	 * @default:0-9
	 */
	private String getRandWord(int length, Graphics g) {
		String finalWord = "";
		String[] array = { "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f", "g", "h", "j", "k", "m", "n", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "A", "B", "C", "D", "E", "F", "G", "H", "J", "K", "M",
				"N", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z" };
		int l = array.length - 1;
		Random r = new Random();
		for (int i = 0; i < length; i++) {
			String cw = array[r.nextInt(l)];
			finalWord += cw;
			this.coloredAndRotation(cw, i, g);
		}
		return finalWord;
	}

	public void resCheckCode() throws Exception {
		int digs = 4;// 位数
		// 设制不缓存图片
		HttpServletResponse response = CSContext.getResponse();
		if (response == null)
			return;
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "No-cache");
		response.setDateHeader("Expires", 0);

		// 生成图片
		response.setContentType("image/jpeg");
		int width = digs * 20; // 图片大小和位数相关
		int height = 30;
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

		Graphics g = image.getGraphics();
		Graphics2D g2d = (Graphics2D) g;
		Font mFont = new Font("宋体", Font.BOLD, 22);
		g.setColor(this.getRandColor(200, 250));
		g.fillRect(0, 0, width, height);
		g.setFont(mFont);
		g.setColor(this.getRandColor(180, 200));
		getRandLine(g2d, width, height);
		String randCode = this.getRandWord(digs, g);
		CSession.putvalue(CSContext.getSession().getId(), "randcode", randCode);
		g.dispose();
		ImageIO.write(image, "JPEG", response.getOutputStream());
	}
}
