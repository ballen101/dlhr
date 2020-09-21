package com.hr.perm.ctr;

import com.corsair.dbpool.CDBConnection;
import com.hr.base.entity.Hr_month_orgposition;
import com.hr.base.entity.Hr_month_quotaoc;
import com.hr.perm.entity.Hr_employee;

public class CtrHrPerm {

	/**
	 * 人事月结
	 * 
	 * @param ym
	 * @return
	 * @throws Exception
	 */
	public static int putmonthemployee(String ym) throws Exception {
		CDBConnection con = new Hr_employee().pool.getCon("putmonthemployee");
		String sqlstr = "delete from hr_month_employee where yearmonth='" + ym + "'";
		int yjct = 0;
		con.startTrans();
		try {
			con.execsql(sqlstr);
			sqlstr = "INSERT INTO hr_month_employee(yearmonth," + emp_fields + ") SELECT '" + ym + "' ," + emp_fields
					+ " FROM hr_employee e WHERE e.empstatid<10";
			yjct = con.execsql(sqlstr);
			con.submit();
			return yjct;
		} catch (Exception e) {
			con.rollback();
			throw e;
		} finally {
			con.close();
		}
	}

	/**
	 * 机构职位月结
	 * 
	 * @param ym
	 * @return
	 * @throws Exception
	 */
	public static int putmonthosp(String ym) throws Exception {
		CDBConnection con = new Hr_month_orgposition().pool.getCon("putmonthosp");
		String sqlstr = "delete from hr_month_orgposition where yearmonth='" + ym + "'";
		int yjct = 0;
		con.startTrans();
		try {
			con.execsql(sqlstr);
			sqlstr = "INSERT INTO hr_month_orgposition(yearmonth," + _ospsqlfield + ") SELECT '" + ym + "' ," + _ospsqlfield
					+ " FROM hr_orgposition op WHERE quota>0 and EXISTS( " + " SELECT 1 FROM shworg o WHERE op.`orgid`=o.orgid AND o.`usable`=1 )";
			yjct = con.execsql(sqlstr);
			con.submit();
			return yjct;
		} catch (Exception e) {
			con.rollback();
			throw e;
		} finally {
			con.close();
		}
	}

	/**
	 * 机构职位月结
	 * 
	 * @param ym
	 * @return
	 * @throws Exception
	 */
	public static int putmonthqtc(String ym) throws Exception {
		CDBConnection con = new Hr_month_quotaoc().pool.getCon("putmonthqtc");
		String sqlstr = "delete from hr_month_quotaoc where yearmonth='" + ym + "'";
		int yjct = 0;
		con.startTrans();
		try {
			con.execsql(sqlstr);
			sqlstr = "INSERT INTO hr_month_quotaoc(yearmonth," + _otqsqlstrfield + ") SELECT '" + ym + "' ,"
					+ " oc.`ocqid`, oc.`orgid`,  oc.`classid`,  oc.`quota`,o.`orgname`,'OO' as `hwc_namezl` FROM hr_quotaoc oc,shworg o"
					+ " WHERE  oc.`orgid`=o.orgid AND o.`usable`=1 and oc.usable=1";
			yjct = con.execsql(sqlstr);
			con.submit();
			return yjct;
		} catch (Exception e) {
			con.rollback();
			throw e;
		} finally {
			con.close();
		}
	}

	private static String emp_fields = "er_id, er_code, employee_code, id_number, sign_org, sign_date, expired_date,"
			+ " card_number, employee_name, mnemonic_code, english_name, avatar_id1, avatar_id2,"
			+ " birthday, sex, bwday, bwyear, hiredday, ljdate, degree,degreetype,degreecheck, married, nationality, "
			+ "nativeid, nativeplace, address, nation, email, empstatid, modify, usedname, pldcp,"
			+ " major, registertype, registeraddress, health, medicalhistory, bloodtype, height, importway,"
			+ " importor, cellphone, urgencycontact, urmail, telphone, introducer, guarantor, skill, "
			+ "skillfullanguage, speciality, welfare, talentstype, emnature, orgid, orgcode, orgname, "
			+ "uorgid, uorgcode, uorgname, uidpath, hwc_namezl, lv_id, lv_num, hg_id, hg_code, hg_name,"
			+ " ospid, ospcode, sp_name, iskey, hwc_namezq, hwc_namezz, usable, sscurty_addr,"
			+ " sscurty_startdate, sscurty_enddate, shoesize, pants_code, coat_code, needdrom, dorm_bed, "
			+ "pay_way, schedtype, atdtype, resigntimes, wfbusyss, noclock, promotionday, entrysourcr, "
			+ "rectcode, rectname, eovertype,mlev,atmid,mdname,advtch_subsidy,isadvtch, note, attid, entid, "
			+ "idpath, creator, createtime, updator,"
			+ " updatetime, property1, property2, property3, property4, property5, insurancestat, advisercode,"
			+ " advisername, adviserphone, juridical, transorg, transextime, dispunit, dispeextime,"
			+ " kqdate_start, kqdate_end";

	private static String _ospsqlfield = "`ospid`, `ospcode`, `pid`, `pname`, `orgid`, `orgname`, `sp_id`, `sp_name`, "
			+ "`gtitle`, `lv_num`, `hg_name`, `hwc_namezl`, `hwc_namezq`, `hwc_namezz`, `quota`, `isadvtech`, "
			+ "`isoffjob`, `issensitive`, `iskey`, `ishighrisk`, `isneedadtoutwork`, `isdreamposition`, `idpath`";
	private static String _otqsqlstrfield = " `ocqid`, `orgid`,  `classid`,  `quota`,`orgname`,`hwc_namezl`";
}
