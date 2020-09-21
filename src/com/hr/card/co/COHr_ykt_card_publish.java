package com.hr.card.co;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import net.sf.json.JSONObject;

import com.corsair.cjpa.CJPABase;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CjpaUtil;
import com.corsair.dbpool.CDBConnection;
import com.corsair.dbpool.util.CJSON;
import com.corsair.dbpool.util.JSONParm;
import com.corsair.server.base.CSContext;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.cjpa.JPAController;
import com.corsair.server.generic.Shworg;
import com.corsair.server.retention.ACO;
import com.corsair.server.retention.ACOAction;
import com.corsair.server.util.CorUtil;
import com.corsair.server.wordflow.Shwwf;
import com.corsair.server.wordflow.Shwwfproc;
import com.hr.access.co.COHr_access_emauthority_list;
import com.hr.access.ctr.UtilAccess;
import com.hr.access.entity.Hr_access_emauthority_list;
import com.hr.access.entity.Hr_access_emauthority_sum;
import com.hr.access.entity.Hr_access_orauthority;
import com.hr.card.entity.Hr_ykt_card;
import com.hr.card.entity.Hr_ykt_card_publish;
import com.hr.perm.entity.Hr_employee;

@ACO(coname = "web.hr.Card")
public class COHr_ykt_card_publish extends JPAController {

	/**
	 * 保存前
	 * 
	 * @param jpa里面有值
	 *            ，还没检测数据完整性，没生成ID CODE 设置默认值
	 * @param con
	 * @param selfLink
	 * @throws Exception
	 */
	@Override
	public void BeforeSave(CJPABase jpa, CDBConnection con, boolean selfLink) throws Exception {
		Hr_ykt_card_publish ycp = (Hr_ykt_card_publish) jpa;
		if (ycp.card_type.getAsIntDefault(0) == 2) { // 类型 1新发 2补卡
			if (ycp.old_card_sn.isEmpty()) {
				String sqlstr = "SELECT * FROM `hr_ykt_card` WHERE er_id=" + ycp.er_id.getValue()
						+ " AND card_stat<>1 ORDER BY disable_date DESC";
				Hr_ykt_card yc = new Hr_ykt_card();
				yc.findBySQL(sqlstr);
				if (!yc.isEmpty()) {
					ycp.old_card_sn.setValue(yc.card_sn.getValue());
					ycp.old_card_number.setValue(yc.card_number.getValue());
				}
			}
		}
	}

	@Override
	public void AfterWFStart(CJPA jpa, CDBConnection con, Shwwf wf, boolean isFilished)
			throws Exception {
		// TODO Auto-generated method stub
		if (isFilished) {
			dowffinished(jpa, con);
		}
	}

	@Override
	public void OnWfSubmit(CJPA jpa, CDBConnection con, Shwwf wf, Shwwfproc proc,
			Shwwfproc nxtproc, boolean isFilished) throws Exception {
		// TODO Auto-generated method stub
		if (isFilished) {
			dowffinished(jpa, con);
		}
	}

	private void dowffinished(CJPA jpa, CDBConnection con) throws Exception {
		doupcard(jpa, con);
		Hr_ykt_card_publish cp = (Hr_ykt_card_publish) jpa;
		// if (cp.card_type.getAsIntDefault(0) == 1)
		doInsertAccessList(con, jpa);
		// if (cp.card_type.getAsIntDefault(0) == 2)// 类型 1新发 2补卡
		// doInsertAList(con, jpa, cp.old_card_sn.getValue());
	}

	// COHr_access_emauthority_list.doUpdateAccessList(con,emp.employee_code.getValue(),"1",entry.entry_id.getValue(),"员工入职");

