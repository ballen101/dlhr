package com.hr.util;

import com.corsair.server.base.CSContext;
import com.corsair.server.cjpa.CJPA;

public class HRSpecDataFilter {
	// 特殊数据 0||undef 非特殊 1 个人所属 2 个人创建
	public static String getspsqlwhere(CJPA jpa, int spcetype) throws Exception {
		if (spcetype == 1) {
			// if (jpa instanceof Hrkq_overtime) {
			//
			// }
			return " and employee_code='" + CSContext.getUserName() + "'"; // 默认都这个
		}
		return null;
	}
}
