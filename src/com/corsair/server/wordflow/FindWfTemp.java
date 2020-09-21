package com.corsair.server.wordflow;

import java.sql.Types;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPABase;
import com.corsair.cjpa.CJPALineData;
import com.corsair.cjpa.util.CjpaUtil;
import com.corsair.server.cjpa.CJPA;

public class FindWfTemp {
	public static String findwftemp(String jpaclass, String id) throws Exception {
		CJPA jpa = (CJPA) CjpaUtil.newJPAObjcet(jpaclass.trim());
		jpa.findByID(id.trim());
		CJPALineData<Shwwftemp> wftems = findwftemp(jpa);
		return wftems.tojson();
	}
	/**
	 * 返回模板对像id
	 * @param jpaclass
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public static String findwftempid(String jpaclass, String id) throws Exception {
		CJPA jpa = (CJPA) CjpaUtil.newJPAObjcet(jpaclass.trim());
		jpa.findByID(id.trim());
		CJPALineData<Shwwftemp> wftems = findwftemp(jpa);
		if(wftems.size()>0){
			return wftems.get(0).getid();
		}else{
			return null;
		}
		
	}

	public static CJPALineData<Shwwftemp> findwftemp(CJPA jpa) throws Exception {
		CJPALineData<Shwwftemp> wftems = new CJPALineData<Shwwftemp>(Shwwftemp.class);
		String sqlstr = "select * from shwwftemp where stat=1 and clas='" + jpa.getClass().getName() + "'";
		wftems.findDataBySQL(sqlstr, true, true);
		for (int i = wftems.size() - 1; i >= 0; i--) {
			if (!checkParms((Shwwftemp) wftems.get(i), jpa)) {
				wftems.remove(i);
			}
		}
		return wftems;
	}

	private static boolean checkParms(Shwwftemp wftem, CJPA jpa) throws Exception {
		CJPALineData<Shwwftemparms> parms = wftem.shwwftemparmss;
		for (CJPABase jpap : parms) {
			Shwwftemparms parm = (Shwwftemparms) jpap;
			if (!(checkRowParm(parm, jpa)))
				return false;
		}
		return true;
	}

	private static boolean checkRowParm(Shwwftemparms parm, CJPA jpa) throws Exception {
		// System.out.println("parm:" + parm.tojson());
		CField field = jpa.cfieldbycfieldname(parm.parmname.getValue());
		if (field == null) {
			throw new Exception("流程启动条件字段【" + parm.parmname.getValue() + "】在JPA中不存在，请联系管理员！");
		}
		String reloper = parm.reloper.getValue();
		int fdtype = field.getFieldtype();
		String value = null;
		if (!parm.parmvalue.isEmpty())
			value = parm.parmvalue.getValue();
		if ((value != null) && (value.equalsIgnoreCase("NULL")))
			value = null;
		if (value == null) {
			if ("=".equalsIgnoreCase(reloper)) {
				return field.isEmpty();
			} else if ("<>".equalsIgnoreCase(reloper)) {
				return !field.isEmpty();
			} else
				return false;
		}

		switch (fdtype) {
		case Types.DATE:
		case Types.TIME:
		case Types.TIMESTAMP: {
			if (">".equalsIgnoreCase(reloper))
				return (field.getAsDatetime().getTime() > parm.parmvalue.getAsDatetime().getTime());
			if ("<".equalsIgnoreCase(reloper))
				return (field.getAsDatetime().getTime() < parm.parmvalue.getAsDatetime().getTime());
			if (">=".equalsIgnoreCase(reloper))
				return (field.getAsDatetime().getTime() >= parm.parmvalue.getAsDatetime().getTime());
			if ("<=".equalsIgnoreCase(reloper))
				return (field.getAsDatetime().getTime() <= parm.parmvalue.getAsDatetime().getTime());
			if ("=".equalsIgnoreCase(reloper))
				return (field.getAsDatetime().getTime() == parm.parmvalue.getAsDatetime().getTime());
			if ("<>".equalsIgnoreCase(reloper))
				return (field.getAsDatetime().getTime() != parm.parmvalue.getAsDatetime().getTime());
		}
		case Types.BIT:
		case Types.TINYINT:
		case Types.SMALLINT:
		case Types.INTEGER:
		case Types.BIGINT:
		case Types.FLOAT:
		case Types.REAL:
		case Types.DOUBLE:
		case Types.NUMERIC:
		case Types.DECIMAL: {
			if (">".equalsIgnoreCase(reloper))
				return (field.getAsFloatDefault(0) > parm.parmvalue.getAsFloatDefault(0));
			if ("<".equalsIgnoreCase(reloper))
				return (field.getAsFloatDefault(0) < parm.parmvalue.getAsFloatDefault(0));
			if (">=".equalsIgnoreCase(reloper))
				return (field.getAsFloatDefault(0) >= parm.parmvalue.getAsFloatDefault(0));
			if ("<=".equalsIgnoreCase(reloper))
				return (field.getAsFloatDefault(0) <= parm.parmvalue.getAsFloatDefault(0));
			if ("=".equalsIgnoreCase(reloper))
				return (field.getAsFloatDefault(0) == parm.parmvalue.getAsFloatDefault(0));
			if ("<>".equalsIgnoreCase(reloper))
				return (field.getAsFloatDefault(0) != parm.parmvalue.getAsFloatDefault(0));
		}
		default: {
			if (">".equalsIgnoreCase(reloper))
				return (field.getValue().compareTo(parm.parmvalue.getValue()) > 0);
			if ("<".equalsIgnoreCase(reloper))
				return (field.getValue().compareTo(parm.parmvalue.getValue()) < 0);
			if (">=".equalsIgnoreCase(reloper))
				return (field.getValue().compareTo(parm.parmvalue.getValue()) >= 0);
			if ("<=".equalsIgnoreCase(reloper))
				return (field.getValue().compareTo(parm.parmvalue.getValue()) <= 0);
			if ("=".equalsIgnoreCase(reloper))
				return (field.getValue().equalsIgnoreCase(parm.parmvalue.getValue()));
			if ("<>".equalsIgnoreCase(reloper))
				return (!field.getValue().equalsIgnoreCase(parm.parmvalue.getValue()));
			if ("like".equalsIgnoreCase(reloper)) {
				String v = field.getValue();
				String pv = parm.parmvalue.getValue();
				//System.out.println(field.getFieldname() + " " + v + " like " + pv);
				if (pv.startsWith("%") && pv.endsWith("%")) {
					pv = pv.replace("%", "");
					return v.indexOf(pv) >= 0;
				} else if (pv.startsWith("%")) {
					pv = pv.replace("%", "");
					return v.endsWith(pv);
				} else if (pv.endsWith("%")) {
					pv = pv.replace("%", "");
					return v.startsWith(pv);
				} else {
					return v.equalsIgnoreCase(pv);
				}
			}
		}
		}
		return false;
	}
}
