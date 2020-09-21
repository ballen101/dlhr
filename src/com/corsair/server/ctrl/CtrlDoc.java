package com.corsair.server.ctrl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.corsair.cjpa.CJPABase;
import com.corsair.cjpa.CJPALineData;
import com.corsair.dbpool.CDBConnection;
import com.corsair.dbpool.DBPools;
import com.corsair.dbpool.util.Systemdate;
import com.corsair.server.base.CSContext;
import com.corsair.server.base.ConstsSw;
import com.corsair.server.csession.CSession;
import com.corsair.server.generic.Shw_physic_file;
import com.corsair.server.generic.Shw_physic_file_refer;
import com.corsair.server.generic.Shwfdr;
import com.corsair.server.generic.Shwfdracl;
import com.corsair.server.generic.Shworg;
import com.corsair.server.util.CFileUtiil;
import com.corsair.server.util.UpLoadFileEx;

/**
 * 文档管理
 * 
 * @author Administrator
 *
 */
public class CtrlDoc {
	// 权限类型：管理、读写删、 读写、删写、读、路由、拒绝、无权限
	public enum SACL {
		ADMIN, WD, W, D, R, ROUTE, REJECT, EMPTY
	}

	public final static int FILEREF_FOLDER = 1; // 文件夹对文件的引用
	public final static int FILEREF_ATTR = 2;// 附件对文件的引用
	public final static int FILEREF_WORKFLOW = 3;// 工作流对文件的引用

	public final static int ACL_FOLDER_ORG = 1;// 1:文件夹对机构
	public final static int ACL_FILE_ORG = 2;// 2 文件对机构
	public final static int ACL_FOLDER_USER = 3;// 3 文件夹对个人
	public final static int ACL_FILE_USER = 4;// 4文件对个人

	public final static int ACTION_COPY_CUT_TYPE_COPY = 1;// 复制
	public final static int ACTION_COPY_CUT_TYPE_CUT = 2;// 剪切

	public final static int PASTE_STYPE_FOLDER = 1;// 文件夹
	public final static int PASTE_STYPE_FILE = 3;// 文件

	// 查询指定文件夹 包含的文件 是否有其它引用 包括文件夹 及 其它非文件夹的引用
	private final static String _sqlstr1 = " SELECT a.* FROM shw_physic_file_refer a "
			+ " WHERE a.pfid IN(	 "
			+ "   SELECT f.pfid FROM shw_physic_file f,shw_physic_file_refer fr, shwfdr fd "
			+ "   WHERE f.pfid=fr.pfid AND fr.pfrtype=1 AND fr.referid=fd.fdrid AND fd.fidpath LIKE '%s' "
			+ " )	 "
			+ " AND a.pfrtype=1 "
			+ " AND a.referid NOT IN(	 "
			+ "   SELECT fdrid FROM shwfdr fd WHERE fd.fidpath LIKE '%s'	 "
			+ " )	 "
			+ " UNION	 "
			+ " SELECT a.* FROM shw_physic_file_refer a  "
			+ " WHERE a.pfid IN(	 "
			+ "   SELECT f.pfid FROM shw_physic_file f,shw_physic_file_refer fr, shwfdr fd "
			+ "    WHERE f.pfid=fr.pfid AND fr.pfrtype=1 AND fr.referid=fd.fdrid AND fd.fidpath LIKE '%s'   "
			+ "  )	 "
			+ " AND a.pfrtype<>1";

	public static JSONArray getFoldersBySuperid(int superid) throws Exception {
		String sqlstr = "select * from shwfdr where superid=" + superid + " and entid=" + CSContext.getCurEntID();
		Object o = CSession.getvalue(CSContext.getSession().getId(), "usertype");// CSContext.getSession().getAttribute("usertype");
		boolean isadmin = ((o != null) && (o.toString().length() > 0) && (Integer.valueOf(o.toString()) == 1));
		if (!isadmin) {
			sqlstr = sqlstr + " and actived=1 ";
		}
		if (superid != 0) {

		}

		JSONArray fls = DBPools.defaultPool().opensql2json_O(sqlstr);

		Iterator<JSONObject> iter = fls.iterator();
		while (iter.hasNext()) {
			JSONObject fl = iter.next();
			SACL acl = CtrlDoc.getCurAclALL(fl, 2);

			// System.out.println(fl.getString("fdrname") + ":" + (SACL.values()[acl.ordinal()]).name());
			if ((acl == SACL.REJECT) || (acl == SACL.EMPTY)) {
				iter.remove();
			}
			fl.put("acl", acl.ordinal());
			int ct = CtrlDoc.getFolderChildernCt(fl.getString("fdrid"));
			if (ct != 0) {
				fl.put("state", "closed");
			}
			fl.put("childrenct", ct);
		}
		// 设置文件夹权限
		// 判断是否还有子文件夹
		return fls;
	}

