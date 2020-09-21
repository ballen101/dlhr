package com.corsair.server.util;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.corsair.cjpa.CJPABase;
import com.corsair.cjpa.CJPABase.CJPAStat;
import com.corsair.cjpa.util.CjpaUtil;
import com.corsair.cjpa.CJPALineData;
import com.corsair.dbpool.CDBConnection;
import com.corsair.dbpool.DBPools;
import com.corsair.dbpool.util.Logsw;
import com.corsair.dbpool.util.Systemdate;
import com.corsair.server.baiduueditor.FileType;
import com.corsair.server.base.CSContext;
import com.corsair.server.base.ConstsSw;
import com.corsair.server.generic.Shw_physic_file;

/**
 * 上传文件
 * 
 * @author Administrator
 *
 */
public class UpLoadFileEx {

	/**
	 * 上传BASE 64 文件
	 * 
	 * var dataUrl = cvs.toDataURL('image/jpeg', 1);
	 * var dataUrl = dataUrl.replace("data:" + fileType + ";base64,", '');
	 * 
	 * @param str64
	 * @param pfname
	 *            文件名，显示到数据库的文件名
	 * @param extfname
	 *            扩展文件名 jpg||.jpg
	 * @return
	 * @throws Exception
	 */
	public static Shw_physic_file douploadBase64File(String str64, String pfname, String extfname) throws Exception {
		Shw_physic_file rst = new Shw_physic_file();
		byte[] datasave = Base64.decodeBase64(str64.getBytes());// VclZip.gunzip(str64).getBytes();//

		String fsep = System.getProperty("file.separator");

		String filePath = "";

		if ((ConstsSw.geAppParmStr("UDFilePath") == null) || (ConstsSw.geAppParmStr("UDFilePath").length() == 0))
			filePath = ConstsSw._root_filepath;
		else
			filePath = ConstsSw.geAppParmStr("UDFilePath");
		if (!filePath.substring(filePath.length() - 1).equalsIgnoreCase(fsep))
			filePath = filePath + fsep;

		String apath = Systemdate.getStrDateNowYYMM();// 相对路径
		filePath = filePath + apath + fsep;

		if (filePath.trim().isEmpty()) {
			throw new Exception("没有找到合适的文件夹");
		}

		if (extfname == null)
			extfname = "";
		if (!extfname.isEmpty()) {
			if (!extfname.substring(0, 1).equals("."))
				extfname = "." + extfname;
		}

		File dir = new File(filePath);
		if (!dir.exists() && !dir.isDirectory() && !dir.mkdirs())
			throw new Exception("创建文件夹<" + filePath + ">错误，联系管理员！");

		String tfname = ((pfname == null) ? "_base64" : pfname);
		// String fname = un + "_" + Systemdate.getStrDateByFmt(new Date(), "hhmmss") + tfname + extfname;
		String fname = getNoReptFileName(extfname);// 数据库里面存的文件名
		String fullfname = filePath + fname;// 物理文件
		String un = (CSContext.getUserNameEx() == null) ? "SYSTEM" : CSContext.getUserNameEx();
		File file = new File(fullfname);
		synchronized (file) {
			BufferedOutputStream fos = new BufferedOutputStream(new FileOutputStream(fullfname, false));
			fos.write(datasave);
			fos.flush();
			fos.close();
		}
		rst.ppath.setValue(apath);
		rst.pfname.setValue(fname);
		rst.create_time.setAsDatetime(new Date());
		rst.creator.setValue(un);
		rst.displayfname.setValue(tfname + extfname);// 数据库里面显示的文件名
		rst.filesize.setValue("0");// K
		rst.extname.setValue(getExtention(tfname + extfname));
		rst.fsid.setAsInt(0);
		rst.setJpaStat(CJPAStat.RSINSERT);
		rst.save();

		return rst;
	}

