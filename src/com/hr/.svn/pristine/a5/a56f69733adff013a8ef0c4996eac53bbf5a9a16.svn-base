package com.hr.card.co;

import java.util.Date;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPABase;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.CJPABase.CJPAStat;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.cjpa.util.CjpaUtil;
import com.corsair.dbpool.CDBConnection;
import com.corsair.dbpool.DBPools;
import com.corsair.dbpool.util.CJSON;
import com.corsair.dbpool.util.JSONParm;
import com.corsair.dbpool.util.PraperedSql;
import com.corsair.dbpool.util.PraperedValue;
import com.corsair.dbpool.util.Systemdate;
import com.corsair.server.base.CSContext;
import com.corsair.server.base.ConstsSw;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.cjpa.JPAController;
import com.corsair.server.genco.COShwUser;
import com.corsair.server.generic.Shw_attach;
import com.corsair.server.generic.Shw_physic_file;
import com.corsair.server.generic.Shworg;
import com.corsair.server.generic.Shwuser;
import com.corsair.server.retention.ACO;
import com.corsair.server.retention.ACOAction;
import com.corsair.server.util.CExcelField;
import com.corsair.server.util.CExcelUtil;
import com.corsair.server.util.CSearchForm;
import com.corsair.server.util.CorUtil;
import com.corsair.server.util.DictionaryTemp;
import com.corsair.server.util.UpLoadFileEx;
import com.corsair.server.wordflow.Shwwf;
import com.corsair.server.wordflow.Shwwfproc;
import com.hr.access.co.COHr_access_emauthority_list;
import com.hr.access.ctr.UtilAccess;
import com.hr.asset.entity.Hr_asset_reject_h;
import com.hr.card.entity.Hr_ykt_card;
import com.hr.card.entity.Hr_ykt_card_clean;
import com.hr.card.entity.Hr_ykt_card_loss;
import com.hr.card.entity.Hr_ykt_card_publish;
import com.hr.perm.entity.Hr_employee;
import com.hr.card.co.COHr_ykt_card_clean;

@ACO(coname = "web.hr.Card")
public class COHr_ykt_card_loss extends JPAController {
	@Override
	public void AfterWFStart(CJPA jpa, CDBConnection con, Shwwf wf, boolean isFilished)
			throws Exception {
		// TODO Auto-generated method stub
		if (isFilished) {
			doupcardloss(jpa, con);
			doUpdateAcS(con, jpa);
		}
	}

	@Override
	public void OnWfSubmit(CJPA jpa, CDBConnection con, Shwwf wf, Shwwfproc proc,
			Shwwfproc nxtproc, boolean isFilished) throws Exception {
		// TODO Auto-generated method stub
		if (isFilished) {
			doupcardloss(jpa, con);
			doUpdateAcS(con, jpa);
		}
	}

	/**
	 * 离职自动挂失
	 * 
	 * @param con
	 * @param er_id
	 * @throws Exception
	 */
	public static void doLeaveLoss(CDBConnection con, String er_id, String remark) throws Exception {
		Hr_ykt_card_loss ycl = new Hr_ykt_card_loss();
		String sqlstr = "SELECT * FROM hr_ykt_card WHERE card_stat=1 AND er_id=" + er_id;
		Hr_ykt_card card = new Hr_ykt_card();
		card.findBySQL(con, sqlstr, false);
		if (card.isEmpty()) {
			return;// throw new Exception("无正在使用的卡，无法挂失");
		}
		Hr_employee he = new Hr_employee();
		he.findByID(con, er_id);
		if (he.isEmpty())
			return;

		ycl.employee_code.setValue(he.employee_code.getValue());
		ycl.employee_name.setValue(he.employee_name.getValue());
		ycl.card_number.setValue(he.card_number.getValue());
		ycl.er_id.setValue(he.er_id.getValue());
		ycl.er_code.setValue(he.er_code.getValue());
		ycl.orgid.setValue(he.orgid.getValue());
		ycl.orgcode.setValue(he.orgcode.getValue());
		ycl.orgname.setValue(he.orgname.getValue());
		ycl.sp_name.setValue(he.sp_name.getValue());
		ycl.hwc_namezl.setValue(he.hwc_namezl.getValue());
		ycl.hwc_namezq.setValue(he.hwc_namezq.getValue());
		ycl.hwc_namezz.setValue(he.hwc_namezz.getValue());
		ycl.hg_name.setValue(he.hg_name.getValue());
		ycl.lv_num.setValue(he.lv_num.getValue());
		ycl.idpath.setValue(he.idpath.getValue());
		ycl.card_id.setValue(card.card_id.getValue());
		ycl.card_sn.setValue(card.card_sn.getValue());
		ycl.card_number.setValue(card.card_number.getValue());
		ycl.card_stat.setValue(card.card_stat.getValue());
		ycl.remark.setValue(remark);
		ycl.save(con);
		ycl.wfcreate(null, con);// 自动提交
	}