	// 创建权限
	public static void newacl(CDBConnection con, String fid, String ownerid, String ownername, int acltype,
			SACL access, Date statime, Date endtime)
			throws Exception {
		if (endtime.getTime() < statime.getTime()) {
			throw new Exception("权限截止日期小于开始日期");
		}
		Shw_physic_file pf = null;
		Shwfdr fdr = new Shwfdr();
		if ((acltype == ACL_FOLDER_ORG) || (acltype == ACL_FOLDER_USER)) {// 文件夹
			fdr.findByID(con, String.valueOf(fid), false);
			if (fdr.isEmpty()) {
				throw new Exception("文件夹【" + fid + "】不存在");
			}
		} else if ((acltype == ACL_FILE_ORG) || (acltype == ACL_FILE_USER)) {// 文件
			pf = new Shw_physic_file();
			pf.findByID(con, String.valueOf(fid), false);
			if (pf.isEmpty()) {
				throw new Exception("文件【" + fid + "】不存在");
			}
		} else {
			throw new Exception("参数acltype错误");
		}
		String sqlstr = "SELECT * FROM shwfdracl WHERE ownerid=" + ownerid + " AND objid=" + fid + " AND acltype=" + acltype;
		Shwfdracl acl = new Shwfdracl();
		acl.findBySQL(sqlstr);
		acl.ownerid.setValue(ownerid);
		acl.ownername.setValue(ownername);
		acl.grantor.setValue(CSContext.getUserName());
		acl.granttime.setAsDatetime(new Date());
		acl.statime.setAsDatetime(statime);
		acl.endtime.setAsDatetime(endtime);
		acl.access.setAsInt(access.ordinal());
		acl.objid.setValue(fid);
		if ((acltype == ACL_FOLDER_ORG) || (acltype == ACL_FOLDER_USER))// folder
			acl.fidpath.setValue(fdr.fidpath.getValue());
		else
			// file
			acl.fidpath.setValue(fdr.fidpath.getValue() + "f" + pf.pfid.getValue() + ",");
		acl.acltype.setAsInt(acltype);
		if ((acltype == ACL_FOLDER_ORG) || (acltype == ACL_FILE_ORG)) {// org
			Shworg org = new Shworg();
			org.findByID(con, ownerid, false);
			if (org.isEmpty()) {
				throw new Exception("机构【" + ownerid + "】不存在");
			}
			acl.idpath.setValue(org.idpath.getValue());
		}
		acl.save(con);
	}

	// 创建权限
	public static void newacl(String fid, String ownerid, String ownername, int acltype, SACL access, Date statime, Date endtime) throws Exception {
		Shwfdr fdr = new Shwfdr();
		CDBConnection con = fdr.pool.getCon(fdr);
		try {
			newacl(con, fid, ownerid, ownername, acltype, access, statime, endtime);
		} catch (Exception e) {
			throw e;
		} finally {
			con.close();
		}
	}