	/**
	 * @return 获取文件夹路径 配置路径+YYMM(日期) 如果未配置，用基准路径
	 * @throws Exception
	 */
	private static String getfilepath(String apath) throws Exception {
		String fsep = System.getProperty("file.separator");
		String filePath = "";
		if ((ConstsSw.geAppParmStr("UDFilePath") == null) || (ConstsSw.geAppParmStr("UDFilePath").length() == 0))
			filePath = ConstsSw._root_filepath;
		else
			filePath = ConstsSw.geAppParmStr("UDFilePath");
		if (!filePath.substring(filePath.length() - 1).equalsIgnoreCase(fsep))
			filePath = filePath + fsep;

		filePath = filePath + apath + fsep;// 相对路径
		if (filePath.trim().isEmpty()) {
			throw new Exception("没有找到合适的文件夹");
		}

		File dir = new File(filePath);
		if (!dir.exists() && !dir.isDirectory() && !dir.mkdirs())
			throw new Exception("创建文件夹<" + filePath + ">错误，联系管理员！");
		return filePath;
	}

	/**
	 * 获取上传文件存到指定目录，比如上传微信证书文件等,与附件管理无关
	 * 
	 * @param filepath
	 * @return 返回不包含路径的文件名
	 * @throws Exception
	 */
	public static String doupload(String filepath) throws Exception {
		if (!CSContext.isMultipartContent())
			throw new Exception("没有文件");

		String fsep = System.getProperty("file.separator");
		String fileTem = (filepath.endsWith(fsep)) ? filepath : filepath + fsep;
		File dir = new File(fileTem);
		if (!dir.exists() && !dir.isDirectory() && !dir.mkdirs())
			throw new Exception("创建文件夹<" + fileTem + ">错误，联系管理员！");

		long MAXFILESIZE = 2 * 1024 * 1024 * 1024; // 允许的最大文件尺寸 2*1000M
		DiskFileItemFactory factory = new DiskFileItemFactory();
		factory.setSizeThreshold(1024 * 1024); // 设置缓冲区
		factory.setRepository(new File(fileTem)); // 文件临时目录
		ServletFileUpload upload = new ServletFileUpload(factory);
		upload.setSizeMax(MAXFILESIZE);// 设置最大文件
		upload.setProgressListener(new UpLoadFileExProgressListener());
		upload.setHeaderEncoding("UTF-8");
		List<?> items = upload.parseRequest(CSContext.getRequest());
		HashMap<String, String> parms = CSContext.getParms();
		for (int i = 0; i < items.size(); i++) {
			FileItem fi = (FileItem) items.get(i);
			if (!fi.isFormField()) {// 如果是文件
				String displayfname = fi.getName();
				String fname = fileTem + displayfname;
				File file = new File(fname);
				if ((file.isFile()) && (!file.delete()))
					throw new Exception("文件【" + fname + "】已经存在且删除错误，联系管理员！");
				fi.write(new File(fname));
				return displayfname;// 只处理第一个文件
			} else {
				parms.put(fi.getFieldName(), fi.getString("UTF-8"));
			}
		}
		return null;
	}

	public static CJPALineData<Shw_physic_file> doupload(boolean imgthb) throws Exception {
		return doupload(imgthb, null);
	}

	/**
	 * 上传文件到 系统文档文件夹
	 * 
	 * @param imgthb
	 *            是否生成缩略图
	 * @return
	 * @throws Exception
	 */
	public static CJPALineData<Shw_physic_file> doupload(boolean imgthb, String filetitle) throws Exception {
		long MAXFILESIZE = 2 * 1024 * 1024 * 1024; // 默认允许的最大文件尺寸 2*1000M
		return doupload(imgthb, filetitle, null, MAXFILESIZE);
	}

	/**
	 * @param imgthb
	 * @param allowTypes
	 *            允许的文件类型 [".png", ".jpg", ".jpeg", ".gif", ".bmp"]
	 * @param maxSize
	 *            文件大小
	 * @return
	 * @throws Exception
	 */
	public static CJPALineData<Shw_physic_file> doupload(boolean imgthb, String filetitle, String[] allowTypes, long maxSize) throws Exception {
		if (!CSContext.isMultipartContent())
			throw new Exception("没有文件");
		FilePath fpath = getFilePath();
		return writefiles(fpath.FilePath, fpath.aPath, fpath.TempPath, imgthb, filetitle, allowTypes, maxSize);
	}

