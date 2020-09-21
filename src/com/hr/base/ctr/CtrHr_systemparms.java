package com.hr.base.ctr;

import com.hr.perm.entity.Hr_systemparms;

public class CtrHr_systemparms {
	public static String getStrValue(String parmcode) throws Exception {
		String sqlstr = "SELECT * FROM hr_systemparms WHERE usable=1 AND parmcode='" + parmcode + "'";
		Hr_systemparms hs = new Hr_systemparms(sqlstr);
		if (hs.isEmpty())
			return null;
		else
			return hs.parmvalue.getValue();
	}

	public static int getIntValue(String parmcode) throws Exception {
		String pv = getStrValue(parmcode);
		if ((pv == null) || pv.isEmpty())
			return 0;
		else
			return Integer.valueOf(pv);
	}
}
