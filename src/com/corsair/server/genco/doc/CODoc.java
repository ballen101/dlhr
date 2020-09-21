package com.corsair.server.genco.doc;

//http://demo.kalcaddle.com/index.php

import java.util.HashMap;
import com.corsair.cjpa.CJPABase;
import com.corsair.cjpa.CJPALineData;
import com.corsair.dbpool.CDBConnection;
import com.corsair.dbpool.DBPools;
import com.corsair.dbpool.util.Systemdate;
import com.corsair.server.base.CSContext;
import com.corsair.server.ctrl.CtrlDoc;
import com.corsair.server.ctrl.CtrlDoc.SACL;
import com.corsair.server.generic.Shw_physic_file;
import com.corsair.server.generic.Shw_physic_file_refer;
import com.corsair.server.generic.Shwfdr;
import com.corsair.server.generic.Shwfdracl;
import com.corsair.server.generic.Shworg;
import com.corsair.server.generic.Shwuser;
import com.corsair.server.retention.ACO;
import com.corsair.server.retention.ACOAction;
import com.corsair.server.util.CorUtil;
import com.corsair.server.util.UpLoadFileEx;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

/**
 * 文档CO
 * 
 * @author Administrator
 *
 */
@ACO(coname = "web.doc")
public class CODoc {
	private static int PUBLIC_ORG_FOLDER = 1;// 公共目录
	private static int USER_FOLDER = 2;// 个人目录
	private static int SHARE_FOLDER = 3;// 分享目录
	private static int RECYCLE_FOLDER = 4;// 回收站目录

	@ACOAction(eventname = "getDocFolders", Authentication = true, notes = "", ispublic = true)
	public String getDocFolders() throws Exception {
		String id = CorUtil.hashMap2Str(CSContext.getParms(), "id");
		int superid = ((id == null) || (id.isEmpty())) ? 0 : Integer.valueOf(id);
		JSONArray rst = CtrlDoc.getFoldersBySuperid(superid);
		return rst.toString();
	}

	@ACOAction(eventname = "createFloder", Authentication = true, notes = "", ispublic = true)
	public String createFloder() throws Exception {
		HashMap<String, String> parms = CSContext.parPostDataParms();
		String id = CorUtil.hashMap2Str(parms, "id", "需要参数ID");
		String name = CorUtil.hashMap2Str(parms, "name", "需要参数name");
		Shwfdr fdr = new Shwfdr();
		fdr.findByID(id);
		if (fdr.isEmpty()) {
			throw new Exception("文件夹【" + id + "】不存在 ");
		}
		// 权限检查
		SACL acl = CtrlDoc.getCurAclALL(fdr.toJsonObj(), 2);
		if (!((acl == SACL.ADMIN) || (acl == SACL.WD) || (acl == SACL.W)))
			throw new Exception("文件夹权限不足");
		return CtrlDoc.createNewFolder(fdr, name).tojson();
	}

	@ACOAction(eventname = "removeFolderOrFiles", Authentication = true, notes = "", ispublic = true)
	public String removeFolderOrFiles() throws Exception {
		JSONObject dinfo = JSONObject.fromObject(CSContext.getPostdata());
		String rfdrid = dinfo.getString("rfdrid");
		JSONArray rfs = dinfo.getJSONArray("data");
		Shwfdr rfdr = new Shwfdr();
		rfdr.findByID(rfdrid);
		if (rfdr.isEmpty())
			throw new Exception("文件夹【" + rfdrid + "】不存在 ");
		for (int i = 0; i < rfs.size(); i++) {
			JSONObject rf = rfs.getJSONObject(i);
			int type = rf.getInt("type");
			if (type == 1) {// folder
				String fdrid = rf.getString("fdrid");
				Shwfdr fdr = new Shwfdr();
				fdr.findByID(fdrid);
				if (fdr.isEmpty())
					throw new Exception("文件夹【" + fdrid + "】不存在 ");
				// 权限检查
				CtrlDoc.removeFolder(fdr);
			} else if (type == 2) {// file
				String pfid = rf.getString("pfid");
				CtrlDoc.revomeFolderFile(rfdr, pfid);
			} else
				throw new Exception("");

		}
		return "{\"result\":\"OK\"}";
	}