	// 更新卡档案数据
	private void doupcard(CJPA jpa, CDBConnection con) throws Exception {
		Hr_ykt_card_publish ycp = (Hr_ykt_card_publish) jpa;
		Hr_ykt_card yc = new Hr_ykt_card();
		Hr_employee he = new Hr_employee();
		he.findByID(ycp.er_id.getValue(), false);
		if (he.isEmpty())
			throw new Exception("ID为【" + ycp.er_id.getValue() + "】的人事资料不存在");
		yc.clear();
		yc.card_number.setValue(ycp.card_number.getValue());
		yc.card_sn.setValue(ycp.card_sn.getValue());
		yc.finger_mark_no.setValue(ycp.finger_mark_no.getValue());
		yc.card_stat.setValue("1");
		yc.er_id.setValue(ycp.er_id.getValue());
		yc.er_code.setValue(ycp.er_code.getValue());
		yc.employee_code.setValue(ycp.employee_code.getValue());
		yc.employee_name.setValue(he.employee_name.getValue());
		yc.orgid.setValue(ycp.orgid.getValue());
		yc.orgcode.setValue(ycp.orgcode.getValue());
		yc.orgname.setValue(ycp.orgname.getValue());
		yc.sp_name.setValue(ycp.sp_name.getValue());
		yc.hwc_namezl.setValue(ycp.hwc_namezl.getValue());
		yc.hwc_namezq.setValue(ycp.hwc_namezq.getValue());
		yc.hwc_namezz.setValue(ycp.hwc_namezz.getValue());
		yc.hg_name.setValue(ycp.hg_name.getValue());
		yc.lv_num.setValue(ycp.lv_num.getValue());
		yc.effective_date.setAsDatetime(new Date());
		yc.disable_date.setValue("9999-12-31");
		yc.idpath.setValue(he.idpath.getValue());
		if (yc.employee_name.isEmpty())
			throw new Exception("姓名不能为空");
		yc.save(con);
		// 更新发卡表的卡关联信息
		ycp.card_id.setValue(yc.card_id.getValue());
		ycp.idpath.setValue(he.idpath.getValue());
		ycp.save(con);
		// 赋值人事档案中的卡信息
		he.findByID4Update(con, ycp.er_id.getValue(), false);
		he.card_number.setValue(ycp.card_number.getValue());
		he.save(con, false);
		// 设置权限

		// UtilAccess.disableAllAccessByEmpid(con, yc.er_id.getAsInt(), "补签清理原权限"); //不能这样写，已经不知道原卡号是多少了
		// 不管补卡发卡，按机构授权就好了
		// UtilAccess.appendAccessListByOrg(con, yc.er_id.getAsInt(), he.orgid.getValue(), 1, ycp.card_publish_id.getValue(),
		// ycp.card_publish_no.getValue(), "发卡/补卡新增权限");
		// COHr_access_emauthority_list.doUpdateAccessList(con, ycp.employee_code.getValue(), 1, ycp.card_publish_no.getValue(), "补卡或发卡");
	}

	@ACOAction(eventname = "savepublish", Authentication = true, notes = "自定义保存")
	public String savepublish() throws Exception {
		Hr_ykt_card_publish ycp = new Hr_ykt_card_publish();
		CDBConnection con = ycp.pool.getCon(this);
		con.startTrans();
		try {
			ycp.fromjson(CSContext.getPostdata());
			ycp.publish_date.setAsDatetime(new Date());
			ycp.effective_date.setAsDatetime(new Date());
			if (ycp.card_publish_id.isEmpty()) {// 创建记录
				int ct = getEmpCardCt(ycp.er_id.getValue(), false);//
				ycp.card_type.setAsInt((ct == 0) ? 1 : 2);
				if (ycp.card_type.getAsIntDefault(0) == 2) {// 类型 1新发 2补卡
					if (ycp.card_number.isEmpty()) {// 生成新的卡号
						ycp.card_number.setAsInt(getnewCardNo(con));
					}
				}
				if (ycp.card_type.getAsIntDefault(0) == 1) {// 类型 1新发 2补卡
					if (ycp.card_number.isEmpty()) {// 生成新的卡号
						String cn = ycp.employee_code.getValue();
						// cn="000000"+cn;
						ycp.card_number.setValue(ycp.employee_code.getValue());
					}
				}
			}
			if (ycp.employee_name.isEmpty())
				throw new Exception("姓名不能为空");
			if (ycp.card_number.isEmpty())
				throw new Exception("卡号不能为空");
			if (ycp.card_sn.isEmpty())
				throw new Exception("卡序列号不能为空");
			ycp.save(con);
			ycp.wfcreate(null, con);// 自动提交
			con.submit();
			return ycp.tojson();
		} catch (Exception e) {
			con.rollback();
			throw e;
		} finally {
			con.close();
		}
	}

	/**
	 * @param con
	 * @return 补卡获取新的卡号
	 * @throws Exception
	 */
	private int getnewCardNo(CDBConnection con) throws Exception {
		String sqlstr = "select curnum from hr_ykt_cardnocur for update";
		int curno = Integer.valueOf(con.openSql2List(sqlstr).get(0).get("curnum"));
		while (true) {
			sqlstr = "SELECT COUNT(*) ct FROM `hr_ykt_cardnodisenabled` WHERE cardno=" + curno;
			if (Integer.valueOf(con.openSql2List(sqlstr).get(0).get("ct")) == 0)
				break;
			else
				curno++;
		}
		if (curno > 199999)
			throw new Exception("按新规则补卡卡号用磬，咋办呢？");
		sqlstr = "update hr_ykt_cardnocur set curnum=" + (curno + 1);
		con.execsql(sqlstr);
		return curno;
	}