	/**
	 * 获取上传文件的文件路径(附件)
	 * 
	 * @return
	 * @throws Exception
	 */
	public static FilePath getFilePath() throws Exception {
		FilePath rst = new FilePath();
		String fsep = System.getProperty("file.separator");
		String filePath = "";
		if ((ConstsSw.geAppParmStr("UDFilePath") == null) || (ConstsSw.geAppParmStr("UDFilePath").length() == 0))
			filePath = ConstsSw._root_filepath;
		else
			filePath = ConstsSw.geAppParmStr("UDFilePath");
		if (!filePath.substring(filePath.length() - 1).equalsIgnoreCase(fsep))
			filePath = filePath + fsep;

		String fileTem = filePath + "temp" + fsep;

		String apath = Systemdate.getStrDateNowYYMM();// 相对路径
		filePath = filePath + apath + fsep;

		if (filePath.trim().isEmpty()) {
			throw new Exception("没有找到合适的文件夹");
		}

		File dir = new File(filePath);
		if (!dir.exists() && !dir.isDirectory() && !dir.mkdirs())
			throw new Exception("创建文件夹<" + filePath + ">错误，联系管理员！");

		dir = new File(fileTem);
		if (!dir.exists() && !dir.isDirectory() && !dir.mkdirs())
			throw new Exception("创建文件夹" + fileTem + "错误，联系管理员！");
		rst.FilePath = filePath;
		rst.aPath = apath;
		rst.TempPath = fileTem;
		return rst;
	}

	/**
	 * 写入文件
	 * 
	 * @param filePath
	 *            除了文件名剩下的路径
	 * @param apath
	 *            写入数据库的相对路径
	 * @param fileTem
	 *            临时文件目录
	 * @param imgthb
	 *            是否生成缩略图
	 * @param allowTypes
	 *            null 允许所有文件
	 * @param maxSize
	 *            大小限制 k
	 * @return
	 * @throws Exception
	 */
	private static CJPALineData<Shw_physic_file> writefiles(String filePath, String apath, String fileTem, boolean imgthb, String filetitle,
			String[] allowTypes, long maxSize) throws Exception {
		long MAXFILESIZE = maxSize; // 允许的最大文件尺寸 2*1000M
		DiskFileItemFactory factory = new DiskFileItemFactory();
		factory.setSizeThreshold(1024 * 1024); // 设置缓冲区
		factory.setRepository(new File(fileTem)); // 文件临时目录
		ServletFileUpload upload = new ServletFileUpload(factory);
		upload.setSizeMax(MAXFILESIZE);// 设置最大文件
		upload.setProgressListener(new UpLoadFileExProgressListener());
		CJPALineData<Shw_physic_file> rst = new CJPALineData<Shw_physic_file>(Shw_physic_file.class);
		upload.setHeaderEncoding("UTF-8");
		List<?> items = upload.parseRequest(CSContext.getRequest());
		HashMap<String, String> parms = CSContext.getParms();
		for (int i = 0; i < items.size(); i++) {
			FileItem fi = (FileItem) items.get(i);
			if (!fi.isFormField()) {// 如果是文件
				String displayfname = fi.getName();// 数据库显示的文件名
				String suffix = FileType.getSuffixByFilename(displayfname);
				if (!validType(suffix, allowTypes)) {
					throw new Exception("不许可的文件类型");
				}
				displayfname = new File(displayfname.trim()).getName();
				String extfname = getExtention(displayfname);
				Logsw.debug("上传文件：" + displayfname);
				String un = (CSContext.getUserNameEx() == null) ? "SYSTEM" : CSContext.getUserNameEx();
				// String fname = un + "_" + Systemdate.getStrDateByFmt(new Date(), "hhmmss") + "_" + displayfname;
				String fname = getNoReptFileName(extfname);
				String fullfname = filePath + fname;
				fi.write(new File(fullfname));
				DecimalFormat df = new DecimalFormat("0.##");
				Shw_physic_file pf = new Shw_physic_file();
				pf.ppath.setValue(apath);
				pf.pfname.setValue(fname);
				pf.create_time.setAsDatetime(new Date());
				pf.creator.setValue(un);

				pf.displayfname.setValue((filetitle == null) ? displayfname : filetitle);
				pf.filesize.setValue(df.format(fi.getSize() / (1024f)));// K
				pf.extname.setValue(extfname);
				pf.fsid.setAsInt(0);
				pf.setJpaStat(CJPAStat.RSINSERT);
				pf.save();
				if (imgthb) {
					createThemImg(pf, apath, displayfname, filetitle, fullfname);
				}
				rst.add(pf);
			} else {
				parms.put(fi.getFieldName(), fi.getString("UTF-8"));
			}
		}
		// extname filesize filecreator filecreate_time
		CSContext.removeParm("uploadfile");
		return rst;
	}

