package com.corsair.server.genco;

import java.util.HashMap;

import net.sf.json.JSONObject;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPABase;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.JPAControllerBase;
import com.corsair.cjpa.util.CJPASqlUtil;
import com.corsair.cjpa.util.CjpaUtil;
import com.corsair.server.base.CSContext;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.cjpa.JPAController;
import com.corsair.server.generic.Shw_attach;
import com.corsair.server.generic.Shw_attach_line;
import com.corsair.server.generic.Shw_physic_file;
import com.corsair.server.retention.ACO;
import com.corsair.server.retention.ACOAction;
import com.corsair.server.util.CorUtil;
import com.corsair.server.util.UpLoadFileEx;

/**
 * 附件 CO
 * 
 * @author Administrator
 *
 */
@ACO(coname = "web.att")
public class COCShw_attach {
	@ACOAction(eventname = "getshw_attachbyid", Authentication = true, notes = "根据表单获取附件(Idpath控制)", ispublic = true)
	public String getJPA_attach() throws Exception {
		HashMap<String, String> parms = CSContext.getParms();
		String jpaclass = CorUtil.hashMap2Str(parms, "jpaclass", "需要参数jpaclass");
		String id = CorUtil.hashMap2Str(parms, "id", "需要参数id");
		CJPA jpa = (CJPA) CjpaUtil.newJPAObjcet(jpaclass);

		String rst = ((JPAController) jpa.getPublicControllerBase()).OnCCoGetAttach(id);
		if (rst != null) {
			return rst;
		}
		getJPAByID(jpa, id);
		CField attid = jpa.cfield("attid");
		if (attid == null)
			throw new Exception("表单无附件字段【attid】");

		Shw_attach att = new Shw_attach();
		att.findByID(attid.getValue());
		return att.tojson();
	}

	@ACOAction(eventname = "upLoadJPAAtt64", Authentication = true, notes = "上传表单附件BASE64", ispublic = true)
	public String upLoadJPAAtt64() throws Exception {
		HashMap<String, String> parms = CSContext.getParms();
		String jpaclass = CorUtil.hashMap2Str(parms, "jpaclass", "需要参数jpaclass");
		String id = CorUtil.hashMap2Str(parms, "id", "需要参数id");
		CJPA jpa = (CJPA) CjpaUtil.newJPAObjcet(jpaclass);
		String rst = ((JPAController) jpa.getPublicControllerBase()).OnCCoUploadAttach(id);
		if (rst != null) {
			return rst;
		}
		getJPAByID(jpa, id);
		CField attid = jpa.cfield("attid");
		if (attid == null)
			throw new Exception("表单无附件字段【attid】");
		Shw_attach att = new Shw_attach();
		if (!attid.isEmpty())
			att.findByID(attid.getValue());
		JSONObject opd = CSContext.parPostData2JSONObject();
		String extfname = CorUtil.getJSONValue(opd, "extfname", "需要参数extfname");
		String fdata = CorUtil.getJSONValue(opd, "fdata", "需要参数fdata");
		String fname = CorUtil.getJSONValue(opd, "fname");
		Shw_physic_file pf = UpLoadFileEx.douploadBase64File(fdata, fname, extfname);

		Shw_attach_line l = new Shw_attach_line();
		l.pfid.setValue(pf.pfid.getValue());
		l.fdrid.setAsInt(0);
		l.displayfname.setValue(pf.displayfname.getValue());
		l.extname.setValue(pf.extname.getValue());
		l.filesize.setValue(pf.filesize.getValue());
		l.filevision.setValue(pf.filevision.getValue());
		att.shw_attach_lines.add(l);

		att.save();
		if (attid.isEmpty()) {
			attid.setValue(att.attid.getValue());
			jpa.save();
		}
		return att.tojson();
	}

	@ACOAction(eventname = "upLoadJPAAtt", Authentication = true, notes = "上传表单附件", ispublic = true)
	public String upLoadJPAAtt() throws Exception {
		HashMap<String, String> parms = CSContext.getParms();
		String jpaclass = CorUtil.hashMap2Str(parms, "jpaclass", "需要参数jpaclass");
		String id = CorUtil.hashMap2Str(parms, "id", "需要参数id");
		String imgthbstr = CorUtil.hashMap2Str(parms, "imgthb");
		String filetitle = CorUtil.hashMap2Str(parms, "filetitle");
		boolean imgthb = (imgthbstr == null) ? false : Boolean.valueOf(imgthbstr);
		CJPA jpa = (CJPA) CjpaUtil.newJPAObjcet(jpaclass);
		if (jpa.getPublicControllerBase() != null) {
			String rst = ((JPAController) jpa.getPublicControllerBase()).OnCCoUploadAttach(id);
			if (rst != null) {
				return rst;
			}
		}
		JPAControllerBase ctr = jpa.getController();
		if (ctr != null) {
			String rst = ((JPAController) ctr).OnCCoUploadAttach(id);
			if (rst != null) {
				return rst;
			}
		}
		getJPAByID(jpa, id);
		CField attid = jpa.cfield("attid");
		if (attid == null)
			throw new Exception("表单无附件字段【attid】");
		Shw_attach att = new Shw_attach();
		if (!attid.isEmpty())
			att.findByID(attid.getValue());

		CJPALineData<Shw_physic_file> pfs = UpLoadFileEx.doupload(imgthb, filetitle);

		for (CJPABase pfb : pfs) {
			Shw_physic_file f = (Shw_physic_file) pfb;
			Shw_attach_line l = new Shw_attach_line();
			l.pfid.setValue(f.pfid.getValue());
			l.fdrid.setAsInt(0);
			l.displayfname.setValue(f.displayfname.getValue());
			l.extname.setValue(f.extname.getValue());
			l.filesize.setValue(f.filesize.getValue());
			l.filevision.setValue(f.filevision.getValue());
			att.shw_attach_lines.add(l);
		}
		att.save();
		if (attid.isEmpty()) {
			attid.setValue(att.attid.getValue());
			jpa.save();
		}
		return att.tojson();
	}

	private void getJPAByID(CJPA jpa, String id) throws Exception {
		String where = "";
		if (jpa.cfield("idpath") != null)
			where = where + CSContext.getIdpathwhere();
		CField idfd = jpa.getIDField();
		if (idfd == null) {
			throw new Exception("根据ID查询JPA<" + jpa.getClass().getSimpleName() + ">数据没发现ID字段");
		}
		String sqlfdname = CJPASqlUtil.getSqlField(jpa.pool.getDbtype(), idfd.getFieldname());
		String sqlvalue = CJPASqlUtil.getSqlValue(jpa.pool.getDbtype(), idfd.getFieldtype(), id);
		String sqlstr = "select * from " + jpa.tablename + " where " + sqlfdname + "=" + sqlvalue;
		jpa.findBySQL(sqlstr, true);
		if (jpa.isEmpty())
			throw new Exception("权限范围内无此表单数据!");
	}
}