	@ACOAction(eventname = "getyktcard", Authentication = true, notes = "获取工号对应卡档案")
	public String getyktcard() throws Exception {
		HashMap<String, String> parms = CSContext.getParms();
		String erid = CorUtil.hashMap2Str(parms, "erid", "需要参数erid");
		String istr = CorUtil.hashMap2Str(parms, "isnormalcard");
		boolean isnormalcard = ((istr == null) || istr.isEmpty()) ? false : Boolean.valueOf(istr);
		JSONObject jo = new JSONObject();
		jo.put("ct", getEmpCardCt(erid, isnormalcard));
		return jo.toString();
	}

	private int getEmpCardCt(String erid, boolean isnormalcard) throws Exception {
		Hr_ykt_card_publish ycp1 = new Hr_ykt_card_publish();
		String sqlstr = "SELECT ifnull(COUNT(*),0) ct FROM hr_ykt_card t WHERE t.er_id =" + erid;
		if (isnormalcard)
			sqlstr = sqlstr + " and card_stat=1";
		return Integer.valueOf(ycp1.pool.openSql2List(sqlstr).get(0).get("ct"));
	}

	@ACOAction(eventname = "findemp4card", Authentication = true, notes = "查找未发卡或需要补卡的人事资料")
	public String findemp4card() throws Exception {
		HashMap<String, String> parms = CSContext.getParms();
		String eparms = parms.get("parms");
		List<JSONParm> jps = CJSON.getParms(eparms);
		for (JSONParm jp : jps) {
			jp.setParmname("e." + jp.getParmname());
		}
		Hr_employee he = new Hr_employee();
		String where = CjpaUtil.buildFindSqlByJsonParms(he, jps);
		String orgid = CorUtil.hashMap2Str(parms, "orgid");
		String hiredday = CorUtil.hashMap2Str(parms, "hiredday");

		String sqlstr = "SELECT e.* FROM  hr_employee e LEFT JOIN hr_ykt_card c ON (e.er_id=c.er_id ";
		if ((hiredday == null) || (hiredday.isEmpty())) {
			sqlstr = sqlstr + " AND c.card_stat = 1 ";
		}
		sqlstr = sqlstr + ")  WHERE c.card_id IS NULL AND e.empstatid<=10 ";
		if ((orgid != null) && (!orgid.isEmpty())) {
			Shworg org = new Shworg();
			org.findByID(orgid, false);
			if (org.isEmpty())
				throw new Exception("没发现ID为【" + orgid + "】的机构");
			where = where + " and e.idpath like '" + org.idpath.getValue() + "%'";
		}
		if ((hiredday != null) && (!hiredday.isEmpty())) {
			where = where + " and hiredday='" + hiredday + "'";
		}

		String smax = parms.get("max");
		int max = (smax == null) ? 300 : Integer.valueOf(smax);
		sqlstr = sqlstr + CSContext.getIdpathwhere().replace("idpath", "e.idpath") + where + " limit 0," + max;
		sqlstr = "SELECT tb.*,IFNULL(COUNT(c.`card_id`),0) cardct FROM ( " + sqlstr + ") tb "
				+ " LEFT JOIN hr_ykt_card c ON tb.er_id=c.er_id "
				+ " GROUP BY tb.er_id";
		return he.pool.opensql2json(sqlstr);
	}