	// 获取当前登陆用户 级联
	// obj 文件或文件夹JSON对象
	// 1 文件权限 2 文件夹权限
	public static SACL getCurAclALL(JSONObject obj, int atp) throws Exception {
		Object o = CSession.getvalue(CSContext.getSession().getId(), "usertype");// CSContext.getSession().getAttribute("usertype");
		if ((o != null) && (o.toString().length() > 0) && (Integer.valueOf(o.toString()) == 1)) {
			return SACL.ADMIN;
		}
		String fidpath = obj.getString("fidpath");
		String fidpathin = fidpath.substring(0, fidpath.length() - 1);
		String fdorgids = getFindOrgIDs();
		String userid = CSContext.getUserID();
		// 权限优先顺序 先个人 再机构 ； 文件夹从下到上
		// CJPALineData<Shwfdracl> rst = new
		// CJPALineData<Shwfdracl>(Shwfdracl.class);
		obj.getString("fidpath");
		String sqlstr = "SELECT * FROM shwfdracl a WHERE a.objid in (" + fidpathin + ")"
				+ "  AND((((acltype=3)OR(acltype=4))AND(ownerid=" + userid + "))OR(((acltype=1)OR(acltype=2))"
				+ fdorgids + ")) "
				+ " ORDER BY a.acltype DESC,a.idpath DESC,a.fidpath DESC";
		CJPALineData<Shwfdracl> acls = new CJPALineData<Shwfdracl>(Shwfdracl.class);
		acls.findDataBySQL(sqlstr);
		if (acls.size() == 0) {
			String sqlstrrote = "SELECT * FROM shwfdracl a WHERE a.fidpath like '" + fidpath + "%'"
					+ "  AND((((acltype=3)OR(acltype=4))AND(ownerid=" + userid + "))OR(((acltype=1)OR(acltype=2))"
					+ fdorgids + ")) ";
			CJPALineData<Shwfdracl> aclrts = new CJPALineData<Shwfdracl>(Shwfdracl.class);
			aclrts.findDataBySQL(sqlstrrote);
			if (aclrts.size() == 0)
				return SACL.EMPTY;
			else
				return SACL.R;
		} else {
			return SACL.values()[((Shwfdracl) acls.get(0)).access.getAsInt()];
		}
	}

	private static void listAddID(List<String> ids, String id) {
		for (String sid : ids) {
			if (sid.equals(id)) {
				return;
			}
		}
		ids.add(id);
	}

	private static String getFindOrgIDs() throws Exception {
		List<String> ids = new ArrayList<String>();
		String[] idpaths = CSContext.getUserIdpaths();

		for (String idpath : idpaths) {
			String[] sids = idpath.split(",");
			for (String sid : sids) {
				listAddID(ids, sid);
			}
		}
		String fids = "";
		for (String id : ids) {
			fids = fids + id + ",";
		}
		if (!fids.isEmpty()) {
			fids = fids.substring(0, fids.length() - 1);
			fids = " and ownerid in (" + fids + ")";
		}
		return fids;
	}

	// 获取某文件夹的的下一级文件夹个数
	public static int getFolderChildernCt(String fdrid) throws Exception {
		// 当前权限下可以看到的文件夹数量？？
		String sqlstr = "select count(*) ct from shwfdr where actived=1 and superid=" + fdrid;
		return Integer.valueOf(DBPools.defaultPool().openSql2List(sqlstr).get(0).get("ct").toString());
	}

	// 获取某文件夹的的子文件夹个数
	public static int getFolderChildernCtALL(Shwfdr fdr) throws Exception {
		String sqlstr = "select count(*) ct from shwfdr where actived=1 and fidpath like '" + fdr.fidpath.getValue() + "%'";
		return Integer.valueOf(DBPools.defaultPool().openSql2List(sqlstr).get(0).get("ct").toString());
	}

	private static class FSC {
		private float size;
		private int ct;
	}

	// 获取子文件数量 和 大小
	public static FSC getFolderChildernSizeAll(Shwfdr fdr) throws Exception {
		String sqlstr = "SELECT SUM(filesize) fs,count(*) ct FROM shw_physic_file WHERE fdrid IN("
				+ "SELECT fdrid FROM shwfdr WHERE actived=1 AND fidpath LIKE '" + fdr.fidpath.getValue() + "%'"
				+ ")";
		Object o = DBPools.defaultPool().openSql2List(sqlstr).get(0).get("fs");
		float f = (o == null) ? 0 : Float.valueOf(o.toString());
		o = DBPools.defaultPool().openSql2List(sqlstr).get(0).get("ct");
		int c = (o == null) ? 0 : Integer.valueOf(o.toString());
		FSC fsc = new FSC();
		fsc.size = f;
		fsc.ct = c;
		return fsc;
	}