	// 更新门禁权限列表
	public static void doUpdateAcS(CDBConnection con, CJPA jpa) throws Exception {
		Hr_ykt_card_loss ycl = new Hr_ykt_card_loss();
		String sqlstr = "UPDATE hr_access_emauthority_sum t" +
				"   SET t.access_status = '2'," +
				"       t.accrediter    = 'SYSTEM'," +
				"       t.accredit_date = NOW()," +
				"       t.attribute1 = 'Y'," +
				"       t.remarks       = '清卡关闭'" +
				" WHERE t.employee_code = '" + ycl.employee_code.getValue() + "'" +
				"   AND t.access_card_number = '" + ycl.card_number.getValue() + "' and t.access_status=1 ";
		con.execsql(sqlstr);
	}

	// 更新卡档案数据
	private void doupcardloss(CJPA jpa, CDBConnection con) throws Exception {
		Hr_employee he = new Hr_employee();
		Hr_ykt_card_loss ycl = (Hr_ykt_card_loss) jpa;
		Hr_ykt_card yc = new Hr_ykt_card();
		// Hr_employee he = new Hr_employee();
		String sqlstr = "SELECT * FROM hr_ykt_card WHERE card_id= " + ycl.card_id.getValue();
		yc.findBySQL(con, sqlstr, false);
		yc.card_number.setValue(ycl.card_number.getValue());
		yc.card_stat.setValue("2");
		yc.er_id.setValue(ycl.er_id.getValue());
		yc.er_code.setValue(ycl.er_code.getValue());
		yc.employee_code.setValue(ycl.employee_code.getValue());
		yc.employee_name.setValue(ycl.employee_name.getValue());
		yc.orgid.setValue(ycl.orgid.getValue());
		yc.orgcode.setValue(ycl.orgcode.getValue());
		yc.orgname.setValue(ycl.orgname.getValue());
		yc.sp_name.setValue(ycl.sp_name.getValue());
		yc.hwc_namezl.setValue(ycl.hwc_namezl.getValue());
		yc.hwc_namezq.setValue(ycl.hwc_namezq.getValue());
		yc.hwc_namezz.setValue(ycl.hwc_namezz.getValue());
		yc.hg_name.setValue(ycl.hg_name.getValue());
		yc.lv_num.setValue(ycl.lv_num.getValue());
		yc.disable_date.setAsDatetime(new Date());
		yc.save(con, false);
		// 清空人事档案中的卡信息
		String sqlstr1 = "SELECT * FROM hr_employee WHERE er_id = " + ycl.er_id.getValue();
		he.findBySQL(con, sqlstr1, false);
		he.card_number.setValue("");
		he.save(con, false);
		// COHr_access_emauthority_list.doUpdateAccessList(con, yc.employee_code.getValue(), 8, ycl.card_loss_no.getValue(), "卡挂失");
		UtilAccess.disableAllAccessByEmpid(con, he.er_id.getAsInt(), "挂失禁用权限");
	}

	@ACOAction(eventname = "saveloss", Authentication = true, notes = "自定义保存")
	public String saveloss() throws Exception {
		Hr_ykt_card_loss ycl = new Hr_ykt_card_loss();
		ycl.fromjson(CSContext.getPostdata());
		if (ycl.card_id.isEmpty()) {
			String sqlstr = "SELECT * FROM hr_ykt_card WHERE card_stat=1 AND er_id=" + ycl.er_id.getValue();
			Hr_ykt_card card = new Hr_ykt_card();
			card.findBySQL(sqlstr);
			if (card.isEmpty()) {
				throw new Exception("无正在使用的卡，无法挂失");
			}
			ycl.card_id.setValue(card.card_id.getValue());
			ycl.card_sn.setValue(card.card_sn.getValue());
			ycl.card_number.setValue(card.card_number.getValue());
			ycl.card_stat.setValue(card.card_stat.getValue());
		}
		ycl.save();
		ycl.wfcreate(null);// 自动提交
		return ycl.tojson();
	}

	@ACOAction(eventname = "findemp4loss", Authentication = true, notes = "查找可以挂失的人事资料")
	public String findemp4loss() throws Exception {
		HashMap<String, String> parms = CSContext.getParms();
		String eparms = parms.get("parms");
		List<JSONParm> jps = CJSON.getParms(eparms);
		Hr_employee he = new Hr_employee();
		String where = CjpaUtil.buildFindSqlByJsonParms(he, jps);
		String orgid = CorUtil.hashMap2Str(parms, "orgid");

		String sqlstr = "SELECT * FROM hr_employee "
				+ " WHERE  EXISTS( "
				+ " SELECT 1 FROM hr_ykt_card WHERE hr_employee.er_id=hr_ykt_card.er_id AND hr_ykt_card.card_stat=1 "
				+ " )";
		if ((orgid != null) && (!orgid.isEmpty())) {
			Shworg org = new Shworg();
			org.findByID(orgid, false);
			if (org.isEmpty())
				throw new Exception("没发现ID为【" + orgid + "】的机构");
			where = where + " and idpath like '" + org.idpath.getValue() + "%'";
		}
		String smax = parms.get("max");
		int max = (smax == null) ? 300 : Integer.valueOf(smax);
		sqlstr = sqlstr + CSContext.getIdpathwhere() + where + " limit 0," + max;
		return DBPools.defaultPool().opensql2json(sqlstr);
	}
}
