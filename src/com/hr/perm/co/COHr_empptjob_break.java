package com.hr.perm.co;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.corsair.server.retention.ACO;
import com.corsair.server.retention.ACOAction;
import com.corsair.server.util.CSearchForm;
import com.hr.perm.entity.Hr_empptjob_app;

@ACO(coname = "web.hr.ptbreak")
public class COHr_empptjob_break {
	@ACOAction(eventname = "finjobpt4break", Authentication = true, notes = "查找允许中断的,权限范围内")
	public String finjobpt4break() throws Exception {
		String sqlstr = "SELECT * FROM hr_empptjob_app WHERE stat=9 AND breaked=2 AND enddate> CURDATE() ";

		Hr_empptjob_app app = new Hr_empptjob_app();
		JSONArray rst = CSearchForm.doExec2JSON_O(app.pool, sqlstr);

		String[] delfds = { "remark", "wfid", "attid", "stat", "creator", "createtime", "updator",
				"updatetime", "attribute1", "attribute2", "attribute3", "attribute4", "attribute5", };
		for (int i = 0; i < rst.size(); i++) {
			JSONObject jo = rst.getJSONObject(i);
			delJOArres(jo, delfds);
		}
		return rst.toString();
	}

	private void delJOArres(JSONObject jo, String[] fds) {
		for (String fd : fds) {
			if (jo.has(fd))
				jo.remove(fd);
		}
	}
}
