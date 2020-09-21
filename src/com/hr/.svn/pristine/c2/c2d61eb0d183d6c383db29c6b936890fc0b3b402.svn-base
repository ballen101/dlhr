package com.hr.perm.ctr;

import java.util.Date;

import com.corsair.dbpool.CDBConnection;
import com.hr.perm.entity.Hr_balcklist;
import com.hr.perm.entity.Hr_employee;

public class CtrHrEmployeeUtil {
	public static boolean isInBlackList(String id_number) throws Exception {
		String sqlstr = "select * from hr_balcklist where id_number='" + id_number + "'";
		Hr_balcklist bl = new Hr_balcklist();
		bl.findBySQL(sqlstr);
		return !bl.isEmpty();
	}

	public static void removeBlackList(CDBConnection con, String id_number) throws Exception {
		String sqlstr = "select * from hr_balcklist where id_number='" + id_number + "'";
		Hr_balcklist bl = new Hr_balcklist();
		bl.findBySQL4Update(con, sqlstr, false);
		bl.delete(con, false);
	}

	public static void addBlackList(CDBConnection con, String id_number) throws Exception {
		if (isInBlackList(id_number))
			return;
		Hr_balcklist bl = new Hr_balcklist();
		bl.id_number.setValue(id_number);
		bl.createtime.setAsDatetime(new Date());
		bl.save(con);
	}

	public static Hr_employee getIDNumberIsFrmal(String id_number) throws Exception {// 查询身份证的在职职工
		String sqlstr = "SELECT * FROM  hr_employee WHERE id_number ='" + id_number + "' and empstatid in(1,2,3,4,5,6,7,8) ORDER BY er_id DESC";
		Hr_employee he = new Hr_employee();
		he.findBySQL(sqlstr);
		return he;
	}
}