	/**
	 * 将JPA某Blob longtext等类型字段值，字段值用Base64编码， 写入文件
	 * 
	 * @param fdvalue
	 *            用来写入文件的字段内容 base64字符串
	 * @param fname
	 *            文件名 包含扩展文件名，不包含路径
	 * @return
	 * @throws Exception
	 */
	public static Shw_physic_file witrefileFromFieldValue(String fdvalue, String fname) throws Exception {
		String apath = Systemdate.getStrDateNowYYMM();// 相对路径
		String filePath = getfilepath(apath);
		String un = (CSContext.getUserNameEx() == null) ? "SYSTEM" : CSContext.getUserNameEx();
		String dbfname = un + "_" + Systemdate.getStrDateByFmt(new Date(), "hhmmss") + "_" + fname;
		String fullfname = filePath + dbfname;
		OutputStream out = new FileOutputStream(fullfname);
		CjpaUtil.generateImage(fdvalue, out);
		out.flush();
		out.close();

		File file = new File(fullfname);
		if (file.exists()) {
			DecimalFormat df = new DecimalFormat("0.##");
			Shw_physic_file pf = new Shw_physic_file();
			pf.ppath.setValue(apath);
			pf.pfname.setValue(dbfname);
			pf.create_time.setAsDatetime(new Date());
			pf.creator.setValue(un);
			pf.displayfname.setValue(fname);
			pf.filesize.setValue(df.format(file.length() / (1024f)));// K
			pf.extname.setValue(getExtention(fname));
			pf.fsid.setAsInt(0);
			pf.setJpaStat(CJPAStat.RSINSERT);
			pf.save();
			return pf;
		} else
			throw new Exception("写入之后文件【" + fullfname + "】不存在!");
	}

	public static String getExtention(String fileName) {
		if (fileName != null && fileName.length() > 0 && fileName.lastIndexOf(".") > -1) {
			return fileName.substring(fileName.lastIndexOf("."));
		}
		return "";
	}

