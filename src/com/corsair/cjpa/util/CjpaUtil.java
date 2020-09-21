package com.corsair.cjpa.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import com.corsair.cjpa.CField;
import com.corsair.cjpa.CJPABase;
import com.corsair.cjpa.CJPALineData;
import com.corsair.dbpool.CDBConnection;
import com.corsair.dbpool.CDBPool;
import com.corsair.dbpool.CDBConnection.DBType;
import com.corsair.dbpool.DBPools;
import com.corsair.dbpool.util.CJSON;
import com.corsair.dbpool.util.CPoolSQLUtil;
import com.corsair.dbpool.util.JSONParm;

/**
 * 实体工具类
 * 
 * @author Administrator
 *
 */
public class CjpaUtil {
	private final static String sqlwhere = "parmname_sqlwhere";

	public static String getUpdatevalue1(final ResultSet rs, int colNum, int type) throws SQLException {
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
				return ("NULL");
			else {
				String v = value.toString();
				// System.out.println(v);
				if (v.substring(v.length() - 2, v.length()).endsWith(".0"))
					v = v.substring(0, v.length() - 2);
				return (v);
			}
		}

		default: {
			Object value = rs.getObject(colNum);
			if (rs.wasNull() || (value == null))
				return ("NULL");
			else {
				// System.out.println(type);
				// System.out.println(value.toString());
				return (value.toString());
			}
		}
		}
	}

	/**
	 * 获取字段名
	 * 
	 * @param rsmd
	 * @param con
	 * @return
	 * @throws Exception
	 */
	public static String getFdName(ResultSetMetaData rsmd, int con) throws Exception {// MySQL的bug，所以要分开取字段名
		return rsmd.getColumnLabel(con);
	}

	/**
	 * 获取显示的值
	 * 
	 * @param dbtype
	 * @param rs
	 * @param colNum
	 * @param type
	 * @return
	 * @throws SQLException
	 */
	public static String getShowvalue(DBType dbtype, final ResultSet rs, int colNum, int type) throws SQLException {
		switch (type) {
		case Types.ARRAY:
		case Types.BLOB:
		case Types.CLOB:
		case Types.DISTINCT:
			return null;
		case Types.LONGVARBINARY:
		case Types.VARBINARY: {
			try {
				InputStream in = rs.getBinaryStream(colNum);
				return GetImageStrByInPut(in);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		case Types.BINARY:
		case Types.REF:
		case Types.STRUCT:
			return null;
		case Types.DATE:
		case Types.TIMESTAMP: {
			Object value = null;
			try {
				value = rs.getObject(colNum);
			} catch (Exception e) {
				// e.printStackTrace();
			}
			if (rs.wasNull() || (value == null))
				return ("");
			else {
				String v = value.toString();
				// if (dbtype == DBType.mysql)
				if (v.substring(v.length() - 2, v.length()).endsWith(".0"))
					v = v.substring(0, v.length() - 2);
				return (v);
			}
		}
		case Types.BIT:// 对应到java 的boolean
			Object vo = rs.getObject(colNum);
			if (rs.wasNull() || (vo == null))
				return ("");
			else {
				String v = vo.toString();
				return Boolean.valueOf(v) ? "1" : "0";
			}
		default: {
			Object value = rs.getObject(colNum);
			if (rs.wasNull() || (value == null))
				return ("");
			else {
				// System.out.println(type);
				// System.out.println(value.toString());
				return value.toString();
			}
		}
		}

	}

	/**
	 * 读取输入流,转换为Base64字符串
	 * 
	 * @param input
	 * @return
	 */
	public static String GetImageStrByInPut(InputStream input) {
		if (input == null)
			return null;
		byte[] result;
		try {
			byte[] temp = new byte[input.available()];
			result = new byte[0];
			int size = 0;
			while ((size = input.read(temp)) != -1) {
				byte[] readBytes = new byte[size];
				System.arraycopy(temp, 0, readBytes, 0, size);
				result = mergeArray(result, readBytes);
			}
			// 对字节数组Base64编码
			BASE64Encoder encoder = new BASE64Encoder();
			return encoder.encode(result);// 返回Base64编码过的字节数组字符串
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	/***
	 * 合并字节数组
	 * 
	 * @param a
	 * @return
	 */
	public static byte[] mergeArray(byte[]... a) {
		// 合并完之后数组的总长度
		int index = 0;
		int sum = 0;
		for (int i = 0; i < a.length; i++) {
			sum = sum + a[i].length;
		}
		byte[] result = new byte[sum];
		for (int i = 0; i < a.length; i++) {
			int lengthOne = a[i].length;
			if (lengthOne == 0) {
				continue;
			}
			// 拷贝数组
			System.arraycopy(a[i], 0, result, index, lengthOne);
			index = index + lengthOne;
		}
		return result;
	}

	/**
	 * base64字符串转化成图片 对字节数组字符串进行Base64解码并生成图片
	 * 
	 * @param imgStr
	 * 数据内容(字符串)
	 * @param path
	 * 输出路径
	 * @return
	 */
	public static void generateImage(String imgStr, OutputStream out) {
		if (imgStr == null) // 图像数据为空
			return;
		BASE64Decoder decoder = new BASE64Decoder();
		try {
			byte[] b = decoder.decodeBuffer(imgStr);// Base64解码
			for (int i = 0; i < b.length; ++i) {
				if (b[i] < 0) {// 调整异常数据
					b[i] += 256;
				}
			}
			// 生成jpeg图片
			out.write(b);
			return;
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}

	/**
	 * 根据类名获取KEY字段
	 * 
	 * @param cls
	 * @return
	 * @throws Exception
	 */
	public static String getJPAIDFieldNameByClass(Class<?> cls) throws Exception {
		if (!CJPABase.class.isAssignableFrom(cls)) {
			throw new Exception(cls.getName() + "必须是 com.corsair.server.cjpa.CJPA的子类");
		}
		Field[] fields = cls.getFields();
		for (Field field : fields) {
			if (field.getGenericType() == CField.class) {
				if (!field.isAnnotationPresent(CFieldinfo.class))
					throw new Exception("JPA 字段未发现注解@CFieldinfo");
				CFieldinfo fdinfo = field.getAnnotation(CFieldinfo.class);
				if (fdinfo.iskey())
					return field.getName();
			}
		}
		return null;
	}

	/**
	 * 创建列表
	 * 
	 * @param className
	 * @return
	 * @throws Exception
	 */
	public static CJPALineData<CJPABase> newJPALinedatas(String className) throws Exception {
		Class<?> CJPAcd = Class.forName(className);
		if (!CJPABase.class.isAssignableFrom(CJPAcd)) {
			throw new Exception(className + "必须从 com.corsair.server.cjpa.CJPA继承");
		}
		@SuppressWarnings("unchecked")
		CJPALineData<CJPABase> linedatas = new CJPALineData<CJPABase>(null, (Class<CJPABase>) CJPAcd);
		return linedatas;
	}

	/**
	 * 创建JPA实例
	 * 
	 * @param className
	 * @return
	 * @throws Exception
	 */
	public static CJPABase newJPAObjcet(String className) throws Exception {
		try {
			if ((className == null) || className.isEmpty())
				throw new Exception("newJPAObjcet 类名为空!");
			Class<?> CJPAcd = Class.forName(className);
			if (!CJPABase.class.isAssignableFrom(CJPAcd)) {
				throw new Exception(className + "必须从 com.corsair.server.cjpa.CJPA继承");
			}
			Class<?> paramTypes[] = {};
			Constructor<?> cst = CJPAcd.getConstructor(paramTypes);
			Object o = cst.newInstance();
			return (CJPABase) (o);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new Exception("<" + className + ">" + e.getLocalizedMessage());
		}
	}

	/**
	 * 获取参数对象
	 * 
	 * @param jps
	 * @param fdname
	 * @return
	 */
	public static JSONParm getParm(List<JSONParm> jps, String fdname) {
		if (fdname == null)
			return null;
		for (JSONParm jp : jps) {
			String parmname = jp.getParmname();
			int idx = parmname.lastIndexOf('.');
			String fn = (idx == -1) ? parmname : parmname.substring(idx + 1);
			if (fdname.equalsIgnoreCase(fn))
				return jp;
		}
		return null;
	}

	/**
	 * 根据参数创建SQL
	 * 
	 * @param jpa
	 * @param jps
	 * @return
	 * @throws Exception
	 */
	public static String buildFindSqlByJsonParms(CJPABase jpa, List<JSONParm> jps) throws Exception {
		String where = "";
		if ((jps == null) || jps.isEmpty())
			return where;
		for (JSONParm jp : jps) {
			String parmname = jp.getParmname();
			int idx = parmname.lastIndexOf('.');
			String fdname = (idx == -1) ? parmname : parmname.substring(idx + 1);
			String svalue = jp.getParmvalue();
			if (sqlwhere.equalsIgnoreCase(parmname)) {
				where = where + " and " + svalue;
			} else {
				CField cfd = jpa.cfield(fdname);
				if (cfd == null)
					throw new Exception("字段" + fdname + "在JPA" + jpa.getClass().getName() + "未找到字段！");
				where = where + buildFindSqlByJsonParm(jpa.pool.getDbtype(), cfd.getFieldtype(), jp);
			}
		}
		return where;
	}

	/**
	 * 根据单个参数创建SQL
	 * 
	 * @param dbtype
	 * @param fieldtype
	 * @param jp
	 * @return
	 */
	public static String buildFindSqlByJsonParm(DBType dbtype, int fieldtype, JSONParm jp) {
		String parmname = jp.getParmname();
		String svalue = jp.getParmvalue();
		if (sqlwhere.equalsIgnoreCase(parmname)) {
			return " and " + svalue;
		} else {
			String reloper = jp.getReloper();
			if ("like".equalsIgnoreCase(reloper)) {
				if (CPoolSQLUtil.eInArray(fieldtype, CPoolSQLUtil.strFDType))
					svalue = "%" + svalue + "%";
				else
					reloper = "=";
				svalue = CJPASqlUtil.getSqlValue(dbtype, fieldtype, svalue);
			} else if ("in".equalsIgnoreCase(reloper)) {// in 不处理
				// svalue = svalue;
			} else
				svalue = CJPASqlUtil.getSqlValue(dbtype, fieldtype, svalue);
			return " and " + CJPASqlUtil.getSqlField(dbtype, parmname) + " " + reloper + " "
					+ svalue;
		}
	}

	/**
	 * 根据参数创建SQL
	 * 
	 * @param pool
	 * @param sqlstr
	 * @param parms
	 * @param notnullparms
	 * @return
	 * @throws Exception
	 */
	public static String buildFindSqlByJsonParms(CDBPool pool, String sqlstr, String parms, String[] notnullparms) throws Exception {
		return buildFindSqlByJsonParms(pool, sqlstr, parms, notnullparms, null);
	}

	/**
	 * 根据参数创建SQL
	 * 
	 * @param pool
	 * @param sqlstr
	 * @param parms
	 * @param notnullparms
	 * @param idpathw
	 * @return
	 * @throws Exception
	 */
	public static String buildFindSqlByJsonParms(CDBPool pool, String sqlstr, String parms, String[] notnullparms, String idpathw) throws Exception {
		return buildFindSqlByJsonParms(pool, sqlstr, parms, notnullparms, null, idpathw);
	}

	private static boolean strInArr(String str, String[] ignParms) {
		if (str == null)
			return false;
		for (String s : ignParms) {
			if (str.equalsIgnoreCase(s))
				return true;
		}
		return false;
	}

	/**
	 * @param pool
	 * @param sqlstr
	 * @param parms
	 * @param notnullparms
	 * 不允许为空的字段
	 * @param ignParms
	 * 略过的字段
	 * @param idpathw
	 * idpath条件
	 * @return sql 条件
	 * @throws Exception
	 */
	public static String buildFindSqlByJsonParms(CDBPool pool, String sqlstr, String parms, String[] notnullparms, String[] ignParms, String idpathw)
			throws Exception {
		// System.out.println("buildFindSqlByJsonParms:1111111111111111111111111");
		String where = "";
		if (((parms == null) || parms.isEmpty()) && ((notnullparms != null) && (notnullparms.length > 0)))
			throw new Exception("【" + notnullparms[0] + "】不能为空!");

		String sql = "select * from (" + sqlstr + ") tb where 1=0";
		List<JSONParm> jps = CJSON.getParms(parms);
		if ((ignParms != null) && (ignParms.length > 0)) {
			Iterator<JSONParm> it = jps.iterator();
			while (it.hasNext()) {
				JSONParm jp = it.next();
				if (strInArr(jp.getParmname(), ignParms))
					it.remove();
			}
		}

		CDBConnection con = pool.getCon(CjpaUtil.class.getName());
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = con.con.createStatement();
			if (DBPools.getCblog() != null) {
				DBPools.getCblog().writelog(con, "【计划执行SQL：" + sql + "】");
			}
			rs = stmt.executeQuery(sql);
			ResultSetMetaData rsmd = rs.getMetaData(); // 得到结果集的定义结构
			int colCount = rsmd.getColumnCount(); // 得到列的总数
			for (JSONParm jp : jps) {
				String parmname = jp.getParmname();
				if (parmname.equalsIgnoreCase(sqlwhere)) {
					where = where + " and " + jp.getParmvalue();
				} else {
					int idx = parmname.lastIndexOf('.');
					String fdname = (idx == -1) ? parmname : parmname.substring(idx + 1);
					for (int i = 1; i <= colCount; i++) {//
						int type = rsmd.getColumnType(i);
						String dbfdname = rsmd.getColumnLabel(i);
						if (fdname.equalsIgnoreCase(dbfdname)) {
							String odvalue = jp.getParmvalue();
							String svalue = odvalue;
							// myh 17-04-18 修改非字符类型 like 错误
							String reloper = jp.getReloper();
							// System.out.println("fdname:" + fdname);
							// System.out.println("type:" + type);
							if (CPoolSQLUtil.eInArray(type, CPoolSQLUtil.strFDType)) {
								if ("like".equalsIgnoreCase(reloper))
									svalue = "%" + svalue + "%";
								svalue = CJPASqlUtil.getSqlValue(pool.getDbtype(), type, svalue);
							} else {
								if ("like".equalsIgnoreCase(jp.getReloper()))
									reloper = "=";
								svalue = CJPASqlUtil.getSqlValue(pool.getDbtype(), type, svalue);
							}

							where = where + " and " + CJPASqlUtil.getSqlField(pool.getDbtype(), parmname)
									+ " " + reloper
									+ " " + svalue;
							break;
						}
					}
				}

			}
			// //idpath处理////
			// System.out.println("idpath处理 :" + colCount);
			for (int i = 1; i <= colCount; i++) {//
				String dbfdname = rsmd.getColumnLabel(i);
				// System.out.println("idpath 处理 字段名：" + dbfdname);
				if ("idpath".equalsIgnoreCase(dbfdname)) {
					// System.out.println("有IDpath字段");
					if ((idpathw != null) && (!idpathw.isEmpty())) {
						where = where + idpathw;
					}
					break;
				}
			}

			rs.close();
			// System.out.println("notnullparms:" + notnullparms.length);
			if (notnullparms != null)
				for (String pname : notnullparms) {
					// System.out.println("pname:" + pname);
					boolean find = false;
					for (JSONParm jp : jps) {
						String fdname = jp.getParmname();
						if (pname.equals(fdname)) {
							find = true;
							break;
						}
					}
					if (!find) {
						throw new Exception("【" + pname + "】不能为空!");
					}
				}

			return where;
		} finally {
			DBPools.safeCloseS_R(stmt, rs);
			con.close();
		}
	}

	// ///////////////////////////
	// statics//////////////////////////////
	/**
	 * 根据类获取字段列表
	 * 
	 * @param cls
	 * @param thr_err
	 * @return
	 * @throws Exception
	 */
	public static List<CLinkFieldInfo> getLineLinkedInfos(Class<?> cls, boolean thr_err) throws Exception {
		List<CLinkFieldInfo> rst = new ArrayList<CLinkFieldInfo>();
		Field[] fields = cls.getFields();
		for (Field field : fields) {
			if (field.getType() == CJPALineData.class) {
				if (field.isAnnotationPresent(CLinkFieldInfo.class)) {
					CLinkFieldInfo cfi = field.getAnnotation(CLinkFieldInfo.class);
					if (cfi.linkFields().length == 0)
						throw new Exception("类【" + cls.getName() + "】属性【" + field.getName() + "】注解@CLinkFieldInfo未设置关联字段");
					rst.add(cfi);
				} else if (thr_err)
					throw new Exception("类【" + cls.getName() + "】属性【" + field.getName() + "】未发现注解@CLinkFieldInfo");
			}
		}
		return rst;
	}

	/**
	 * 根据类获取表
	 * 
	 * @param cls
	 * @return
	 */
	public static String getJPATableName(Class<?> cls) {
		String tablename = null;
		if (cls.isAnnotationPresent(CEntity.class)) {
			CEntity entity = cls.getAnnotation(CEntity.class);
			tablename = entity.tablename();
		}
		if ((tablename == null) || (tablename.isEmpty()))
			tablename = cls.getSimpleName().toLowerCase();
		return tablename;
	}

	/**
	 * 根据类获取KEY字段
	 * 
	 * @param cls
	 * @return
	 * @throws Exception
	 */
	public static CFieldinfo getIDField(Class<?> cls) throws Exception {
		if (!CJPABase.class.isAssignableFrom(cls)) {
			throw new Exception(cls.getName() + "必须是 com.corsair.server.cjpa.CJPA的子类");
		}
		Field[] fields = cls.getFields();
		for (Field field : fields) {
			if (field.getGenericType() == CField.class) {
				if (!field.isAnnotationPresent(CFieldinfo.class))
					throw new Exception("JPA 字段未发现注解@CFieldinfo");
				CFieldinfo fdinfo = field.getAnnotation(CFieldinfo.class);
				if (fdinfo.iskey())
					return fdinfo;
			}
		}
		return null;
	}

	/**
	 * 根据类、字段名获取字段对象
	 * 
	 * @param cls
	 * @param fdname
	 * @return
	 * @throws Exception
	 */
	public static CFieldinfo getField(Class<CJPABase> cls, String fdname) throws Exception {
		if (!CJPABase.class.isAssignableFrom(cls)) {
			throw new Exception(cls.getName() + "必须是 com.corsair.server.cjpa.CJPA的子类");
		}
		Field[] fields = cls.getFields();
		for (Field field : fields) {
			if (field.getGenericType() == CField.class) {
				if (!field.isAnnotationPresent(CFieldinfo.class))
					throw new Exception("JPA 字段未发现注解@CFieldinfo");
				CFieldinfo fdinfo = field.getAnnotation(CFieldinfo.class);
				if (fdinfo.fieldname().equalsIgnoreCase(fdname))
					return fdinfo;
			}
		}
		return null;
	}

	/**
	 * 获取连接池
	 * 
	 * @param cls
	 * @return
	 */
	public static CDBPool getDBPool(Class<?> cls) {
		String poolname = null;
		if (cls.isAnnotationPresent(CEntity.class)) {
			CEntity entity = cls.getAnnotation(CEntity.class);
			poolname = entity.dbpool();
		}
		if (poolname.isEmpty()) {
			return DBPools.defaultPool();
		} else {
			return DBPools.poolByName(poolname);
		}
	}
}