	public static String getFolderAttr(int acltype, String fdrid) throws Exception {
		JSONObject jo = new JSONObject();
		if (acltype == 2) {
			Shwfdr fdr = new Shwfdr();
			fdr.findByID(fdrid);
			if (fdr.isEmpty())
				throw new Exception("没有发现ID为【" + fdrid + "】的文件夹");
			jo.put("childfoldercount", getFolderChildernCtALL(fdr));
			jo.put("creator", fdr.creator.getValue());
			jo.put("createtime", fdr.createtime.getValue());
			jo.put("updator", fdr.updator.getValue());
			jo.put("updatetime", fdr.updatetime.getValue());
			FSC fsc = getFolderChildernSizeAll(fdr);
			jo.put("size", fsc.size);
			jo.put("fcount", fsc.ct);
		} else if (acltype == 1) {

		} else {
			throw new Exception("type参数错误");
		}
		return jo.toString();
	}

	// 下载文件

	private static String getRootPath() {
		String un = (CSContext.getUserNameEx() == null) ? "SYSTEM" : CSContext.getUserNameEx();
		String fsep = System.getProperty("file.separator");
		String rootfstr = ConstsSw.geAppParmStr("UDFilePath") + "temp" + fsep + un + "_" + Systemdate.getStrDateByFmt(new Date(), "hhmmss")
				+ fsep;
		File file = new File(rootfstr);
		if (!file.exists() && !file.isDirectory()) {
			file.mkdirs();
		}
		return rootfstr;
	}

	public static void downfiles(JSONArray dinf) throws Exception {
		String fsep = System.getProperty("file.separator");
		String bootpath = getRootPath();
		String rootfstr = bootpath + "download" + fsep;
		for (int i = 0; i < dinf.size(); i++) {
			JSONObject jfd = dinf.getJSONObject(i);
			int type = jfd.getInt("type");
			if (type == 1) {// floder
				String fdrid = jfd.getString("fdrid");
				Shwfdr fdr = new Shwfdr();
				fdr.findByID(fdrid);
				if (fdr.isEmpty()) {
					throw new Exception("文件夹ID【" + fdrid + "】不存在");
				}
				String sqlstr = "select * from shwfdr where actived=1 and fidpath like '" + fdr.fidpath.getValue() + "%'";
				JSONArray ftree = DBPools.defaultPool().opensql2jsontree_o(sqlstr, "fdrid", "superid", false);
				createTemFile(ftree, rootfstr, fsep);
			} else if (type == 2) {// file
				String pfid = jfd.getString("pfid");
				Shw_physic_file pf = new Shw_physic_file();
				pf.findByID(pfid);
				if (pf.isEmpty()) {
					throw new Exception("文件ID【" + pfid + "】不存在");
				}
				String fullname = ConstsSw.geAppParmStr("UDFilePath") + fsep + pf.ppath.getValue() + fsep + pf.pfname.getValue();
				if (!(new File(fullname)).exists()) {
					fullname = ConstsSw._root_filepath + "attifiles" + fsep + pf.ppath.getValue() + fsep + pf.pfname.getValue();
					if (!(new File(fullname)).exists())
						throw new Exception("文件" + fullname + "不存在!");
				}
				String nfname = rootfstr + pf.displayfname.getValue();
				File file = new File(nfname);
				if ((file.exists()) && (file.isFile())) {
					if (!file.delete())
						throw new Exception("删除文件错误【" + nfname + "】");
				}
				RandomAccessFile outfile = new RandomAccessFile(nfname, "rw");
				forTransfer(new RandomAccessFile(fullname, "r"), outfile);
			} else
				throw new Exception("下载文件Type参数错误");
		}

		String rarfname = bootpath + "下载文件.zip";
		CFileUtiil.zipFold(rarfname, rootfstr);

		HttpServletResponse rsp = CSContext.getResponse();
		rsp.setContentType(getContentType(rarfname));
		rsp.setHeader("content-disposition", "attachment; filename=" + new String(("下载文件.zip").getBytes("GB2312"), "ISO_8859_1"));
		OutputStream os = rsp.getOutputStream();
		FileInputStream fis = new FileInputStream(rarfname);
		try {
			byte[] b = new byte[1024];
			int i = 0;
			while ((i = fis.read(b)) > 0) {
				os.write(b, 0, i);
			}
			fis.close();
			os.flush();
			os.close();
		} catch (Exception e) {
			e.printStackTrace();
			fis.close();
			os.flush();
			os.close();
		}
		File f = new File(bootpath);
		CFileUtiil.delete(f);
	}