	// 发卡插入门禁流水表
	public static void doInsertAccessList(CDBConnection con, CJPA jpa) throws Exception {
		Hr_access_emauthority_list as = new Hr_access_emauthority_list();
		Hr_ykt_card_publish ao = (Hr_ykt_card_publish) jpa;
		Hr_employee emp = new Hr_employee();
		emp.findByID(ao.er_id.getValue());
		if (emp.isEmpty())
			throw new Exception("未找到工号为【" + ao.employee_code.getValue() + "】的员工资料");
		String orgids = emp.idpath.getValue();
		orgids = orgids.substring(0, orgids.length() - 1);
		String sqlstr = "SELECT * FROM Hr_access_orauthority WHERE stat=9 AND orgid IN (" + orgids + ")";
		CJPALineData<Hr_access_orauthority> es = new CJPALineData<Hr_access_orauthority>(Hr_access_orauthority.class);
		es.findDataBySQL(sqlstr);
		for (CJPABase j : es) {
			Hr_access_orauthority er = (Hr_access_orauthority) j;
			as.clear();
			as.source.setValue("6");
			as.source_id.setValue(ao.card_publish_id.getValue());
			as.source_num.setValue(ao.card_publish_no.getValue());
			as.change_reason.setValue("发卡/补卡授权");
			as.er_id.setValue(ao.er_id.getValue());
			as.employee_code.setValue(ao.employee_code.getValue());
			as.employee_name.setValue(ao.employee_name.getValue());
			as.sex.setValue(emp.sex.getValue());
			as.access_card_number.setValue(ao.card_number.getValue());
			as.access_card_seq.setValue(ao.card_sn.getValue());
			as.hiredday.setValue(emp.hiredday.getValue());
			as.orgid.setValue(ao.orgid.getValue());
			as.orgname.setValue(ao.orgname.getValue());
			as.orgcode.setValue(ao.orgcode.getValue());
			as.extorgname.setValue(emp.orgname.getValue());
			as.hwc_namezl.setValue(ao.hwc_namezl.getValue());
			as.lv_id.setValue(emp.lv_id.getValue());
			as.lv_num.setValue(emp.lv_num.getValue());
			as.access_list_id.setValue(er.access_list_id.getValue());
			as.access_list_code.setValue(er.access_list_code.getValue());
			as.access_list_model.setValue(er.access_list_model.getValue());
			as.deploy_area.setValue(er.deploy_area.getValue());
			as.access_place.setValue(er.access_place.getValue());
			as.access_status.setValue(1);
			as.accrediter.setValue("SYSTEM");
			as.accredit_date.setAsDatetime(new Date());
			as.remarks.setValue("发卡/补卡授权");
			as.save(con, false);
		}
	}

	// 补卡插入门禁流水表
	public static void doInsertAList(CDBConnection con, CJPA jpa, String oldcardsn) throws Exception {
		Hr_access_emauthority_list as = new Hr_access_emauthority_list();
		Hr_ykt_card_publish ao = (Hr_ykt_card_publish) jpa;
		Hr_employee emp = new Hr_employee();
		emp.findByID(ao.er_id.getValue());
		if (emp.isEmpty())
			throw new Exception("未找到工号为【" + ao.employee_code.getValue() + "】的员工资料");
		Shworg aos = new Shworg();
		aos.findByID(ao.orgid.getValue());
		String sqlstr = "SELECT * FROM hr_access_emauthority_sum t WHERE t.access_status=2 and t.attribute1 = 'Y' AND t.access_card_seq = '"
				+ oldcardsn + "'";
		CJPALineData<Hr_access_emauthority_sum> es = new CJPALineData<Hr_access_emauthority_sum>(
				Hr_access_emauthority_sum.class);
		es.findDataBySQL(sqlstr);
		for (CJPABase j : es) {
			Hr_access_emauthority_sum er = (Hr_access_emauthority_sum) j;
			as.clear();
			as.source.setValue("6");
			as.source_id.setValue(ao.card_publish_id.getValue());
			as.source_num.setValue(ao.card_publish_no.getValue());
			as.change_reason.setValue("新卡授权");
			as.er_id.setValue(ao.er_id.getValue());
			as.employee_code.setValue(ao.employee_code.getValue());
			as.employee_name.setValue(ao.employee_name.getValue());
			as.sex.setValue(emp.sex.getValue());
			as.access_card_number.setValue(ao.card_number.getValue());
			as.access_card_seq.setValue(ao.card_sn.getValue());
			as.hiredday.setValue(emp.hiredday.getValue());
			as.orgid.setValue(ao.orgid.getValue());
			as.orgname.setValue(ao.orgname.getValue());
			as.orgcode.setValue(ao.orgcode.getValue());
			as.extorgname.setValue(aos.extorgname.getValue());
			as.hwc_namezl.setValue(ao.hwc_namezl.getValue());
			as.lv_id.setValue(emp.lv_id.getValue());
			as.lv_num.setValue(emp.lv_num.getValue());
			as.access_list_id.setValue(er.access_list_id.getValue());
			as.access_list_code.setValue(er.access_list_code.getValue());
			as.access_list_model.setValue(er.access_list_model.getValue());
			as.deploy_area.setValue(er.deploy_area.getValue());
			as.access_place.setValue(er.access_place.getValue());
			as.access_status.setValue("1");
			as.accrediter.setValue("SYSTEM");
			as.accredit_date.setAsDatetime(new Date());
			as.remarks.setValue("补卡授权");
			as.save(con, false);
		}
	}
}
