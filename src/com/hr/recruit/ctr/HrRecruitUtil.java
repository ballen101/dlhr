package com.hr.recruit.ctr;

import com.corsair.dbpool.CDBConnection;
import com.hr.recruit.entity.Hr_recruit_ecodes;

public class HrRecruitUtil {

	public static String getNewEmpCode(CDBConnection con) throws Exception {
		String sqlstr = "SELECT * FROM hr_recruit_ecodes WHERE stat=9 AND usable=1 AND codecur<=codeend ORDER BY reid LIMIT 1 FOR UPDATE";
		Hr_recruit_ecodes ec = new Hr_recruit_ecodes();
		ec.findBySQL4Update(con, sqlstr, false);
		if (ec.isEmpty())
			throw new Exception("生成工号错误【未配置可用工号段】");
		if (ec.codecur.getAsInt() > ec.codeend.getAsInt())
			throw new Exception("数据错误");
		int codecur = ec.codecur.getAsInt();

		ec.codecur.setValue(codecur + 1);
		if (ec.codecur.getAsInt() > ec.codeend.getAsInt())
			ec.usable.setValue(2);
		ec.save(con);
		String rst = "000000000000" + codecur;
		rst = rst.substring(rst.length() - ec.codelen.getAsIntDefault(6), rst.length());
		return rst;
	}

}