	private static void downfolder(String fdrid) throws Exception {
		Shwfdr fdr = new Shwfdr();
		fdr.findByID(fdrid);
		if (fdr.isEmpty()) {
			throw new Exception("文件夹ID【" + fdrid + "】不存在");
		}
		String sqlstr = "select * from shwfdr where actived=1 and fidpath like '" + fdr.fidpath.getValue() + "%'";
		JSONArray ftree = DBPools.defaultPool().opensql2jsontree_o(sqlstr, "fdrid", "superid", false);
		String un = (CSContext.getUserNameEx() == null) ? "SYSTEM" : CSContext.getUserNameEx();
		String fsep = System.getProperty("file.separator");
		String rootfstr = ConstsSw.geAppParmStr("UDFilePath") + "temp" + fsep + un + "_" + Systemdate.getStrDateByFmt(new Date(), "hhmmss")
				+ fsep;
		File file = new File(rootfstr);
		if (!file.exists() && !file.isDirectory()) {
			file.mkdir();
		}
		createTemFile(ftree, rootfstr, fsep);
		String rarfname = rootfstr + fdr.fdrname.getValue() + ".zip";
		CFileUtiil.zipFold(rarfname, rootfstr + fdr.fdrname.getValue() + fsep);

		HttpServletResponse rsp = CSContext.getResponse();
		rsp.setContentType(getContentType(rarfname));
		rsp.setHeader("content-disposition", "attachment; filename=" + new String((fdr.fdrname.getValue() + ".zip").getBytes("GB2312"), "ISO_8859_1"));
		OutputStream os = rsp.getOutputStream();
		FileInputStream fis = new FileInputStream(rarfname);
		try {
			byte[] b = new byte[1024];
			int i = 0;
			while ((i = fis.read(b)) > 0) {
				os.write(b, 0, i);
			}
			fis.close();
			os.flush();
			os.close();
		} catch (Exception e) {
			e.printStackTrace();
			fis.close();
			os.flush();
			os.close();
		}
		File f = new File(rootfstr);
		CFileUtiil.delete(f);
	}

	private static String getContentType(String pathToFile) {
		Path path = Paths.get(pathToFile);
		try {
			return Files.probeContentType(path);
		} catch (IOException e) {
			return "application/text";
		}

	}

	private static void createTemFile(JSONArray ftree, final String rootfstr, String fsep) throws Exception {
		for (int i = 0; i < ftree.size(); i++) {
			JSONObject fnode = ftree.getJSONObject(i);
			crtNodeFile(fnode, rootfstr, fsep);
		}
	}

	private static void crtNodeFile(JSONObject fnode, final String fdstr, String fsep) throws Exception {
		String nfdstr = fdstr + fnode.getString("fdrname") + fsep;
		fnode.put("tpath", nfdstr);
		File file = new File(nfdstr);
		if (!(file.exists() && file.isDirectory())) {
			file.mkdirs();
		}
		copyfoldfile(fnode.getString("fdrid"), nfdstr);
		if (fnode.has("children")) {
			JSONArray nodes = fnode.getJSONArray("children");
			for (int i = 0; i < nodes.size(); i++) {
				JSONObject node = nodes.getJSONObject(i);
				crtNodeFile(node, nfdstr, fsep);
			}
		}
	}

