package com.corsair.server.genco;

import java.sql.ResultSetMetaData;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.sf.json.JSONObject;

import com.corsair.dbpool.CDBConnection;
import com.corsair.dbpool.CDBPool;
import com.corsair.dbpool.DBPools;
import com.corsair.dbpool.util.CResultSetMetaData;
import com.corsair.dbpool.util.CResultSetMetaDataItem;
import com.corsair.server.base.CSContext;
import com.corsair.server.retention.ACO;
import com.corsair.server.retention.ACOAction;
import com.corsair.server.util.CorUtil;

/**
 * 开发CO
 * 
 * @author Administrator
 *
 */
@ACO(coname = "web.dev")
public class CODevloper {
	String sqltemmssql2015 = "	select DB_NAME() dbname,'%s' tbname,c.name columnname,"
			+ "	coment=isnull(cast(p.value as nvarchar(50)),''), "
			+ "	iskey=case when exists( "
			+ "	    select 1 from "
			+ "	     (SELECT cs.column_name"
			+ "	       FROM information_schema.COLUMNS cs,information_schema.KEY_COLUMN_USAGE cu,"
			+ "	            information_schema.TABLE_CONSTRAINTS tc"
			+ "	       where cs.table_catalog=cu.table_catalog "
			+ "	          and cs.table_schema=cu.table_schema"
			+ "	          and cs.column_name=cu.column_name"
			+ "	          and tc.table_schema=cs.table_schema"
			+ "	          and tc.constraint_type='PRIMARY KEY'"
			+ "	          and tc.constraint_name=cu.constraint_name"
			+ "	          and cs.table_name=cu.table_name"
			+ "	          and cs.table_name=tc.table_name"
			+ "	          and cs.table_name='%s')tb"
			+ "	       where tb.column_name=c.name"
			+ "		  )"
			+ "		  then  1 else 0 end"
			+ "	from sys.columns c "
			+ "	left join sys.extended_properties p"
			+ "	on c.object_id=p.major_id and c.column_id=p.minor_id"
			+ "	where c.object_id=object_id('%s')";

	@ACOAction(eventname = "createJPAClass", notes = "创建JPAClass", Authentication = true)
	public String createJPAClass() throws Exception {
		HashMap<String, String> parms = CSContext.parPostDataParms();
		String poolname = CorUtil.hashMap2Str(parms, "poolname", "需要参数poolname");
		String jpaclassname = CorUtil.hashMap2Str(parms, "jpaclassname", "需要参数jpaclassname");
		String sqlstr = CorUtil.hashMap2Str(parms, "sqlstr", "需要参数sqlstr");

		CDBPool pool = DBPools.poolByName(poolname);
		if (pool == null)
			throw new Exception("数据库连接池【" + poolname + "】未发现");
		JSONObject jo = new JSONObject();
		jo.put("rst", createJPAClass(pool, jpaclassname, sqlstr));
		return jo.toString();
	}

