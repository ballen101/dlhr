package com.hr.attd.ctr;

import java.util.ArrayList;

import com.corsair.cjpa.CJPABase;
import com.corsair.dbpool.CDBConnection;
import com.corsair.dbpool.util.PraperedSql;
import com.corsair.server.cjpa.JPAController;
import com.hr.attd.entity.Hrkq_swcdlst;

public class CtrHrkq_swcdlst extends JPAController {
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
		Hrkq_swcdlst swc = (Hrkq_swcdlst) jpa;
		if (swc.empno.getAsIntDefault(0) == 0)
			throw new Exception("工号又TM为0了，麻烦通知项目组检查，谢谢！！！");

	}
}
