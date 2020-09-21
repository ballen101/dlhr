package com.hr.recruit.co;

import java.util.Date;
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
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.corsair.cjpa.CJPABase;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.CJPABase.CJPAStat;
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
import com.corsair.server.util.CReport;
import com.corsair.server.util.CSearchForm;
import com.corsair.server.util.CorUtil;
import com.corsair.server.util.DictionaryTemp;
import com.corsair.server.util.UpLoadFileEx;
import com.corsair.server.wordflow.Shwwf;
import com.corsair.server.wordflow.Shwwfproc;
import com.hr.asset.entity.Hr_asset_allot_h;
import com.hr.base.entity.Hr_orgposition;
import com.hr.perm.entity.Hr_employee;
import com.hr.recruit.entity.Hr_recruit_distribution;
import com.hr.recruit.entity.Hr_recruit_form;
import com.hr.recruit.entity.Hr_recruit_transport;

@ACO(coname = "web.hr.Recruit.Distribution")
public class COHr_recruit_distribution {

	// 查询招募人员
	@ACOAction(eventname = "findreCruitEr", Authentication = true, ispublic = false, notes = "招募人员查询")
	public String findreCruitEr() throws Exception {
		Hr_employee al = new Hr_employee();
		String sqlstr = "SELECT e.*,t.recruit_quachk_id " +
				"  FROM hr_recruit_form t, hr_employee e" +
				" WHERE t.er_id = e.er_id and t.quachk=1 and e.empstatid=0 " +
				"   AND NOT EXISTS (SELECT *" +
				"          FROM hr_recruit_distribution rd" +
				"         WHERE rd.employee_code = t.employee_code" +
				"           AND rd.distribution_type in (1,2))";
		return new CReport(sqlstr, null).findReport();
	}

	// 查询已分配人员
	@ACOAction(eventname = "findreDistrEr", Authentication = true, ispublic = false, notes = "已分配人员查询")
	public String findreDistrEr() throws Exception {
		String sqlstr = "SELECT e.*,t.recruit_quachk_id " +
				"  FROM hr_recruit_distribution t, hr_employee e" +
				" WHERE t.er_id = e.er_id" +
				"   AND t.stat = 9" +
				"   AND t.distribution_type = 1" +
				"   AND NOT EXISTS (SELECT *" +
				"          FROM hr_recruit_distribution rd" +
				"         WHERE rd.er_id = t.er_id" +
				"           AND rd.stat = 9" +
				"           AND rd.distribution_type = 2)";
		return new CReport(sqlstr, null).findReport();
	}

	@ACOAction(eventname = "batchDisp", Authentication = true, ispublic = false, notes = "批量分配")
	public String batchDisp() throws Exception {
		JSONObject jo = CSContext.parPostData2JSONObject();
		String ospid = CorUtil.getJSONValue(jo, "ospid", "需要参数【ospid】");
		Hr_orgposition osp = new Hr_orgposition();
		osp.findByID(ospid, false);
		if (osp.isEmpty())
			throw new Exception("ID为【" + ospid + "】的机构职位不存在");

		Hr_orgposition eosp = new Hr_orgposition();

		Object[] erids = jo.getJSONArray("erids").toArray();
		Hr_recruit_distribution rd = new Hr_recruit_distribution();
		Hr_employee emp = new Hr_employee();
		Hr_recruit_form ref = new Hr_recruit_form();
		CDBConnection con = rd.pool.getCon(this);
		con.startTrans();
		try {
			for (Object erid : erids) {
				emp.clear();
				emp.findByID(erid.toString(), false);
				if (emp.isEmpty())
					throw new Exception("ID为【" + erid.toString() + "】的人事资料不存在");
				// emp.empstatid 状态判断
				eosp.clear();
				eosp.findByID(emp.ospid.getValue());
				if (!osp.sp_id.getValue().equalsIgnoreCase(eosp.sp_id.getValue())) {
					throw new Exception("工号【" + emp.employee_code.getValue() + "】分配前后不数据统一标准职位");
				}

				ref.clear();
				ref.findBySQL("select * from hr_recruit_form where er_id=" + erid);
				if (ref.isEmpty())
					throw new Exception("人事ID【" + erid + "】的招募登记数据不存在");

				rd.clear();
				rd.er_id.setValue(emp.er_id.getValue()); // 人事ID
				rd.er_code.setValue(emp.er_code.getValue()); // 档案编码
				rd.employee_code.setValue(emp.employee_code.getValue()); // 工号
				rd.employee_name.setValue(emp.employee_name.getValue()); // 姓名
				rd.hiredday.setValue(emp.hiredday.getValue()); // 入职日期
				rd.emnature.setValue(emp.emnature.getValue()); // 人员性质
				rd.orgid.setValue(emp.orgid.getValue()); // 部门ID
				rd.orgcode.setValue(emp.orgcode.getValue()); // 部门编码
				rd.orgname.setValue(emp.orgname.getValue()); // 部门名称
				rd.hwc_namezl.setValue(emp.hwc_namezl.getValue()); // 职类
				rd.lv_id.setValue(emp.lv_id.getValue()); // 职级ID
				rd.lv_num.setValue(emp.lv_num.getValue()); // 职级
				rd.hg_id.setValue(emp.hg_id.getValue()); // 职等ID
				rd.hg_code.setValue(emp.hg_code.getValue()); // 职等编码
				rd.hg_name.setValue(emp.hg_name.getValue()); // 职等名称
				rd.ospid.setValue(emp.ospid.getValue()); // 职位ID
				rd.ospcode.setValue(emp.ospcode.getValue()); // 职位编码
				rd.sp_name.setValue(emp.sp_name.getValue()); // 职位名称
				rd.norgid.setValue(osp.orgid.getValue()); // 分配部门ID
				rd.norgcode.setValue(osp.orgcode.getValue()); // 分配部门编码
				rd.norgname.setValue(osp.orgname.getValue()); // 分配部门名称
				rd.nhwc_namezl.setValue(osp.hwc_namezl.getValue()); // 分配职类
				String nemnature = (osp.isoffjob.getAsIntDefault(0) == 1) ? "脱产" : "非脱产";
				rd.nemnature.setValue(nemnature);
				rd.nlv_id.setValue(osp.lv_id.getValue()); // 分配职级ID
				rd.nlv_num.setValue(osp.lv_num.getValue()); // 分配职级
				rd.nhg_id.setValue(osp.hg_id.getValue()); // 分配职等ID
				rd.nhg_code.setValue(osp.hg_code.getValue()); // 分配职等编码
				rd.nhg_name.setValue(osp.hg_name.getValue()); // 分配职等名称
				rd.nospid.setValue(osp.ospid.getValue()); // 分配职位ID
				rd.nospcode.setValue(osp.ospcode.getValue()); // 分配职位编码
				rd.nsp_name.setValue(osp.sp_name.getValue()); // 分配职位名称
				rd.note.setValue("批量分配"); // 备注
				rd.idpath.setValue(osp.idpath.getValue()); // idpath
				rd.distribution_type.setValue(1);
				rd.distribution_date.setAsDatetime(new Date());
				rd.recruit_quachk_id.setValue(ref.recruit_quachk_id.getValue());
				rd.save(con);
				rd.wfcreate(null, con);
			}
			con.submit();
		} catch (Exception e) {
			con.rollback();
			throw e;
		} finally {
			con.close();
		}
		return CSContext.getJSON_OK();
	}

}