	/*
	 * @ACOAction(eventname = "getFolderAcl", Authentication = true, notes =
	 * "获取当前登陆用户文件夹或文件权限", ispublic = true) public String getFolderAcl() throws
	 * Exception { String id = CorUtil.hashMap2Str(CSContext.getParms(), "id",
	 * "需要参数id"); // 1 文件权限 2 文件夹权限 int atp =
	 * Integer.valueOf(CorUtil.hashMap2Str(CSContext.getParms(), "atp",
	 * "需要参数atp")); // SACL acl = CtrlDoc.getCurAclALL(id, atp); // return
	 * "{\"result\":\"" + acl.ordinal() + "\"}"; return null; }
	 */

	@ACOAction(eventname = "getFolderAcled", Authentication = true, notes = "获取当前文件夹权限授予情况", ispublic = true)
	public String getFolderAcled() throws Exception {
		String id = CorUtil.hashMap2Str(CSContext.getParms(), "id", "需要参数id");
		// 1 文件权限 2 文件夹权限
		int atp = Integer.valueOf(CorUtil.hashMap2Str(CSContext.getParms(), "atp", "需要参数atp"));
		String sqlstr = null;
		if (atp == 1) {
			sqlstr = "SELECT * FROM shwfdracl a WHERE a.objid=" + id + " AND ((a.acltype=2)OR(a.acltype=4))";
		} else if (atp == 2) {
			sqlstr = "SELECT * FROM shwfdracl a WHERE a.objid=" + id + " AND ((a.acltype=1)OR(a.acltype=3))";
		} else {
			throw new Exception("参数错误,atp为1或2");
		}

		Shwfdracl acl = new Shwfdracl();
		return acl.pool.opensql2json(sqlstr);
	}

	@ACOAction(eventname = "setFolderAcl", Authentication = true, notes = "当前登陆用户给文件夹或文件授权", ispublic = true)
	public String setFolderAcl() throws Exception {
		JSONArray parms = (JSONArray) JSONSerializer.toJSON(CSContext.getPostdata());
		for (int i = 0; i < parms.size(); i++) {
			// 文件夹ID，文件ID
			// 机构ID 或 用户ID
			// 权限类型 1:文件夹对机构 2 文件对机构 3 文件夹对个人 4文件对个人
			// 权限 1 修改 2 删除3 路由不需要授权
			JSONObject parm = parms.getJSONObject(i);
			String ownername = null;
			String ownerid = parm.getString("ownerid");
			int acltype = Integer.valueOf(parm.getInt("acltype"));
			if ((acltype == 1) || (acltype == 2)) {
				Shworg org = new Shworg();
				org.findByID(ownerid);
				if (org.isEmpty())
					throw new Exception("机构【" + ownerid + "】没发现");
				ownername = org.orgname.getValue();
			} else if ((acltype == 3) || (acltype == 4)) {
				Shwuser user = new Shwuser();
				user.findByID(ownerid);
				if (user.isEmpty())
					throw new Exception("用户【" + ownerid + "】没发现");
				ownername = user.username.getValue();
			} else {
				throw new Exception("参数错误");
			}
			CtrlDoc.newacl(parm.getString("id"), ownerid, ownername, acltype, SACL.values()[parm.getInt("access")],
					Systemdate.getDateByStr(parm.getString("statime")),
					Systemdate.getDateByStr(parm.getString("endtime")));
		}
		return "{\"result\":\"OK\"}";
	}

