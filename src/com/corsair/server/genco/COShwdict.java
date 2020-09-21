package com.corsair.server.genco;

import java.util.ArrayList;
import java.util.List;

import com.corsair.cjpa.CJPAJSON;
import com.corsair.cjpa.CJPALineData;
import com.corsair.dbpool.DBPools;
import com.corsair.server.base.CSContext;
import com.corsair.server.base.ConstsSw;
import com.corsair.server.generic.Shwdict;
import com.corsair.server.retention.ACO;
import com.corsair.server.retention.ACOAction;

@ACO(coname = "web.dict")
public class COShwdict {
	@ACOAction(eventname = "getdictvalues", Authentication = false, ispublic = true)
	public String getdictvalues() throws Exception {
		String dicid = CSContext.getParms().get("dicid");
		if ((dicid == null) || dicid.isEmpty())
			throw new Exception("参数dicid必须！");
		String sqlstr = "select * from shwdict where usable=1 and pid=" + dicid + " ORDER BY dictvalue+0";
		System.out.println("dicid---------------"+dicid);
		CJPALineData<Shwdict> dics = new CJPALineData<Shwdict>(Shwdict.class);
		dics.findDataBySQL(sqlstr, true, false);
		return dics.tojson();
	}

	@ACOAction(eventname = "getDicTree", Authentication = false, ispublic = true)
	public String getDicTree() throws Exception {
		String sqlstr = "select a.dictid id,a.* from shwdict a where a.pid<>0 ORDER BY dictvalue+0";
		return DBPools.defaultPool().opensql2jsontree(sqlstr, "id", "pid", false);
	}

	@ACOAction(eventname = "saveDict", Authentication = true, notes = "保存字典")
	public String saveDict() throws Exception {
		Shwdict dict = new Shwdict();
		dict.fromjson(CSContext.getPostdata());
		if ((dict.pid.getValue() == null) || dict.pid.getValue().isEmpty())
			throw new Exception("pid字段不能为空！");
		return ((CJPAJSON) dict.save()).tojson();
	}

	@ACOAction(eventname = "delDict", Authentication = true, notes = "删除字典")
	public String delDict() throws Exception {
		String dictid = CSContext.getParms().get("dictid");
		if ((dictid == null) || dictid.isEmpty())
			throw new Exception("参数dicid必须！");
		List<String> sqls = new ArrayList<String>();
		sqls.add("DELETE FROM shwdict WHERE pid IN  (SELECT dictid FROM (SELECT dictid FROM shwdict WHERE pid=" + dictid + ") tb)");
		sqls.add("DELETE FROM shwdict WHERE pid =" + dictid);
		sqls.add("DELETE FROM shwdict WHERE dictid =" + dictid);
		new Shwdict().pool.execSqls(sqls);
		return "{\"result\":\"OK\"}";
	}

}