	private static void copyfoldfile(String fdrid, String fpath) throws Exception {
		String sqlstr = "SELECT f.* FROM shw_physic_file f,shw_physic_file_refer fr WHERE f.pfid=fr.pfid AND fr.pfrtype=1 AND fr.referid =" + fdrid;
		CJPALineData<Shw_physic_file> pfs = new CJPALineData<Shw_physic_file>(Shw_physic_file.class);
		pfs.findDataBySQL(sqlstr);
		String fs = System.getProperty("file.separator");
		for (CJPABase jpa : pfs) {
			Shw_physic_file pf = (Shw_physic_file) jpa;
			String fullname = ConstsSw.geAppParmStr("UDFilePath") + fs + pf.ppath.getValue() + fs + pf.pfname.getValue();
			if (!(new File(fullname)).exists()) {
				fullname = ConstsSw._root_filepath + "attifiles" + fs + pf.ppath.getValue() + fs + pf.pfname.getValue();
				if (!(new File(fullname)).exists())
					throw new Exception("文件" + fullname + "不存在!");
			}
			String nfname = fpath + pf.displayfname.getValue();
			File file = new File(nfname);
			if ((file.exists()) && (file.isFile())) {
				if (!file.delete())
					throw new Exception("删除文件错误【" + nfname + "】");
			}
			RandomAccessFile outfile = new RandomAccessFile(nfname, "rw");
			forTransfer(new RandomAccessFile(fullname, "r"), outfile);
		}

	}

	private static void forTransfer(RandomAccessFile f1, RandomAccessFile f2) throws Exception {
		final int len = 20971520;
		int curPosition = 0;
		FileChannel inC = f1.getChannel();
		FileChannel outC = f2.getChannel();
		while (true) {
			if (inC.position() == inC.size()) {
				inC.close();
				outC.close();
				break;
			}
			if ((inC.size() - inC.position()) < len)
				curPosition = (int) (inC.size() - inC.position());
			else
				curPosition = len;
			inC.transferTo(inC.position(), curPosition, outC);
			inC.position(inC.position() + curPosition);
		}
	}

	// ///////////下载文件 end

	/*
	 * 查询 文件引用情况 1、文件在指定文件夹中 2、引用不在指定文件夹中 3、包括非文件夹的引用
	 */
	public static JSONArray findPFileRefNotInFdr(Shwfdr fdr) throws Exception {
		String sqlstr = String.format(_sqlstr1, fdr.fidpath.getValue(), fdr.fidpath.getValue(), fdr.fidpath.getValue());
		JSONArray rst = DBPools.defaultPool().opensql2json_O(sqlstr);
		return rst;
	}

	public static void Paste(int acttp, String dfdrid, String rfdrid, JSONArray sinf) throws Exception {
		Shwfdr fdr = new Shwfdr();
		fdr.findByID(dfdrid);
		if (fdr.isEmpty())
			throw new Exception("没有发现ID为【" + dfdrid + "】的文件夹");

		for (int i = 0; i < sinf.size(); i++) {
			JSONObject jfd = sinf.getJSONObject(i);
			int type = jfd.getInt("type");
			if (type == 1) {// floder
				String sfdrid = jfd.getString("fdrid");
				PasteFolder(sfdrid, dfdrid);
				if (acttp == ACTION_COPY_CUT_TYPE_CUT) {
					Shwfdr sfdr = new Shwfdr();
					sfdr.findByID(sfdrid);
					if (!sfdr.isEmpty())
						removeFolder(sfdr);
				}
			} else if (type == 2) {// file
				String pfid = jfd.getString("pfid");
				PasteFile(pfid, dfdrid);
				if (acttp == ACTION_COPY_CUT_TYPE_CUT) {
					Shwfdr rfdr = new Shwfdr();
					rfdr.findByID(rfdrid);
					if (rfdr.isEmpty())
						throw new Exception("没有发现ID为【" + rfdrid + "】的文件夹");
					revomeFolderFile(rfdr, pfid);
				}
			} else
				throw new Exception("复制文件Type参数错误");
		}

	}