	@ACOAction(eventname = "removeFolderAcl", Authentication = true, notes = "当前登陆用户取消文件夹或文件授权", ispublic = true)
	public String removeFolderAcl() throws Exception {
		JSONArray parms = (JSONArray) JSONSerializer.toJSON(CSContext.getPostdata());
		for (int i = 0; i < parms.size(); i++) {
			JSONObject parm = parms.getJSONObject(i);
			String aclid = parm.getString("aclid");
			Shwfdracl acl = new Shwfdracl();
			acl.findByID(aclid);
			if (acl.isEmpty()) {
				throw new Exception("权限【" + aclid + "】不存在");
			}
			int acltype = acl.acltype.getAsInt();
			if ((acltype == 3) || (acltype == 4)) {
				if (acl.ownerid.getValue().equals(CSContext.getUserID())) {
					throw new Exception("不能删除给自己的授权");
				}
			}
			// 检查是否有管理员权限
			acl.delete();
		}
		return "{\"result\":\"OK\"}";
	}

	@ACOAction(eventname = "getFolderAttr", Authentication = true, notes = "", ispublic = true)
	public String getFolderAttr() throws NumberFormatException, Exception {
		HashMap<String, String> parms = CSContext.getParms();
		int acltype = Integer.valueOf(CorUtil.hashMap2Str(parms, "type", "需要参数type"));
		String fdrid = CorUtil.hashMap2Str(parms, "id", "需要参数type");
		return CtrlDoc.getFolderAttr(acltype, fdrid);
	}

	@ACOAction(eventname = "UpdateFloderName", Authentication = true, notes = "", ispublic = true)
	public String UpdateFloderName() throws Exception {
		HashMap<String, String> parms = CSContext.parPostDataParms();
		String id = CorUtil.hashMap2Str(parms, "id", "需要参数ID");
		String name = CorUtil.hashMap2Str(parms, "name", "需要参数name");
		Shwfdr fdr = new Shwfdr();
		fdr.findByID(id);
		if (fdr.isEmpty()) {
			throw new Exception("文件夹【" + id + "】不存在 ");
		}
		// 权限检查
		SACL acl = CtrlDoc.getCurAclALL(fdr.toJsonObj(), 2);
		if (!((acl == SACL.ADMIN) || (acl == SACL.WD) || (acl == SACL.W)))
			throw new Exception("文件夹权限不足");
		if (name.equals(fdr.fdrname.getValue())) {
			throw new Exception("文件名相同");
		}
		fdr.fdrname.setValue(name);
		fdr.save();
		return fdr.tojson();
	}

	@ACOAction(eventname = "UpdateFileName", Authentication = true, notes = "", ispublic = true)
	public String UpdateFileName() throws Exception {
		HashMap<String, String> parms = CSContext.parPostDataParms();
		String fdrid = CorUtil.hashMap2Str(parms, "fdrid", "需要参数fdrid");
		String pfid = CorUtil.hashMap2Str(parms, "pfid", "需要参数pfid");
		String name = CorUtil.hashMap2Str(parms, "name", "需要参数name");
		Shwfdr fdr = new Shwfdr();
		fdr.findByID(fdrid);
		if (fdr.isEmpty()) {
			throw new Exception("文件夹【" + fdrid + "】不存在 ");
		}
		// 权限检查
		SACL acl = CtrlDoc.getCurAclALL(fdr.toJsonObj(), 2);
		if (!((acl == SACL.ADMIN) || (acl == SACL.WD) || (acl == SACL.W)))
			throw new Exception("文件夹权限不足");

		UpLoadFileEx.updateDisplayName(pfid, name);
		Shw_physic_file pf = new Shw_physic_file();
		return ((Shw_physic_file) pf.findByID(pfid)).tojson();
	}

	@ACOAction(eventname = "uploadfile", Authentication = true)
	public String uploadfile() throws Exception {
		if (!CSContext.isMultipartContent())
			throw new Exception("没有文件");
		CJPALineData<Shw_physic_file> pfs = UpLoadFileEx.doupload(true);
		CDBConnection con = DBPools.defaultPool().getCon("CODoc.uploadfile");
		con.startTrans();
		try {
			HashMap<String, String> parms = CSContext.getParms();
			String fdrid = CorUtil.hashMap2Str(parms, "fdrid", "没有选择文件夹");
			for (CJPABase jpa : pfs) {
				Shw_physic_file pf = (Shw_physic_file) jpa;
				Shw_physic_file_refer pfr = new Shw_physic_file_refer();
				pfr.referid.setValue(fdrid);
				pfr.pfrtype.setAsInt(CtrlDoc.FILEREF_FOLDER);
				pfr.pfid.setValue(pf.pfid.getValue());
				pfr.save(con);
			}
			con.submit();
			return pfs.tojson();
		} catch (Exception e) {
			con.rollback();
			removeallfls(pfs);
			throw e;
		}
	}