	public String createJPAClass(CDBPool pool, String jpaclassname, String sqlstr) throws Exception {
		List<HashMap<String, String>> tbinfos = getTBInfos(pool, jpaclassname);
		String tbcomment = getTBComment(pool, jpaclassname);
		sqlstr = "select * from (" + sqlstr + ") tb where 1=0";
		CResultSetMetaData<CResultSetMetaDataItem> clolums = pool.getsqlMetadata(sqlstr);
		List<String> ClassStrs = new ArrayList<String>();
		ClassStrs.add("import com.corsair.cjpa.CField;");
		ClassStrs.add("import com.corsair.cjpa.CJPALineData;");
		ClassStrs.add("import com.corsair.cjpa.util.CEntity;");
		ClassStrs.add("import com.corsair.cjpa.util.CFieldinfo;");
		ClassStrs.add("import com.corsair.server.cjpa.CJPA;");
		ClassStrs.add("import java.sql.Types;");
		ClassStrs.add("");
		if (tbcomment == null)
			ClassStrs.add("@CEntity(tablename=\"" + jpaclassname + "\")");
		else
			ClassStrs.add("@CEntity(caption=\"" + tbcomment + "\",tablename=\"" + jpaclassname + "\")");
		ClassStrs.add("public class " + jpaclassname + " extends CJPA {");

		for (CResultSetMetaDataItem col : clolums) {
			String fdname = col.getColumnLabel();
			String vfdname = fdname;
			if (pool.getDbtype() == CDBConnection.DBType.oracle) {
				vfdname = vfdname.toLowerCase();
			}
			String comments = getCommentEx(pool, tbinfos, fdname);
			String fdinfo = "@CFieldinfo(fieldname =\"" + vfdname + "\",";
			fdinfo = (isPrivkey(pool, tbinfos, fdname)) ? (fdinfo + "iskey=true,") : fdinfo;
			fdinfo = (col.isAutoIncrement()) ? (fdinfo + "autoinc=true,") : fdinfo;
			fdinfo = (ResultSetMetaData.columnNoNulls == col.getNullable()) ? (fdinfo + "notnull=true,") : fdinfo;
			fdinfo = fdinfo + "precision=" + col.getPrecision() + ",";
			fdinfo = fdinfo + "scale=" + col.getScale() + ",";
			fdinfo = fdinfo + "caption=\"" + comments + "\",";
			String dv = getDefValue(pool, tbinfos, fdname);
			fdinfo = (dv != null) ? (fdinfo + "defvalue=\"" + dv + "\",") : fdinfo;
			fdinfo = fdinfo + "datetype=" + getJavaType(col.getColumnType());
			fdinfo = fdinfo + ")";
			ClassStrs.add("	" + fdinfo);

			ClassStrs.add("	public CField " + vfdname + ";  //" + comments);
		}

		ClassStrs.add("	public String SqlWhere; //查询附加条件");
		ClassStrs.add("	public int MaxCount; //查询最大数量");
		ClassStrs.add("");
		ClassStrs.add("     //自关联数据定义");
		ClassStrs.add("");
		ClassStrs.add("");

		ClassStrs.add("	public " + jpaclassname + "() throws Exception {");
		ClassStrs.add("	}");
		ClassStrs.add("");

		ClassStrs.add("	@Override");
		ClassStrs.add("	public boolean InitObject() {//类初始化调用的方法");
		ClassStrs.add("		super.InitObject();");
		// ClassStrs.addStrings(FKeyFields);
		// ClassStrs.addStrings(FNotNullFields);
		// ClassStrs.addStrings(FDataTypes);
		ClassStrs.add("		return true;");
		ClassStrs.add("	} ");
		ClassStrs.add("");
		ClassStrs.add("	@Override");
		ClassStrs.add("	public boolean FinalObject() { //类释放前调用的方法");
		ClassStrs.add("		super.FinalObject(); ");
		ClassStrs.add("		return true; ");
		ClassStrs.add("	} ");
		ClassStrs.add("} ");

		return getliststr(ClassStrs);
	}

	@ACOAction(eventname = "createHTMLTemple", notes = "创建HTML Inputs", Authentication = true)
	public String createHTMLTemple() throws Exception {
		HashMap<String, String> parms = CSContext.parPostDataParms();
		String poolname = CorUtil.hashMap2Str(parms, "poolname", "需要参数poolname");
		String jpaclassname = CorUtil.hashMap2Str(parms, "jpaclassname", "需要参数jpaclassname");
		String sqlstr = CorUtil.hashMap2Str(parms, "sqlstr", "需要参数sqlstr");
		int cns = Integer.valueOf(CorUtil.hashMap2Str(parms, "cols", "需要参数cols"));
		CDBPool pool = DBPools.poolByName(poolname);
		if (pool == null)
			throw new Exception("数据库连接池【" + poolname + "】未发现");
		List<HashMap<String, String>> tbinfos = getTBInfos(pool, jpaclassname);
		sqlstr = "select * from (" + sqlstr + ") tb where 1=0";
		CResultSetMetaData<CResultSetMetaDataItem> clolums = pool.getsqlMetadata(sqlstr);
		String atab = "	";
		int rows = ((clolums.size() % cns) == 0) ? (clolums.size() / cns) : ((clolums.size() / cns) + 1);
		List<String> htmls = new ArrayList<String>();
		for (int i = 0; i < rows; i++) {
			int idx = i * cns;
			if (idx >= clolums.size())
				break;
			htmls.add("<tr>");
			for (int j = 0; j < cns; j++) {
				idx = i * cns + j;
				if (idx >= clolums.size())
					break;
				CResultSetMetaDataItem smti = clolums.get(idx);
				htmls.add(atab + "<td cjoptions=\"fdname:'" + smti.getColumnLabel() + "'\">" + getCommentEx(pool, tbinfos, smti.getColumnLabel()) + "</td>");
				String clsname = "";
				String fmter = "";
				if (isDateType(smti.getColumnType())) {
					clsname = "easyui-datetimebox";
					fmter = "data-options=\"formatter:$dateformattostr\"";
				} else {
					clsname = "easyui-textbox";
				}
				String notnull = (smti.getNullable() == 0) ? ",required:true" : "";
				String tems = "<td><input class=\"" + clsname + "\" cjoptions=\"fdname:'" + smti.getColumnLabel() + "'" + notnull + "\"" + fmter
						+ " style=\"height:20px;width: 100px\"/></td>";
				htmls.add(atab + tems);
			}
			htmls.add("</tr>");
		}
		JSONObject jo = new JSONObject();
		jo.put("rst", getliststr(htmls));
		return jo.toString();
	}

