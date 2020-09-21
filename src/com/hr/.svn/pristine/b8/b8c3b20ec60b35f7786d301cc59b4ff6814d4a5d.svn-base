package com.hr.perm.ctr;

import com.corsair.cjpa.CJPABase;
import com.corsair.dbpool.CDBConnection;
import com.corsair.dbpool.util.Systemdate;
import com.corsair.server.cjpa.CJPA;
import com.corsair.server.cjpa.JPAController;
import com.hr.perm.entity.Hr_employee;
import com.hr.perm.entity.Hr_employee_reward;

public class CtrHr_employee_reward extends JPAController {
	/**
	 * 通用保存后，提交事务前触发
	 * 
	 * @param con
	 * @param jpa
	 * @return 不为空的返回值，将作为查询结果返回给前台
	 * @throws Exception
	 */
	public String AfterCCoSave(CDBConnection con, CJPA jpa) throws Exception {
		return null;
	}

	/**
	 * 完成保存
	 * 
	 * @param jpa
	 * 已经实例化到数据库，JPA变为 RSLOAD4DB 状态
	 * @param con
	 * @param selfLink
	 */
	public void AfterSave(CJPABase jpa, CDBConnection con, boolean selfLink) throws Exception {
		Hr_employee_reward er = (Hr_employee_reward) jpa;
		if (er.rwnature.getAsInt(0) == 2) {// 激励类型 1 正 2 负
			String ym = Systemdate.getStrDateByFmt(er.rewardtime.getAsDate(), "yyyy-MM");
			String er_id = er.er_id.getValue();
			String sqlstr = "SELECT IFNULL(SUM(ABS(IFNULL(rwamount,0))),0) samt FROM hr_employee_reward WHERE er_id="
					+ er_id + " AND rwnature=2 AND DATE_FORMAT(rewardtime,'%Y-%m') ='" + ym + "'";
			float samt = Float.valueOf(con.openSql2List(sqlstr).get(0).get("samt").toString());
			Hr_employee emp = new Hr_employee();
			emp.findByID(con, er_id);
			float lv = emp.lv_num.getAsFloat(4);
			if (lv < 4) {
				if (samt > 2000) {
					throw new Exception("员工【" + emp.employee_name.getValue() + "】在【" + ym + "】已录入扣罚【"
							+ samt + "】元，本次录入已超过上限【2000】元，请拆分【" + (samt - 2000) + "】元录入到次月");
				}
			} else {
				if (samt > 500) {
					throw new Exception("员工【" + emp.employee_name.getValue() + "】在【" + ym + "】已录入扣罚【"
							+ samt + "】元，本次录入已超过上限【500】元，请拆分【" + (samt - 500) + "】元录入到次月");
				}
			}
		}
	}
}