	private static void createThemImg(Shw_physic_file pf, String apath, String displayfname, String filetitle, String fullfname) {
		try {
			String tfilename = new ImageUtil().thumbnailImage(fullfname, 200, 200);
			String un = (CSContext.getUserNameEx() == null) ? "SYSTEM" : CSContext.getUserNameEx();
			File f = new File(tfilename);
			if (tfilename != null) {
				Shw_physic_file pfe = new Shw_physic_file();
				pfe.ppath.setValue(apath);
				pfe.pfname.setValue(f.getName());
				pfe.create_time.setAsDatetime(new Date());
				pfe.creator.setValue(un);
				pfe.displayfname.setValue((filetitle == null) ? displayfname : filetitle);
				pfe.filesize.setValue("0");
				pfe.extname.setValue(getExtention(displayfname));
				pfe.fsid.setAsInt(0);
				pfe.ppfid.setAsInt(pf.pfid.getAsIntDefault(0));
				pfe.ptype.setAsInt(1);// 缩略图
				pfe.save();
			}
		} catch (Exception e) {
			try {
				Logsw.error("生成缩略图错误", e);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

	public static boolean isImage(Shw_physic_file pf) {
		String fs = System.getProperty("file.separator");
		String fullname = ConstsSw.geAppParmStr("UDFilePath") + fs + pf.ppath.getValue() + fs + pf.pfname.getValue();
		File file = new File(fullname);
		if (!file.exists()) {
			fullname = ConstsSw._root_filepath + "attifiles" + fs + pf.ppath.getValue() + fs + pf.pfname.getValue();
			file = new File(fullname);
			if (file.exists())
				return false;
		}
		try {
			BufferedImage Image = ImageIO.read(file);
			return (Image != null);
		} catch (IOException e) {
			return false;
		}
	}

	public static void delAttFile(String pfid) throws Exception {
		Shw_physic_file pf = new Shw_physic_file();
		pf.findByID(pfid);
		if (pf.isEmpty()) {
			Logsw.debug("没有找到物理文件记录【" + pfid + "】");
			return;
		}
		// 删除自关联文档 比如缩略图等
		String sqlstr = "SELECT * FROM shw_physic_file f WHERE f.ppfid=" + pf.pfid.getValue();
		CJPALineData<Shw_physic_file> phfs = new CJPALineData<Shw_physic_file>(Shw_physic_file.class);
		phfs.findDataBySQL(sqlstr);
		if (phfs.size() > 0) {
			for (CJPABase jpa : phfs) {
				delAttFile(((Shw_physic_file) jpa).pfid.getValue());
			}
		}
		String fullname = getPhysicalFileName(pf);
		File file = new File(fullname);
		if (file.exists())
			file.delete();
		if (pf.isEmpty())
			throw new Exception("没有找到物理文件记录！");
		pf.delete();
	}

	public static void updateDisplayName(String pfid, String displayname) throws Exception {
		CDBConnection con = DBPools.defaultPool().getCon("UpLoadFileEx.updateDisplayName");
		try {
			Shw_physic_file pf = new Shw_physic_file();
			pf.findByID4Update(con, pfid, false);
			if (pf.isEmpty()) {
				throw new Exception("没有找到物理文件【" + pfid + "】记录！");
			}
			String sqlstr = "UPDATE shw_physic_file SET displayfname='" + displayname + "'  WHERE ppfid=" + pf.pfid.getValue();
			con.execsql(sqlstr);
			pf.displayfname.setValue(displayname);
			pf.save(con);
			con.submit();
		} catch (Exception e) {
			con.rollback();
			throw e;
		} finally {
			con.close();
		}
	}

	public static String getPhysicalFileName(String pfid) {
		try {
			Shw_physic_file pf = new Shw_physic_file();
			pf.findByID(pfid);
			return getPhysicalFileName(pf);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public static String getPhysicalFileName(Shw_physic_file pf) {
		String fs = System.getProperty("file.separator");
		String fullname = ConstsSw.geAppParmStr("UDFilePath") + fs + pf.ppath.getValue() + fs + pf.pfname.getValue();
		Logsw.debug("getPhysicalFileName fullname:" + fullname);
		if (!(new File(fullname)).exists()) {
			fullname = ConstsSw._root_filepath + "attifiles" + fs + pf.ppath.getValue() + fs + pf.pfname.getValue();
		}
		return fullname;
	}

	/**
	 * 获取不重复文件名
	 * 如果有登录用户 则用登陆用户ID+_+UUID
	 * 
	 * @param extfname
	 *            扩展文件名 .jpg || jpg
	 * @return
	 */
	public static String getNoReptFileName(String extfname) {
		if (extfname == null)
			extfname = "";
		if (!extfname.isEmpty()) {
			if (!extfname.substring(0, 1).equals("."))
				extfname = "." + extfname;
		}
		String un = (CSContext.getUserIDNoErr() == null) ? "SYSTEM" : CSContext.getUserIDNoErr();
		return un + "_" + CorUtil.getShotuuid() + extfname;
	}

	/**
	 * 是否存在的文件类型
	 * 
	 * @param type
	 *            //.png
	 * @param allowTypes
	 *            //[".png", ".jpg", ".jpeg", ".gif", ".bmp"]
	 * @return
	 */
	private static boolean validType(String type, String[] allowTypes) {
		if (allowTypes == null)
			return true;
		if (allowTypes.length == 0)
			return true;
		List<String> list = Arrays.asList(allowTypes);
		return list.contains(type);
	}

}
