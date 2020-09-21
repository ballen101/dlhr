package com.corsair.cjpa.util;

import java.util.ArrayList;
import java.util.List;

import com.corsair.cjpa.CJPABase;
import com.corsair.dbpool.CDBPool;

/**
 * SQL创建工具
 * 
 * @author Administrator
 *
 */
public class CJPASqlBuilding {
	private int aliaid = 0;

	public List<String> getDelSqlsByAnno(Class<?> cls, String idvalue, Boolean selfLink) throws Exception {
		aliaid = 0;
		List<String> sqls = new ArrayList<String>();
		if (!CJPABase.class.isAssignableFrom(cls)) {
			throw new Exception("【" + cls.getName() + "】必须从CJPA继承");
		}
		getDelSqlJPA(sqls, cls, idvalue, selfLink);
		return sqls;
	}

	private int getAliaId() {
		return ++aliaid;
	}

	private void getDelSqlJPA(List<String> sqls, Class<?> cls, String idvalue, Boolean selfLink) throws Exception {
		aliaid = 0;
		String aln = "tb" + getAliaId();
		CFieldinfo idfdinfo = CjpaUtil.getIDField(cls);
		CDBPool pool = CjpaUtil.getDBPool(cls);
		String tbname = CjpaUtil.getJPATableName(cls);

		String v = CJPASqlUtil.getSqlValue(pool.getDbtype(), idfdinfo.datetype(), idvalue);
		String oidsql = "select " + aln + "." + idfdinfo.fieldname() + " from " + tbname + " " + aln + " where " + aln + "." + idfdinfo.fieldname() + "=" + v;

		List<CLinkFieldInfo> lines = CjpaUtil.getLineLinkedInfos(cls, false);
		if ((selfLink) && (lines.size() > 0))
			getDelSqlJPALine(sqls, lines, oidsql, aln);
		// 加入删除语句
		String sqldel = "delete from " + tbname + " where " + idfdinfo.fieldname() + "=" + v;
		sqls.add(sqldel);
	}

	private void getDelSqlJPALine(List<String> sqls, List<CLinkFieldInfo> lines, String oidsql, String oaln) throws Exception {
		for (CLinkFieldInfo lfi : lines) {
			Class<?> cls = lfi.jpaclass();
			String aln = "tb" + getAliaId();
			String ltbname = CjpaUtil.getJPATableName(cls);
			LinkFieldItem[] items = lfi.linkFields();
			String ssql = "select " + aln + "." + CjpaUtil.getJPAIDFieldNameByClass(cls) + " from " + ltbname + " " + aln + " WHERE EXISTS(";
			String sqlexts = oidsql;
			for (LinkFieldItem item : items) {
				sqlexts = sqlexts + " and " + oaln + "." + item.mfield() + "=" + aln + "." + item.lfield();
			}
			ssql = ssql + sqlexts;
			ssql = ssql + ")";

			List<CLinkFieldInfo> llines = CjpaUtil.getLineLinkedInfos(cls, true);
			if (llines.size() > 0) {
				getDelSqlJPALine(sqls, llines, ssql, aln);
			}
			// 加入删除语句
			String sqlstrm = "select * from (" + ssql + ") tb";// mysql的中间表
			String sqldel = "delete from " + ltbname + " where " + CjpaUtil.getJPAIDFieldNameByClass(cls) + " in (" + sqlstrm + ")";
			sqls.add(sqldel);
		}
	}
}