	public static void Paste(int acttp, int fdrorfile, String sfdrid, String sfileid, String dfdrid) throws Exception {
		Shwfdr fdr = new Shwfdr();
		fdr.findByID(dfdrid);
		if (fdr.isEmpty())
			throw new Exception("没有发现ID为【" + dfdrid + "】的文件夹");

		JSONArray sfids = null;

		if (fdrorfile == PASTE_STYPE_FOLDER) {
			PasteFolder(sfdrid, dfdrid);
		} else if (fdrorfile == PASTE_STYPE_FILE) {
			sfids = JSONArray.fromObject(sfileid);
			for (int i = 0; i < sfids.size(); i++) {
				PasteFile(sfids.getJSONObject(i).getString("pfid"), dfdrid);
			}
		} else {
			throw new Exception("粘贴数据源错误");
		}
		if (acttp == ACTION_COPY_CUT_TYPE_COPY) {
			return;
		} else if (acttp == ACTION_COPY_CUT_TYPE_CUT) {
			if (fdrorfile == PASTE_STYPE_FOLDER) {// 删除源文件夹
				Shwfdr sfdr = new Shwfdr();
				sfdr.findByID(sfdrid);
				if (!sfdr.isEmpty())
					removeFolder(sfdr);
			}
			if (fdrorfile == PASTE_STYPE_FILE) {// 删除源文件
				Shwfdr sfdr = new Shwfdr();
				sfdr.findByID(sfdrid);
				if (!sfdr.isEmpty()) {
					for (int i = 0; i < sfids.size(); i++) {
						revomeFolderFile(sfdr, sfids.getJSONObject(i).getString("pfid"));
					}
				}
			}
		} else {
			throw new Exception("粘贴参数错误");
		}

	}

	// 复制文件夹 包含子文件
	public static void PasteFolder(String sfdrid, String dfdrid) throws Exception {
		Shwfdr dfdr = new Shwfdr();
		dfdr.findByID(dfdrid);
		if (dfdr.isEmpty())
			throw new Exception("目标文件夹【" + dfdrid + "】不存在");
		Shwfdr sfdr = new Shwfdr();
		sfdr.findByID(sfdrid);
		if (sfdr.isEmpty())
			throw new Exception("源文件夹【" + sfdrid + "】不存在");
		String sqlstr = "select * from shwfdr a where actived=1 and a.fidpath like '" + sfdr.fidpath.getValue() + "%'";

		JSONArray sjfdr = sfdr.pool.opensql2jsontree_o(sqlstr, "fdrid", "superid", false);
		for (int i = 0; i < sjfdr.size(); i++) {
			JSONObject node = sjfdr.getJSONObject(i);
			PasteFolder(node, dfdr);
		}
	}

	// 复制文件夹 包含子文件
	public static void PasteFolder(JSONObject snode, Shwfdr dfdr) throws Exception {
		Shwfdr nfdr = createNewFolder(dfdr, snode.getString("fdrname"));// 创建新的
		String sqlstr = "SELECT f.* FROM shw_physic_file f,shw_physic_file_refer fr WHERE f.pfid=fr.pfid AND fr.pfrtype=1 AND fr.referid ="
				+ snode.getString("fdrid");
		CJPALineData<Shw_physic_file> pfs = new CJPALineData<Shw_physic_file>(Shw_physic_file.class);
		pfs.findDataBySQL(sqlstr);// 源文件夹下面的所有文件 不包含子文件夹
		for (CJPABase jpa : pfs) {
			Shw_physic_file pf = (Shw_physic_file) jpa;
			PasteFile(pf.pfid.getValue(), nfdr.fdrid.getValue());
		}
		if (snode.has("children")) {
			JSONArray nodes = snode.getJSONArray("children");
			for (int i = 0; i < nodes.size(); i++) {
				JSONObject node = nodes.getJSONObject(i);
				PasteFolder(node, nfdr);
			}
		}
	}

	// 粘贴文件到文件夹
	public static void PasteFile(String pfid, String fdrid) throws Exception {
		String sqlstr = "select count(*) ct from shw_physic_file_refer where pfrtype=1 and referid=" + fdrid + " and pfid=" + pfid;
		if (Integer.valueOf(DBPools.defaultPool().openSql2List(sqlstr).get(0).get("ct").toString()) != 0) {
			return;
		}
		Shw_physic_file_refer pfr = new Shw_physic_file_refer();
		pfr.referid.setValue(fdrid);
		pfr.pfrtype.setAsInt(CtrlDoc.FILEREF_FOLDER);
		pfr.pfid.setValue(pfid);
		pfr.save();
	}

