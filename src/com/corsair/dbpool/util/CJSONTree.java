package com.corsair.dbpool.util;

import java.io.ByteArrayOutputStream;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPABase;
import com.corsair.dbpool.CDBConnection.DBType;

/**
 * 树型JSON处理
 * 
 * @author Administrator
 *
 */
public class CJSONTree {

	public static String Dataset2JSONTree(ResultSet rs, String idfd, String pidfd, boolean async) throws Exception {
		List<HashMap<String, String>> rows = Dataset2List(rs);
		List<HashMap<String, String>> rootrows = getRootRows(rows, idfd, pidfd);
		return list2Json(rootrows, rows, idfd, pidfd, async);
	}

	public static JSONArray Dataset2JSON(ResultSet rs, String idfd, String pidfd, boolean async) throws Exception {
		List<HashMap<String, String>> rows = Dataset2List(rs);
		// System.out.println("rows:" + rows.size());
		List<HashMap<String, String>> rootrows = getRootRows(rows, idfd, pidfd);
		// System.out.println("rootrows:" + rootrows.size());
		return list2json_o(rootrows, rows, idfd, pidfd, async);
	}

	private static JSONArray list2json_o(List<HashMap<String, String>> rows, List<HashMap<String, String>> allrows, String idfd, String pidfd, boolean async) {
		JSONArray js = new JSONArray();
		for (HashMap<String, String> row : rows) {
			js.add(hashmap2json_o(row, allrows, idfd, pidfd, async));
		}
		return js;
	}

	private static String list2Json(List<HashMap<String, String>> rows, List<HashMap<String, String>> allrows, String idfd, String pidfd, boolean async)
			throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		JsonFactory jf = new JsonFactory();
		JsonGenerator jg = jf.createJsonGenerator(baos);
		jg.writeStartArray();
		for (HashMap<String, String> row : rows) {
			jg.writeRawValue(hashmap2json(row, allrows, idfd, pidfd, async));
		}
		jg.writeEndArray();
		jg.close();
		return baos.toString("utf-8");
	}

	private static List<HashMap<String, String>> getChildRows(HashMap<String, String> row, List<HashMap<String, String>> allrows, String idfd, String pidfd) {
		List<HashMap<String, String>> chdrows = new ArrayList<HashMap<String, String>>();
		for (HashMap<String, String> chdrow : allrows) {
			if (chdrow.get(pidfd).equalsIgnoreCase(row.get(idfd))) {
				chdrows.add(chdrow);
			}
		}
		return chdrows;
	}

	public static List<HashMap<String, String>> Dataset2List(ResultSet rs) throws Exception {
		return Dataset2List(DBType.mysql, rs);
	}

	public static List<HashMap<String, String>> Dataset2List(DBType dbtype, ResultSet rs) throws Exception {
		List<HashMap<String, String>> datalist = new ArrayList<HashMap<String, String>>();
		ResultSetMetaData rsmd = rs.getMetaData(); // 得到结果集的定义结构
		int columnCount = rsmd.getColumnCount();
		while (rs.next()) {
			HashMap<String, String> rowdata = new HashMap<String, String>();
			for (int i = 1; i <= columnCount; i++) {
				String fdname = rsmd.getColumnLabel(i);
				if (dbtype == DBType.oracle)
					fdname = fdname.toLowerCase();
				rowdata.put(fdname, getvalue(rs, i, rsmd.getColumnType(i)));
			}
			datalist.add(rowdata);
		}
		return datalist;
	}

	private static JSONObject hashmap2json_o(HashMap<String, String> row, List<HashMap<String, String>> allrows, String idfd, String pidfd, boolean async) {
		JSONObject rst = new JSONObject();
		Iterator<Entry<String, String>> iter = row.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<String, String> entry = iter.next();
			rst.put(entry.getKey(), entry.getValue());
		}
		if (!async) {
			List<HashMap<String, String>> chdrows = getChildRows(row, allrows, idfd, pidfd);
			if (chdrows.size() > 0) {
				JSONArray js = new JSONArray();
				for (HashMap<String, String> chdrow : chdrows) {
					JSONObject jo = hashmap2json_o(chdrow, allrows, idfd, pidfd, async);
					js.add(jo);
				}
				rst.put("children", js);
			}
		}
		return rst;
	}

	private static String hashmap2json(HashMap<String, String> row, List<HashMap<String, String>> allrows, String idfd, String pidfd, boolean async)
			throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		JsonFactory jf = new JsonFactory();
		JsonGenerator jg = jf.createJsonGenerator(baos);
		jg.writeStartObject();

		Iterator<Entry<String, String>> iter = row.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<String, String> entry = iter.next();
			jg.writeStringField(entry.getKey(), entry.getValue());
		}

		if (!async) {
			List<HashMap<String, String>> chdrows = getChildRows(row, allrows, idfd, pidfd);
			if (chdrows.size() > 0) {
				jg.writeArrayFieldStart("children");
				for (HashMap<String, String> chdrow : chdrows) {
					jg.writeRawValue(hashmap2json(chdrow, allrows, idfd, pidfd, async));
				}
				jg.writeEndArray();

			}
		}

		jg.writeEndObject();
		jg.close();
		return baos.toString("utf-8");
	}

	/**
	 * 逻辑，对每一行如果没有任何一行是他的父亲，则为根
	 * 
	 * @param datalist
	 * @param idfd
	 * @param pidfd
	 * @return
	 */
	private static List<HashMap<String, String>> getRootRows(List<HashMap<String, String>> datalist, String idfd, String pidfd) {
		List<HashMap<String, String>> rootrows = new ArrayList<HashMap<String, String>>();
		for (HashMap<String, String> row : datalist) {
			boolean fd = false;
			String pid = row.get(pidfd);
			for (int i = 0; i < datalist.size(); i++) {
				HashMap<String, String> row1 = datalist.get(i);
				String id = row1.get(idfd);
				// System.out.println("pid:" + pid + ";id:" + pid);
				if (pid.equalsIgnoreCase(id)) {
					fd = true;
					break;
				}
			}
			if (!fd) {
				rootrows.add(row);
			}
		}
		return rootrows;
	}

	private static String getvalue(final ResultSet rs, int colNum, int type) throws SQLException {
		switch (type) {
		case Types.ARRAY:
		case Types.BLOB:
		case Types.CLOB:
		case Types.DISTINCT:
		case Types.LONGVARBINARY:
		case Types.VARBINARY:
		case Types.BINARY:
		case Types.REF:
		case Types.STRUCT:
			return "undefined";
		case Types.DATE:
		case Types.TIMESTAMP: {

			Object value = null;
			try {
				value = rs.getObject(colNum);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (rs.wasNull() || (value == null))
				return (null);
			else {
				String v = value.toString();
				if (v.substring(v.length() - 2, v.length()).endsWith(".0"))
					v = v.substring(0, v.length() - 2);
				return (v);
			}
		}

		default: {
			Object value = rs.getObject(colNum);
			if (rs.wasNull() || (value == null))
				return (null);// ConstsSw.xml_null
			else {
				// System.out.println(type);
				// System.out.println(value.toString());
				return (value.toString());
			}
		}
		}
	}

}