	private void removeallfls(CJPALineData<Shw_physic_file> pfs) throws Exception {
		if (pfs.size() > 0) {
			for (CJPABase pfb : pfs) {
				Shw_physic_file pf = (Shw_physic_file) pfb;
				UpLoadFileEx.delAttFile(pf.pfid.getValue());
			}
		}
	}

	@ACOAction(eventname = "getFolderDocs", Authentication = true, ispublic = true)
	public String getFolderDocs() throws Exception {
		HashMap<String, String> parms = CSContext.getParms();
		String fdrid = CorUtil.hashMap2Str(parms, "fdrid", "没有选择文件夹");

		JSONArray folders = CtrlDoc.getFoldersBySuperid(Integer.valueOf(fdrid));
		String sqlstr = "SELECT f.* FROM shw_physic_file f,shw_physic_file_refer fr WHERE f.pfid=fr.pfid AND fr.pfrtype=1 AND fr.referid=" + fdrid;

		JSONArray files = DBPools.defaultPool().opensql2json_O(sqlstr);

		for (int i = 0; i < folders.size(); i++) {
			folders.getJSONObject(i).put("type", 1);
		}
		for (int i = 0; i < files.size(); i++) {
			files.getJSONObject(i).put("type", 2);
			folders.add(files.getJSONObject(i));
		}

		return folders.toString();
	}

	// @ACOAction(eventname = "downloadFolder", Authentication = true, ispublic
	// = true)
	// public String downloadFolder() throws Exception {
	// HashMap<String, String> parms = CSContext.getParms();
	// String fdrid = CorUtil.hashMap2Str(parms, "fdrid", "没有选择文件夹");
	// CtrlDoc.downfolder(fdrid);
	// return null;
	// }

	@ACOAction(eventname = "downloadFiles", Authentication = true, ispublic = true)
	public String downloadFiles() throws Exception {
		HashMap<String, String> parms = CSContext.getParms();
		String dinfo = CorUtil.hashMap2Str(parms, "dinfo", "需要参数【dinfo】");
		JSONArray dinf = JSONArray.fromObject(dinfo);
		if (dinf.size() == 0) {
			throw new Exception("没有选择下载的文件！");
		}
		CtrlDoc.downfiles(dinf);
		return null;
	}

	// inpaste: false,
	// rfdrid: undefined,//如果是剪切 剪切后需要刷新的文件夹
	// cporct: undefined,//复制还是剪切
	// sinfo: undefined,//复制的文件和文件夹
	// dfdrid: undefined//目标文件夹id
	@ACOAction(eventname = "Paste", Authentication = true, ispublic = true)
	public String Paste() throws Exception {
		HashMap<String, String> parms = CSContext.parPostDataParms();
		int acttp = Integer.valueOf(CorUtil.hashMap2Str(parms, "cporct", "需要参数cporct"));
		String sinfo = CorUtil.hashMap2Str(parms, "sinfo", "需要参数sinfo");// 源文件夹ID
		String dfdrid = CorUtil.hashMap2Str(parms, "dfdrid", "需要参数dfdrid");// 目标文件夹
		String rfdrid = CorUtil.hashMap2Str(parms, "rfdrid");// 目标文件夹
		JSONArray sinf = JSONArray.fromObject(sinfo);
		if (sinf.size() == 0) {
			throw new Exception("没有选择复制的文件！");
		}
		CtrlDoc.Paste(acttp, dfdrid, rfdrid, sinf);
		return "{\"result\":\"OK\"}";
	}

}
