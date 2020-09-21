package com.corsair.server.baiduueditor;

import java.util.HashMap;
import java.util.Map;

import com.corsair.cjpa.CJPALineData;
import com.corsair.server.base.CSContext;
import com.corsair.server.generic.Shw_physic_file;
import com.corsair.server.generic.Shworg;
import com.corsair.server.generic.Shworgfile;
import com.corsair.server.util.CorUtil;
import com.corsair.server.util.UpLoadFileEx;

/**
 * 文件上传者
 * 
 * @author Administrator
 *
 */
public class Uploader {
	public final static State doupdate(Map<String, Object> conf, int actionCode) {
		State state = null;
		try {
			String filedName = (String) conf.get("fieldName");
			if ("true".equals(conf.get("isBase64"))) {
				// state = Base64Uploader.save(this.request.getParameter(filedName), this.conf);
			} else {
				state = doBinaryUoload(conf, actionCode);
				// state = BinaryUploader.save(this.conf);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new BaseState(false, e.getMessage());
		}
		if (state == null)
			return new BaseState(false, "未知错误");
		else
			return state;
	}

	private static State doBinaryUoload(Map<String, Object> conf, int actionCode) throws Exception {
		HashMap<String, String> urlparms = CSContext.getParms();
		String orgid = CorUtil.hashMap2Str(urlparms, "orgid", "需要orgid");
		Shworg org = new Shworg();
		org.findByID(orgid);
		if (org.isEmpty())
			throw new Exception("机构不存在");
		long maxSize = ((Long) conf.get("maxSize")).longValue();
		String[] allowFiles = (String[]) conf.get("allowFiles");
		CJPALineData<Shw_physic_file> pfs = UpLoadFileEx.doupload(false, null, allowFiles, maxSize);
		if (pfs.size() <= 0)
			return new BaseState(false, AppInfo.NOT_MULTIPART_CONTENT);
		if (pfs.size() > 1) {// 太多文件只留第一个
			for (int i = 1; i < pfs.size(); i++) {
				Shw_physic_file pf = (Shw_physic_file) pfs.get(i);
				UpLoadFileEx.delAttFile(pf.pfid.getValue());
			}
		}
		Shw_physic_file pf = (Shw_physic_file) pfs.get(0);
		Shworgfile of = new Shworgfile();
		of.pfid.setValue(pf.pfid.getValue()); // 物理文件ID
		of.orgid.setValue(org.orgid.getValue()); // 机构ID
		of.orgname.setValue(org.orgname.getValue()); // 机构名称
		of.idpath.setValue(org.idpath.getValue()); // idpath

		if (actionCode == ActionMap.UPLOAD_IMAGE)
			of.ptype.setAsInt(1); // 类型 扩展
		if (actionCode == ActionMap.UPLOAD_VIDEO)
			of.ptype.setAsInt(2); // 类型 扩展
		if (actionCode == ActionMap.UPLOAD_SCRAWL)
			of.ptype.setAsInt(3); // 类型 扩展
		if (actionCode == ActionMap.UPLOAD_FILE)
			of.ptype.setAsInt(4); // 类型 扩展

		of.entid.setAsInt(1); // entid
		of.save();
		State state = new BaseState(true);
		state.putInfo("size", pf.filesize.getAsLongDefault(0) * 1000);
		state.putInfo("title", pf.displayfname.getValue());
		state.putInfo("url", "");
		state.putInfo("pfid", pf.pfid.getValue());
		state.putInfo("type", pf.extname.getValue());
		state.putInfo("original", "");
		return state;
	}

}
