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
import com.hr.access.entity.Hr_access_emauthority_list;
import com.hr.access.entity.Hr_access_emauthority_sum;
import com.hr.access.entity.Hr_access_orauthority;
import com.hr.asset.entity.Hr_asset_reject_h;
import com.hr.card.entity.Hr_ykt_card;
import com.hr.card.entity.Hr_ykt_card_clean;
import com.hr.card.entity.Hr_ykt_card_loss;
import com.hr.card.entity.Hr_ykt_card_publish;
import com.hr.perm.entity.Hr_employee;

@ACO(coname = "web.hr.Card")
public class COHr_ykt_card_clean extends JPAController {

	@Override
	public void AfterWFStart(CJPA jpa, CDBConnection con, Shwwf wf, boolean isFilished)
			throws Exception {
		// TODO Auto-generated method stub
		if (isFilished) {
			doupcardclean(jpa, con);
			doUpdateAS(con, jpa);
		}
	}

	@Override
	public void OnWfSubmit(CJPA jpa, CDBConnection con, Shwwf wf, Shwwfproc proc,
			Shwwfproc nxtproc, boolean isFilished) throws Exception {
		// TODO Auto-generated method stub
		if (isFilished) {
			doupcardclean(jpa, con);
			doUpdateAS(con, jpa);
		}
	}

	public static void doUpdateAS(CDBConnection con, CJPA jpa) throws Exception {
		Hr_ykt_card_clean ycc = new Hr_ykt_card_clean();
		String sqlstr = "UPDATE hr_access_emauthority_sum t" +
				"   SET t.access_status = '2'," +
				"       t.accrediter    = 'SYSTEM'," +
				"       t.accredit_date = NOW()," +
				"       t.attribute1 = 'Y'," +
				"       t.remarks       = '清卡关闭'" +
				" WHERE t.employee_code = '" + ycc.employee_code.getValue() + "'" +
				"   AND t.access_card_number = '" + ycc.card_number.getValue() + "' and t.access_status=1 ";
		con.execsql(sqlstr);
	}

	// 更新卡档案数据
	private void doupcardclean(CJPA jpa, CDBConnection con) throws Exception {
		Hr_ykt_card_clean ycl = (Hr_ykt_card_clean) jpa;
		Hr_ykt_card yc = new Hr_ykt_card();
		Hr_employee he = new Hr_employee();
		String sqlstr = "SELECT * FROM hr_ykt_card t WHERE t.card_number = '" + ycl.card_number.getValue() + "'";
		// and card_stat=1 由于卡号唯一，所以取消状态，避免数据不一致的时候报错
		yc.findBySQL(con, sqlstr, false);
		yc.card_number.setValue(ycl.card_number.getValue());
		yc.card_stat.setValue("3");
		yc.er_id.setValue(ycl.er_id.getValue());
		yc.er_code.setValue(ycl.er_code.getValue());
		yc.employee_code.setValue(ycl.employee_code.getValue());
		yc.employee_name.setValue(he.employee_name.getValue());
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
		String sqlstr1 = "SELECT * FROM hr_employee t WHERE t.employee_code = '" + ycl.employee_code.getValue() + "'";
		he.findBySQL(con, sqlstr1, false);
		he.card_number.setValue("");
		he.save(con, false);
		// COHr_access_emauthority_list.doUpdateAccessList(con, yc.employee_code.getValue(), 7, ycl.card_clean_no.getValue(), "清卡");
		UtilAccess.disableAllAccessByEmpid(con, he.er_id.getAsInt(), "清卡禁用权限");
	}

	@ACOAction(eventname = "doclean", Authentication = true, notes = "清卡操作")
	public String doclean() throws Exception {
		HashMap<String, String> parms = CSContext.get_pjdataparms();
		String card_number = CorUtil.hashMap2Str(parms, "card_number", "需要参数card_number");
		String clean_reason = CorUtil.hashMap2Str(parms, "clean_reason");

		String sqlstr = "SELECT * FROM hr_ykt_card WHERE card_number='" + card_number + "' ";// AND card_stat=2
		Hr_ykt_card cd = new Hr_ykt_card();
		cd.findBySQL(sqlstr);		
		
		if (cd.isEmpty())
			throw new Exception("找不到卡号为【" + card_number + "】的【在用】卡档案信息");

		Hr_employee emp = new Hr_employee();
		//emp.findByID(cd.er_id.getValue(), false);
		
		sqlstr = "select * from hr_employee where empstatid IN (0,12,13) and er_id='" + cd.er_id.getValue() + "'";
		emp.findBySQL(sqlstr);// 查询人事档案
		if (emp.isEmpty())
			throw new Exception("不存在ID为【" + cd.er_id.getValue() + "】的离职员工资料");

		Hr_ykt_card_clean cc = new Hr_ykt_card_clean();
		CDBConnection con = cc.pool.getCon(this);
		con.startTrans();
		try {
			sqlstr = "SELECT * FROM hr_ykt_card_clean WHERE card_id=" + cd.card_id.getValue();
			cc.findBySQL(con, sqlstr, true);
			cc.card_clean_id.setValue(null);
			cc.card_clean_no.setValue(null);
			cc.card_id.setValue(cd.card_id.getValue()); // 卡ID
			cc.card_sn.setValue(cd.card_sn.getValue()); // 卡序列号
			cc.card_number.setValue(cd.card_number.getValue()); // 卡号
			cc.card_stat.setValue("3"); // 卡状态 1 正常 2 挂失 3 清卡 4 报废
			cc.employee_code.setValue(emp.employee_code.getValue()); // 工号
			cc.employee_name.setValue(emp.employee_name.getValue()); // 姓名
			cc.er_id.setValue(emp.er_id.getValue()); // 人事ID
			cc.er_code.setValue(emp.er_code.getValue()); // 档案编码
			cc.orgid.setValue(emp.orgid.getValue()); // 部门ID
			cc.orgcode.setValue(emp.orgcode.getValue()); // 部门编码
			cc.orgname.setValue(emp.orgname.getValue()); // 部门名称
			cc.sp_name.setValue(emp.sp_name.getValue()); // 职位
			cc.hwc_namezl.setValue(emp.hwc_namezl.getValue()); // 职类
			cc.hwc_namezq.setValue(emp.hwc_namezq.getValue()); // 职群
			cc.hwc_namezz.setValue(emp.hwc_namezz.getValue()); // 职种
			cc.hg_name.setValue(emp.hg_name.getValue()); // 职等
			cc.lv_num.setValue(emp.lv_num.getValue()); // 职级
			cc.clean_date.setAsDatetime(new Date()); // 清卡时间
			cc.clean_reason.setValue(clean_reason); // 清卡原因
			cc.disable_date.setAsDatetime(new Date()); // 失效时间
			cc.save(con);
			cc.wfcreate(null, con);
			con.submit();
			return cc.tojson();
		} catch (Exception e) {
			con.rollback();
			throw e;
		} finally {
			con.close();
		}

	}
}