	@ACOAction(eventname = "createHTMLTemplenew", notes = "创建HTML Inputs", Authentication = true)
	public String createHTMLTemplenew() throws Exception {
		HashMap<String, String> parms = CSContext.parPostDataParms();
		String poolname = CorUtil.hashMap2Str(parms, "poolname", "需要参数poolname");
		String jpaclassname = CorUtil.hashMap2Str(parms, "jpaclassname", "需要参数jpaclassname");
		String sqlstr = CorUtil.hashMap2Str(parms, "sqlstr", "需要参数sqlstr");
		int cns = Integer.valueOf(CorUtil.hashMap2Str(parms, "cols", "需要参数cols"));
		CDBPool pool = DBPools.poolByName(poolname);
		if (pool == null)
			throw new Exception("数据库连接池【" + poolname + "】未发现");
		List<HashMap<String, String>> tbinfos = getTBInfos(pool, jpaclassname);
		sqlstr = "select * from (" + sqlstr + ") tb where 1=0";
		CResultSetMetaData<CResultSetMetaDataItem> clolums = pool.getsqlMetadata(sqlstr);
		String atab = "	";
		int rows = ((clolums.size() % cns) == 0) ? (clolums.size() / cns) : ((clolums.size() / cns) + 1);
		List<String> htmls = new ArrayList<String>();
		for (int i = 0; i < rows; i++) {
			int idx = i * cns;
			if (idx >= clolums.size())
				break;
			htmls.add("<tr>");
			for (int j = 0; j < cns; j++) {
				idx = i * cns + j;
				if (idx >= clolums.size())
					break;
				CResultSetMetaDataItem smti = clolums.get(idx);
				htmls.add(atab + "<td cjoptions=\"fdname:'" + smti.getColumnLabel() + "'\">" + getCommentEx(pool, tbinfos, smti.getColumnLabel()) + "</td>");
				String clsname = "";
				String fmter = "";
				if (isDateType(smti.getColumnType())) {
					clsname = "easyui-datetimebox";
					fmter = ",formatter:$dateformattostr";
				} else {
					clsname = "easyui-textbox";
				}
				String notnull = (smti.getNullable() == 0) ? ",crequired:true" : "";
				String tems = "<td><input cjoptions=\"easyui_class:'" + clsname + "',fdname:'" + smti.getColumnLabel() + "'" + notnull + fmter + "\""
						+ " style=\"height:20px;width: 100px\"/></td>";
				htmls.add(atab + tems);
			}
			htmls.add("</tr>");
		}
		JSONObject jo = new JSONObject();
		jo.put("rst", getliststr(htmls));
		return jo.toString();
	}

	@ACOAction(eventname = "createJS", notes = "创建HTML JS", Authentication = true)
	public String createJS() throws Exception {
		HashMap<String, String> parms = CSContext.parPostDataParms();
		String poolname = CorUtil.hashMap2Str(parms, "poolname", "需要参数poolname");
		String jpaclassname = CorUtil.hashMap2Str(parms, "jpaclassname", "需要参数jpaclassname");
		String sqlstr = CorUtil.hashMap2Str(parms, "sqlstr", "需要参数sqlstr");

		CDBPool pool = DBPools.poolByName(poolname);
		if (pool == null)
			throw new Exception("数据库连接池【" + poolname + "】未发现");

		List<HashMap<String, String>> tbinfos = getTBInfos(pool, jpaclassname);

		sqlstr = "select * from (" + sqlstr + ") tb where 1=0";
		CResultSetMetaData<CResultSetMetaDataItem> clolums = pool.getsqlMetadata(sqlstr);
		List<String> htmljss = new ArrayList<String>();
		htmljss.add("var gdListColumns=[");
		for (CResultSetMetaDataItem col : clolums) {
			String fdname = col.getColumnLabel();
			String comments = getCommentEx(pool, tbinfos, fdname);
			htmljss.add("	{field:'" + fdname + "',title:'" + comments + "',width:64},");
		}
		if (htmljss.size() > 0) {
			String temstr = htmljss.get(htmljss.size() - 1);
			htmljss.set(htmljss.size() - 1, temstr.substring(0, temstr.length() - 1));
		}
		htmljss.add("]");
		JSONObject jo = new JSONObject();
		jo.put("rst", getliststr(htmljss));
		return jo.toString();
	}

