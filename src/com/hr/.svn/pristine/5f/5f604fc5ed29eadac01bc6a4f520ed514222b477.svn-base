package com.hr.perm.co;

import com.corsair.server.retention.ACO;
import com.corsair.server.retention.ACOAction;
import com.hr.perm.entity.Hr_entry_prob;

@ACO(coname = "web.hr.entryprob")
public class COHr_entry_prob {
	@ACOAction(eventname = "findeprobkhconst", Authentication = true, notes = "获取入职转正考核项")
	public String findeprobkhconst() throws Exception {
		String sqlstr = "select itemname khitem,itemnote khnote from hr_entry_prob_khconst order by pkcid";
		return (new Hr_entry_prob()).pool.opensql2json(sqlstr);
	}
}
