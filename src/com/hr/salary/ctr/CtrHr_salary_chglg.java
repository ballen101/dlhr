package com.hr.salary.ctr;

import java.util.ArrayList;
import java.util.Date;

import com.corsair.cjpa.CJPABase;
import com.corsair.dbpool.CDBConnection;
import com.corsair.dbpool.util.PraperedSql;
import com.corsair.dbpool.util.Systemdate;
import com.corsair.server.cjpa.JPAController;
import com.hr.salary.entity.Hr_salary_chglg;

public class CtrHr_salary_chglg extends JPAController {

	/**
	 * 删除前 貌似列表JPA被干掉时候无法监控到
	 * 
	 * @param jpa还未保存到数据库
	 *            ，JPA状态未变
	 * @param con
	 * @param selfLink
	 * @throws Exception
	 */
	@Override
	public void OnDelete(CJPABase jpa, CDBConnection con, boolean selfLink) throws Exception {
		throw new Exception("不允许删除");
	}

	/**
	 * 保存中
	 * 
	 * @param jpa
	 *            完成完整性检测、生成ID、CODE、设置完默认值、生成SQL语句；还未保存到数据库，JPA状态未变
	 * @param con
	 * @param sqllist
	 * @param selfLink
	 * @throws Exception
	 */
	public void OnSave(CJPABase jpa, CDBConnection con, ArrayList<PraperedSql> sqllist, boolean selfLink) throws Exception {
		Hr_salary_chglg sc = (Hr_salary_chglg) jpa;
		Date cd = Systemdate.getDateByStr(Systemdate.getStrDateyyyy_mm_dd(Systemdate.getDateByStr(sc.chgdate.getValue())));// 去除时分秒
		Date bgdate=Systemdate.getFirstAndLastOfMonth(cd).date1;
		Date eddate = Systemdate.dateMonthAdd(bgdate, 1);// 加一月
		String sqlstr = "UPDATE hr_salary_chglg SET useable=2 WHERE useable=1 AND er_id=" + sc.er_id.getValue() + 
				" AND sacgid<>" + sc.sacgid.getValue() ;
				//+" and chgdate>='"+Systemdate.getStrDateyyyy_mm_dd(bgdate)+"' and chgdate<'"+Systemdate.getStrDateyyyy_mm_dd(eddate)+"'";
		con.execsql(sqlstr);
	}

}