	// 在pfdr下新建名称为name的文件夹，并返回对象
	public static Shwfdr createNewFolder(Shwfdr pfdr, String name) throws Exception {
		Shwfdr nfdr = new Shwfdr();
		String[] fields = { "fdtype", "entid", "clas", "flag", "inspect", "issecret" };
		nfdr.assignfield(pfdr, true, fields);
		nfdr.createtime.setAsDatetime(new Date());
		nfdr.creator.setValue(CSContext.getUserDisplayname());
		nfdr.actived.setAsInt(1);
		CDBConnection con = pfdr.pool.getCon("CtrlDoc");
		try {
			nfdr.checkOrCreateIDCode(con, true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			con.close();
		}
		nfdr.superid.setValue(pfdr.fdrid.getValue());
		nfdr.fidpath.setValue(pfdr.fidpath.getValue() + nfdr.fdrid.getValue() + ",");
		nfdr.fdrname.setValue(name);
		nfdr.sysbuildin.setAsInt(2);
		nfdr.save();
		return nfdr;
	}

	// 删除文件夹 不检查权限
	public static void removeFolder(Shwfdr fdr) throws Exception {
		ArrayList<String> sqls = new ArrayList<String>();
		// 删除所有权限
		sqls.add("DELETE FROM shwfdracl WHERE acltype IN(1,3) AND objid IN(SELECT f.fdrid FROM shwfdr f WHERE f.fidpath LIKE '" + fdr.fidpath.getValue()
				+ "%')");
		// 删除所有文件
		removeFolderFiles(fdr);
		// 删除所有文件夹
		sqls.add("DELETE FROM shwfdr WHERE fidpath LIKE '" + fdr.fidpath.getValue() + "%'");
		DBPools.defaultPool().execSqls(sqls);
	}

	/*
	 * 删除文件夹里面的所有文件，包括子文件 1、删除该文件夹对文件的引用； 2、检查被引用的文件的引用数量，数量为0的时候删除物理文件记录及文件
	 */
	private static void removeFolderFiles(Shwfdr fdr) throws Exception {
		String sqlstr = " SELECT f.* FROM shw_physic_file f,shw_physic_file_refer fr, shwfdr fd "
				+ "WHERE f.pfid=fr.pfid AND fr.pfrtype=1 AND fr.referid=fd.fdrid AND fd.fidpath LIKE '" + fdr.fidpath.getValue() + "%' ";
		List<HashMap<String, String>> fls = DBPools.defaultPool().openSql2List(sqlstr);
		sqlstr = "DELETE FROM shw_physic_file_refer"
				+ " WHERE pfrid IN("
				+ " SELECT pfrid FROM (SELECT fr.pfrid FROM shw_physic_file f,shw_physic_file_refer fr, shwfdr fd "
				+ "  WHERE f.pfid=fr.pfid AND fr.pfrtype=1 AND fr.referid=fd.fdrid AND fd.fidpath LIKE '" + fdr.fidpath.getValue() + "%') tb "
				+ " )";
		DBPools.defaultPool().execsql(sqlstr);
		for (HashMap<String, String> fl : fls) {
			sqlstr = "SELECT COUNT(*) ct FROM shw_physic_file_refer fr WHERE fr.pfid=" + fl.get("pfid").toString();
			if (Integer.valueOf(DBPools.defaultPool().openSql2List(sqlstr).get(0).get("ct").toString()) == 0)
				UpLoadFileEx.delAttFile(fl.get("pfid").toString());
		}
	}

	/*
	 * 删除文件夹里面的單個文件，包括子文件 1、删除该文件夹对文件的引用； 2、检查被引用的文件的引用数量，数量为0的时候删除物理文件记录及文件
	 */
	public static void revomeFolderFile(Shwfdr fdr, String pfid) throws Exception {
		String sqlstr = "DELETE FROM shw_physic_file_refer WHERE pfrtype=1 AND pfid=" + pfid + " AND referid=" + fdr.fdrid.getValue();
		DBPools.defaultPool().execsql(sqlstr);
		sqlstr = "SELECT COUNT(*) ct FROM shw_physic_file_refer fr WHERE fr.pfid=" + pfid;
		if (Integer.valueOf(DBPools.defaultPool().openSql2List(sqlstr).get(0).get("ct").toString()) == 0)
			UpLoadFileEx.delAttFile(pfid);
	}

}
