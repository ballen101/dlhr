package com.hr.recruit.ctr;

import com.corsair.cjpa.CJPABase;
import com.corsair.dbpool.CDBConnection;
import com.corsair.server.cjpa.JPAController;
import com.hr.recruit.entity.Hr_recruit_ecodes;

public class CtrHr_recruit_ecodes extends JPAController {
	@Override
	public void BeforeSave(CJPABase jpa, CDBConnection con, boolean selfLink) throws Exception {
		Hr_recruit_ecodes re = (Hr_recruit_ecodes) jpa;
		if (re.codecur.isEmpty())
			re.codecur.setValue(re.codebegin.getValue());
	}
}