	private boolean isDateType(int dttype) {
		if ((dttype == Types.TIME) || (dttype == Types.DATE) || (dttype == Types.TIMESTAMP))
			return true;
		return false;
	}

	private String getliststr(List<String> ClassStrs) {
		String rst = "";
		for (String s : ClassStrs) {
			rst = rst + s + "\r\n";
		}
		return rst;
	}

	private String getCommentEx(CDBPool pool, List<HashMap<String, String>> tbinfos, String fdname) {
		String rst = fdname;
		if (tbinfos == null)
			return rst;
		for (HashMap<String, String> fdinfo : tbinfos) {
			// System.out.println(pool.getDbtype() + ":" + CDBConnection.DBType.oracle);
			if (pool.getDbtype() == CDBConnection.DBType.mysql) {
				if ((pool.pprm.schema.equals(fdinfo.get("TABLE_SCHEMA"))) && (fdname.equals(fdinfo.get("COLUMN_NAME")))) {
					rst = fdinfo.get("COLUMN_COMMENT");
					break;
				}
			} else if (pool.getDbtype() == CDBConnection.DBType.sqlserver) {
				if ((pool.pprm.schema.equals(fdinfo.get("dbname"))) && (fdname.equals(fdinfo.get("columnname")))) {
					rst = fdinfo.get("coment");
					break;
				}
			} else if (pool.getDbtype() == CDBConnection.DBType.oracle) {
				// System.out.println(fdname.toUpperCase() + ":" + fdinfo.get("column_name"));
				if (fdname.equalsIgnoreCase(fdinfo.get("column_name"))) {
					rst = fdinfo.get("comments");
					break;
				}
			} else {
				rst = fdname;
			}
		}
		if ((rst == null) || (rst.isEmpty()))
			rst = fdname;
		return rst;
	}

	private String getDefValue(CDBPool pool, List<HashMap<String, String>> tbinfos, String fdname) {
		if (tbinfos == null)
			return null;
		for (HashMap<String, String> fdinfo : tbinfos) {
			if (pool.getDbtype() == CDBConnection.DBType.mysql) {
				if ((pool.pprm.schema.equals(fdinfo.get("TABLE_SCHEMA"))) && (fdname.equals(fdinfo.get("COLUMN_NAME")))) {
					String dv = fdinfo.get("COLUMN_DEFAULT");
					return ((dv == null) || (dv.isEmpty())) ? null : dv;
				}
			} else if (pool.getDbtype() == CDBConnection.DBType.sqlserver) {
				if ((pool.pprm.schema.equals(fdinfo.get("dbname"))) && (fdname.equals(fdinfo.get("columnname")))) {
					return null;
				}
			} else
				return null;
		}
		return null;

	}

	private boolean isPrivkey(CDBPool pool, List<HashMap<String, String>> tbinfos, String fdname) {
		if (tbinfos == null)
			return false;
		for (HashMap<String, String> fdinfo : tbinfos) {
			if (pool.getDbtype() == CDBConnection.DBType.mysql) {
				if ((pool.pprm.schema.equals(fdinfo.get("TABLE_SCHEMA"))) && (fdname.equals(fdinfo.get("COLUMN_NAME")))) {
					return "PRI".equals(fdinfo.get("COLUMN_KEY"));
				}
			} else if (pool.getDbtype() == CDBConnection.DBType.sqlserver) {
				if ((pool.pprm.schema.equals(fdinfo.get("dbname"))) && (fdname.equals(fdinfo.get("columnname")))) {
					return ("1".equals(fdinfo.get("iskey")));
				}
			} else
				return false;
		}
		return false;
	}

	// private boolean getFieldAllowNULLEx(CDBPool pool, List<HashMap<String,
	// String>> tbinfos, String fdname) {
	//
	// }

	private String getJavaType(int tp) {
		switch (tp) {
		case -7:
			return "Types.BIT";
		case -6:
			return "Types.TINYINT";
		case 5:
			return "Types.SMALLINT";
		case 4:
			return "Types.INTEGER";
		case -5:
			return "Types.BIGINT";
		case 6:
			return "Types.FLOAT";
		case 7:
			return "Types.REAL";
		case 8:
			return "Types.DOUBLE";
		case 2:
			return "Types.NUMERIC";
		case 3:
			return "Types.DECIMAL";
		case 1:
			return "Types.CHAR";
		case 12:
			return "Types.VARCHAR";
		case -1:
			return "Types.LONGVARCHAR";
		case 91:
			return "Types.DATE";
		case 92:
			return "Types.TIME";
		case 93:
			return "Types.TIMESTAMP";
		case -2:
			return "Types.BINARY";
		case -3:
			return "Types.VARBINARY";
		case -4:
			return "Types.LONGVARBINARY";
		case 0:
			return "Types.NULL";
		case 1111:
			return "Types.OTHER";
		case 2000:
			return "Types.JAVA_OBJECT";
		case 2001:
			return "Types.DISTINCT";
		case 2002:
			return "Types.STRUCT";
		case 2003:
			return "Types.ARRAY";
		case 2004:
			return "Types.BLOB";
		case 2005:
			return "Types.CLOB";
		case 2006:
			return "Types.REF";
		case 70:
			return "Types.DATALINK";
		case 16:
			return "Types.BOOLEAN";
		case -8:
			return "Types.ROWID";
		case -15:
			return "Types.NCHAR";
		case -9:
			return "Types.NVARCHAR";
		case -16:
			return "Types.LONGNVARCHAR";
		case 2011:
			return "Types.NCLOB";
		case 2009:
			return "Types.SQLXML";
		}
		return "Types.VARCHAR";
	}

	private List<HashMap<String, String>> getTBInfos(CDBPool pool, String jpaclassname) {
		String colssql = null;
		if (pool.getDbtype() == CDBConnection.DBType.mysql) {
			colssql = "SELECT  * from Information_schema.columns where upper(table_name)='"
					+ jpaclassname.toUpperCase() + "' and table_schema='" + pool.pprm.schema.toUpperCase() + "'";
		}
		if (pool.getDbtype() == CDBConnection.DBType.sqlserver) {
			colssql = String.format(sqltemmssql2015, jpaclassname.toLowerCase(), jpaclassname.toLowerCase(), jpaclassname.toLowerCase());
		}
		if (pool.getDbtype() == CDBConnection.DBType.oracle) {
			colssql = "select * from all_col_comments where OWNER='" + pool.pprm.schema.toUpperCase()
					+ "' and TABLE_NAME='" + jpaclassname.toUpperCase() + "'";
		}
		List<HashMap<String, String>> tbinfos = null;
		if (colssql != null) {
			try {
				tbinfos = pool.openSql2List(colssql);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return tbinfos;
	}

	private String getTBComment(CDBPool pool, String jpaclassname) {
		String colssql = null;
		if (pool.getDbtype() == CDBConnection.DBType.mysql) {
			colssql = "SELECT TABLE_COMMENT FROM Information_schema.TABLES WHERE upper(TABLE_NAME)='"
					+ jpaclassname.toUpperCase() + "' and TABLE_SCHEMA='" + pool.pprm.schema.toUpperCase() + "'";
		}
		if (pool.getDbtype() == CDBConnection.DBType.oracle) {
			colssql = "select * from all_tab_comments where OWNER='" + pool.pprm.schema.toUpperCase()
					+ "' and TABLE_NAME='" + jpaclassname.toUpperCase() + "'";
		}

		if (colssql != null) {
			try {
				List<HashMap<String, String>> rows = pool.openSql2List(colssql);
				if ((rows == null) || (rows.size() == 0))
					return null;
				Object co = null;
				if (pool.getDbtype() == CDBConnection.DBType.mysql)
					co = rows.get(0).get("TABLE_COMMENT");
				else if (pool.getDbtype() == CDBConnection.DBType.oracle)
					co = rows.get(0).get("COMMENTS");
				if (co == null)
					return null;
				if (co.toString().isEmpty())
					return null;
				return co.toString();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}
}
