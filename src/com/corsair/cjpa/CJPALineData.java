package com.corsair.cjpa;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Constructor;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.dom4j.Element;

import com.corsair.cjpa.CJPABase.CJPAStat;
import com.corsair.cjpa.util.CEntity;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.cjpa.util.CJPASqlUtil;
import com.corsair.cjpa.util.CLinkFieldInfo;
import com.corsair.cjpa.util.CjpaUtil;
import com.corsair.cjpa.util.LinkFieldItem;
import com.corsair.dbpool.CDBConnection;
import com.corsair.dbpool.CDBConnection.DBType;
import com.corsair.dbpool.CDBPool;
import com.corsair.dbpool.DBPools;
import com.corsair.dbpool.util.CPoolSQLUtil;
import com.corsair.dbpool.util.Logsw;
import com.corsair.dbpool.util.PraperedSql;
import com.corsair.dbpool.util.PraperedValue;

/**
 * JPA 行表
 * 
 * @author Administrator
 *
 * @param <T>
 */
public class CJPALineData<T> extends ArrayList<CJPABase> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3137822052767354971L;
	private CJPABase owner;
	private CDBPool pool;
	private Class<CJPABase> entityClas;
	private String tableName;
	private String cfieldname;
	private List<LinkField> linkfields = new ArrayList<LinkField>();
	private CLinkFieldInfo linkfdinfo;

	private String sqlOrderBystr;
	private String sqlWhereStr;
	private int maxRecord = 300;
	private boolean lazy = false;
	private int rowcount = 0;

	// 运行期的泛型无法获取
	// public CJPALineData() {
	//
	// }

	/**
	 * @param entityClas
	 * CJPA 的子类
	 */
	public CJPALineData(Class<?> entityClas) {
		this(null, entityClas);
	}

	/**
	 * 创建并且初始化列表数据
	 * 
	 * @param entityClas
	 * @param sqlstr
	 * @throws Exception
	 */
	public CJPALineData(Class<?> entityClas, String sqlstr) throws Exception {
		this(null, entityClas);
		findDataBySQL(sqlstr, true, true);
	}

	/**
	 * 创建并且根据指定数据库连接初始化列表数据
	 * 
	 * @param entityClas
	 * @param con
	 * @param sqlstr
	 * @throws Exception
	 */
	public CJPALineData(Class<?> entityClas, CDBConnection con, String sqlstr) throws Exception {
		this(null, entityClas);
		findDataBySQL(con, sqlstr, true, true, -1, 0, false);
	}

	/**
	 * 创建并且根据指定数据库连接初始化列表数据 ,同时指定是否加载自关联数据
	 * 
	 * @param entityClas
	 * @param con
	 * @param sqlstr
	 * @param selflink
	 * @throws Exception
	 */
	public CJPALineData(Class<?> entityClas, CDBConnection con, String sqlstr, boolean selflink) throws Exception {
		this(null, entityClas);
		findDataBySQL(con, sqlstr, true, selflink, -1, 0, false);
	}

	@SuppressWarnings("unchecked")
	public CJPALineData(CJPABase owner, Class<?> entityClas) {
		// this();
		try {
			this.owner = owner;
			if ((entityClas != null)) {
				if (!CJPABase.class.isAssignableFrom(entityClas))
					throw new Exception("CJPALineData构造函数参数" + entityClas.getName() + "务必是com.corsair.server.cjpa.CJPA的子类!");
				this.entityClas = (Class<CJPABase>) entityClas;
				String tablename = null;
				String poolname = null;
				if (entityClas.isAnnotationPresent(CEntity.class)) {
					CEntity entity = entityClas.getAnnotation(CEntity.class);
					// System.out.println("111111" + entity);
					poolname = entity.dbpool();
					tablename = entity.tablename();
				}
				if (pool == null) {
					if ((poolname == null) || poolname.isEmpty())
						this.pool = DBPools.defaultPool();
					else
						this.pool = DBPools.poolByName(poolname);
				}
				if ((tablename == null) || tablename.isEmpty())
					this.tableName = entityClas.getSimpleName().toLowerCase();
				else
					this.tableName = tablename;
			}
		} catch (Exception e) {
			try {
				Logsw.error(e);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

	/**
	 * 添加主从关联字段
	 * 
	 * @param mfield
	 * 主表关联字段名
	 * @param lfield
	 * 从表关联字段名
	 */
	public void addLinkField(String mfield, String lfield) {
		linkfields.add(new LinkField(mfield, lfield));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.ArrayList#clear()
	 * 清空所有JPA数据，并释放内存，最后调用父类清理列表
	 */
	@Override
	public void clear() {
		for (CJPABase jpa : this) {
			jpa.clear();
			jpa = null;
		}
		super.clear();
	}

	/**
	 * @return 创建SQL语句，如果分是否有关联注解 两种方法；结果一致 ;有注解性能更好
	 * @throws Exception
	 */
	public String buildWhereSQL() throws Exception {
		return (getLinkfdinfo() == null) ? buildWhereSQL_old() : buildWhereSQL_new();
	}

	private String buildWhereSQL_old() throws Exception {
		CJPABase cjpatem = null;
		try {
			cjpatem = newJPAObjcet(entityClas, "", pool);
		} catch (Exception e) {
			throw new Exception("构造JAP错误:" + e.getMessage());
		}
		String sqlwhere = "";
		for (LinkField lf : linkfields) {
			CField cfm = owner.cfield(lf.getMfield());
			if (cfm == null) {
				throw new Exception("关联字段<" + lf.getMfield() + ">在主CJPA<" + owner.tablename + ">中不存在!");
			}
			CField cfl = cjpatem.cfield(lf.getLfield());
			if (cfl == null) {
				throw new Exception("关联字段<" + lf.getLfield() + ">在从CJPA<" + cjpatem.tablename + ">中不存在!");
			}
			if (cfl.getFieldtype() == 0) {
				throw new Exception("关联字段<" + cfl.getFieldname() + ">没有数据类型!");
			}
			if (cfm.getValue() == null) {
				// 無關聯值
			} else {
				String sqlfdname = CJPASqlUtil.getSqlField(pool.getDbtype(), lf.getLfield());
				String sqlvalue = CJPASqlUtil.getSqlValue(this.pool.getDbtype(), cfl.getFieldtype(), cfm.getValue());
				sqlwhere = sqlwhere + " and " + sqlfdname + "=" + sqlvalue;
			}
		}
		return sqlwhere;
	}

	private String buildWhereSQL_new() throws Exception {
		// System.out.println("buildWhereSQL_new...........................");
		String sqlwhere = "";
		for (LinkFieldItem lfi : linkfdinfo.linkFields()) {
			CField cfm = owner.cfield(lfi.mfield());
			if (cfm == null) {
				throw new Exception("关联字段<" + lfi.mfield() + ">在主CJPA<" + owner.tablename + ">中不存在!");
			}
			if (cfm.getValue() == null) {
				// 关联字段没值
			} else {
				String sqlfdname = CJPASqlUtil.getSqlField(pool.getDbtype(), lfi.lfield());
				CFieldinfo fdi = CjpaUtil.getField(entityClas, lfi.lfield());
				if (fdi == null)
					throw new Exception("JPA类【" + entityClas.getName() + "】中没发现字段为【" + lfi.lfield() + "】的注解信息");
				String sqlvalue = CJPASqlUtil.getSqlValue(pool.getDbtype(), fdi.datetype(), cfm.getValue());
				sqlwhere = sqlwhere + " and " + sqlfdname + "=" + sqlvalue;
			}
		}
		return sqlwhere;
	}

	// ///////////////////////loadDataByOwner /////////////////////////////////

	// SELECT tb3.* FROM `inv_stock_checkline_deal` tb3,(SELECT tb2.* FROM
	// `inv_stock_checkline` tb2,(SELECT * FROM `inv_stock_check` WHERE
	// `stockchkid`=15) tb1
	// WHERE tb1.`stockchkid` =tb2.`stockchkid` AND
	// tb1.`stockchkcode`=tb2.`stockchkcode`) tb4
	// WHERE tb3.`stockchkid` =tb4.`stockchkid` AND
	// tb3.`stockchkcode`=tb4.`stockchkcode` AND
	// tb3.`stockchklineid`=tb4.stockchklineid

	/**
	 * 根据关联字段、属主JPA加载数据列表
	 * 
	 * @param clear
	 * 加载前是否清空自己
	 * @param selflink
	 * @throws Exception
	 */
	public void loadDataByOwner(boolean clear, boolean selflink) throws Exception {
		if (pool == null)
			throw new Exception(entityClas.getSimpleName() + " JPA List 载入数据前必须初始化 Pool！");
		if (entityClas == null)
			throw new Exception(entityClas.getSimpleName() + " JPA List 载入数据前必须初始化 实体类型！");
		CDBConnection con = pool.getCon(this);
		try {
			loadDataByOwner(con, clear, selflink, false);
		} catch (Exception e) {
			throw e;
		} finally {
			con.close();
		}
	}

	/**
	 * 指定数据库连接根据关联字段、属主JPA加载数据列表
	 * 
	 * @param con
	 * @param clear
	 * @param selflink
	 * @param update
	 * 是否置为更新状态（会锁定相关数据库记录，直到con提交或回滚）
	 * @throws Exception
	 */
	public void loadDataByOwner(CDBConnection con, boolean clear, boolean selflink, boolean update) throws Exception {
		if (isLazy())
			return;
		if (pool == null)
			throw new Exception(entityClas.getSimpleName() + " JPA List 载入数据前必须初始化 Pool！");
		String sqlwhere = buildWhereSQL();
		if (sqlwhere.isEmpty()) {
			Logsw.debug(entityClas.getSimpleName() + " 载入CJPA明细表错误:生成查询条件为空!");
			return;
		}
		String sqlstr = "select * from " + tableName + " where 1=1 " + sqlwhere;

		if ((sqlOrderBystr != null) && (!sqlOrderBystr.isEmpty()))
			sqlstr = sqlstr + " order by " + sqlOrderBystr;
		findDataBySQL(con, sqlstr, clear, selflink, -1, 0, update);
	}

	// ///////////////////loadDataByOwner////////////////////////

	// /////////////////////////findDataBySQL///////////////////
	/**
	 * 从SQL 加载数据
	 * 
	 * @param sqlstr
	 * @return
	 * @throws Exception
	 */
	public CJPALineData<T> findDataBySQL(String sqlstr) throws Exception {
		findDataBySQL(sqlstr, true, true);
		return this;
	}

	/**
	 * 从SQL 加载数据
	 * 
	 * @param sqlstr
	 * @param clear
	 * @param selflink
	 * @return
	 * @throws Exception
	 */
	public CJPALineData<T> findDataBySQL(String sqlstr, boolean clear, boolean selflink) throws Exception {
		return findDataBySQL(sqlstr, clear, selflink, -1, 0, true);
	}

	/**
	 * 从SQL 加载数据
	 * 
	 * @param sqlstr
	 * @param clear
	 * @param selflink
	 * @return
	 * @throws Exception
	 */
	public CJPALineData<T> findDataBySQL(String sqlstr, boolean clear, boolean selflink, boolean showlog) throws Exception {
		return findDataBySQL(sqlstr, clear, selflink, -1, 0, showlog);
	}

	/**
	 * 从SQL 加载数据
	 * 
	 * @param sqlstr
	 * @param clear
	 * @param selflink
	 * @param page
	 * @param pagesize
	 * @return
	 * @throws Exception
	 */
	public CJPALineData<T> findDataBySQL(String sqlstr, boolean clear, boolean selflink, int page, int pagesize, boolean showlog) throws Exception {
		if (pool == null)
			throw new Exception(entityClas.getSimpleName() + " JPA List 载入数据前必须初始化 Pool！");
		if (entityClas == null)
			throw new Exception(entityClas.getSimpleName() + " JPA List 载入数据前必须初始化 实体类型！");
		CDBConnection con = pool.getCon(this);
		try {
			return findDataBySQL(con, sqlstr, clear, selflink, page, pagesize, false, showlog);
		} catch (Exception e) {
			throw e;
		} finally {
			con.close();
		}
	}

	/**
	 * 从SQL 加载数据
	 * 
	 * @param con
	 * @param sqlstr
	 * @param selflink
	 * @param update
	 * @return
	 * @throws Exception
	 */
	public CJPALineData<T> findDataBySQL(CDBConnection con, String sqlstr, boolean selflink, boolean update) throws Exception {
		return findDataBySQL(con, sqlstr, true, selflink, -1, 0, update);
	}

	/**
	 * 从SQL 加载数据
	 * 
	 * @param con
	 * @param sqlstr
	 * @param clear
	 * @param selflink
	 * @param page
	 * : -1 不分页
	 * @param pagesize
	 * @param update
	 * @return
	 * @throws Exception
	 */
	public CJPALineData<T> findDataBySQL(CDBConnection con, String sqlstr, boolean clear, boolean selflink, int page, int pagesize, boolean update)
			throws Exception {
		return findDataBySQL(con, sqlstr, clear, selflink, page, pagesize, update, true);
	}

	/**
	 * 从SQL 加载数据
	 * 
	 * @param con
	 * @param sqlstr
	 * @param clear
	 * @param selflink
	 * @param page
	 * : -1 不分页
	 * @param pagesize
	 * @param update
	 * @return
	 * @throws Exception
	 */
	public CJPALineData<T> findDataBySQL(CDBConnection con, String sqlstr, boolean clear, boolean selflink, int page, int pagesize, boolean update, boolean showlog)
			throws Exception {
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = con.con.createStatement();
			String psqlstr = sqlstr;
			if (page != -1) {
				rowcount = Integer.valueOf(con.openSql2List("select count(*) ct from(" + sqlstr + ") tb").get(0).get("ct"));// 获取总数量
				if (con.getDBType() == DBType.mysql) {
					if (page < 1)
						page = 1;
					psqlstr = sqlstr + " limit " + (page - 1) * pagesize + "," + pagesize;
				}
			}
			if (update)
				psqlstr = psqlstr + " for update";
			long time = System.currentTimeMillis();
			rs = stmt.executeQuery(psqlstr);
			loadData4DataSet(con, rs, clear, selflink, update);

			if ((DBPools.getCblog() != null) && showlog) {
				long et = System.currentTimeMillis();
				DBPools.getCblog().writelog(con, "【执行SQL：" + psqlstr + "；耗时:" + (et - time) + "】");
			}
			return this;
		} finally {
			DBPools.safeCloseS_R(stmt, rs);
		}
	}

	// ////////////findDataBySQL///////////////////////////

	/**
	 * 创建列表类指定的JPA实体
	 * 
	 * @param entityClas
	 * @return
	 * @throws Exception
	 */
	public CJPABase newJPAObjcet(Class<CJPABase> entityClas) throws Exception {
		// System.out.println(entityClas.getName());
		Constructor<?> cst = null;
		try {
			cst = entityClas.getConstructor();
			Object o = cst.newInstance();
			return (CJPABase) (o);
		} catch (NoSuchMethodException e) {
			Object o = entityClas.newInstance();
			return (CJPABase) (o);
		}
	}

	/**
	 * 创建列表类指定的JPA实体
	 * 
	 * @param entityClas
	 * @param tablename
	 * @param pool
	 * @return
	 * @throws Exception
	 */
	public CJPABase newJPAObjcet(Class<CJPABase> entityClas, String tablename, CDBPool pool) throws Exception {
		// System.out.println(entityClas.getName());
		Class<?> paramTypes[] = { String.class, CDBPool.class, String.class };
		Constructor<?> cst = null;
		try {
			cst = entityClas.getConstructor(paramTypes);
			Object o = cst.newInstance(tablename, pool);
			return (CJPABase) (o);
		} catch (NoSuchMethodException e) {
			Object o = entityClas.newInstance();
			return (CJPABase) (o);
		}
	}

	/**
	 * 从数据集加载列表数据
	 * 
	 * @param con
	 * @param rs
	 * @param update
	 * 是否锁定更新
	 * @throws Exception
	 */
	public void loadData4DataSet(CDBConnection con, ResultSet rs, boolean update) throws Exception {
		loadData4DataSet(con, rs, true, true, update);
	}

	/**
	 * 从数据集加载列表数据
	 * 
	 * @param con
	 * @param rs
	 * @param clear
	 * @param selflink
	 * @param update
	 * 是否锁定更新
	 * @throws Exception
	 */
	public void loadData4DataSet(CDBConnection con, ResultSet rs, boolean clear, boolean selflink, boolean update) throws Exception {
		if (clear)
			clear();
		if (rs == null)
			throw new Exception("从结果集载入数据错误:结果集为空!");
		while (rs.next()) {
			// System.out.println("entityClas:" + this.entityClas);
			CJPABase cjpaline = newJPAObjcet(entityClas, "", pool);
			((CJPALoad) cjpaline).load4DataSet(con, rs, selflink, update);
			this.add(cjpaline);
		}
	}

	/**
	 * 生成XML节点数据
	 * 
	 * @param linedata
	 */
	public void toxmlnode(Element linedata) {
		String entityclas;
		if (getEntityClas() == null)
			entityclas = "";
		else
			entityclas = getEntityClas().getName();
		linedata.addAttribute("entityclas", entityclas);
		linedata.addAttribute("tablename", getTableName());
		linedata.addAttribute("orderby", getSqlOrderBystr());
		linedata.addAttribute("where", getSqlWhereStr());
		linedata.addAttribute("maxrecord", String.valueOf(getMaxRecord()));
		linedata.addAttribute("lazy", Boolean.valueOf(isLazy()).toString());
		Element linkfields = linedata.addElement("linkfields");
		for (LinkField lf : getLinkfields()) {
			Element linkfield = linkfields.addElement("linkfield");
			linkfield.addAttribute("mfield", lf.getMfield());
			linkfield.addAttribute("lfield", lf.getLfield());
		}
		Element lines = linedata.addElement("lines");
		for (CJPABase jpa : this) {
			((CJPAXML) jpa).toxmlroot(lines.addElement("cjpa"));
		}
	}

	/**
	 * XML包含修改的 需要删除的数据
	 * 从XML加载数据
	 * 
	 * @param elinedata
	 * @throws Exception
	 */
	public void fromxmlnode(Element elinedata) throws Exception {
		setTableName(elinedata.attributeValue("tablename"));
		setSqlOrderBystr(elinedata.attributeValue("orderby"));
		setSqlWhereStr(elinedata.attributeValue("where"));
		setMaxRecord(Integer.valueOf(elinedata.attributeValue("maxrecord")));
		setLazy(Boolean.valueOf(elinedata.attributeValue("lazy")));
		@SuppressWarnings("unchecked")
		List<Element> ejpas = elinedata.element("lines").elements("cjpa");
		// System.out.println("fromxmlnode 列表数据行数:"+ejpas.size());
		for (Element ejpa : ejpas) {
			CJPAXML ccjpa = (CJPAXML) newJPAObjcet(elinedata.attributeValue("entityclas"));
			ccjpa.fromxmlroot(ejpa);
			this.add(ccjpa);
		}
	}

	/**
	 * @return 输出JSON字符串
	 * @throws Exception
	 */
	public String tojson() throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		JsonFactory jf = new JsonFactory();
		JsonGenerator jg = jf.createJsonGenerator(baos);
		jg.writeStartArray();
		for (CJPABase cjpa : this) {
			jg.writeRawValue(((CJPAJSON) cjpa).tojson());
		}
		jg.writeEndArray();
		// jg.writeEndObject();
		jg.close();
		return baos.toString("utf-8");
	}

	/**
	 * 输出JSON字符串
	 * 
	 * @param fields
	 * 指定字段
	 * @return
	 * @throws Exception
	 */
	public String tojson(String[] fields) throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		JsonFactory jf = new JsonFactory();
		JsonGenerator jg = jf.createJsonGenerator(baos);
		jg.writeStartArray();
		for (CJPABase cjpa : this) {
			jg.writeRawValue(((CJPAJSON) cjpa).tojsonsimple(fields));
		}
		jg.writeEndArray();
		// jg.writeEndObject();
		jg.close();
		return baos.toString("utf-8");
	}

	/**
	 * @return 按多页格式输出JSON字符串{total:total,page:page,rows:[]}
	 * @throws Exception
	 */
	public String tojsonpage() throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		JsonFactory jf = new JsonFactory();
		JsonGenerator jg = jf.createJsonGenerator(baos);
		jg.writeStartObject();
		jg.writeNumberField("total", rowcount);
		jg.writeFieldName("rows");
		jg.writeRawValue(tojson());
		jg.writeEndObject();
		jg.close();
		return baos.toString("utf-8");
	}

	/*
	 * public String tojsonarry() throws Exception { ByteArrayOutputStream baos = new ByteArrayOutputStream(); JsonFactory jf = new JsonFactory(); JsonGenerator
	 * jg = jf.createJsonGenerator(baos); jg.writeStartObject(); // System.out.println(entityClas.getName()); jg.writeFieldName(entityClas.getSimpleName() +
	 * "s"); jg.writeStartArray(); for (CJPABase cjpa : this) { jg.writeRawValue(((CJPAJSON) cjpa).tosimplejson()); } jg.writeEndArray(); jg.writeEndObject();
	 * jg.close(); return baos.toString("utf-8"); }
	 */

	/**
	 * 根据ID从列表获取JPA对象
	 * 
	 * @param idvalue
	 * @return
	 */
	public CJPABase getJPAByID(String idvalue) {
		if (idvalue == null) {
			return null;
		}
		for (CJPABase cjpa : this) {
			if (idvalue.equalsIgnoreCase(cjpa.getid()))
				return (CJPABase) cjpa;
		}
		return null;
	}

	private String getIDValueFromJsonNode(JsonNode node) throws Exception {
		// this.entityClas.
		String idfdname = CjpaUtil.getJPAIDFieldNameByClass(entityClas);
		if (idfdname == null)
			throw new Exception(entityClas.getName() + "没有发现ID字段!");
		// JsonNode clsnode = node.get(entityClas.getSimpleName());

		JsonNode vnode = node.get(idfdname);
		if (vnode == null)
			return null;
		// throw new Exception("JSON节点未发现字段:" + idfdname);
		return vnode.asText();
	}

	private void setJpasTag() {
		for (CJPABase cjpa : this) {
			cjpa.tag = 0;
		}
	}

	private void checkJpasTag() {
		for (CJPABase cjpa : this) {
			if (cjpa.tag == 0) {
				cjpa.setJpaStat(CJPAStat.RSDEL);
			}
		}
	}

	/**
	 * 从JSON加载数据
	 * 
	 * @param json
	 * @throws Exception
	 */
	public void fromjson(String json) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		try {
			JsonNode rootNode = mapper.readTree(json);
			fromjsonnode(rootNode);
		} catch (Exception e) {
			throw new Exception("json格式错误!");
		}
	}

	//
	/**
	 * json 里面包含修改的数据 未修改ide数据，不包含需要删除的数据
	 * 从JSON节点加载数据
	 * 
	 * @param nodelist
	 * @throws Exception
	 */
	public void fromjsonnode(JsonNode nodelist) throws Exception {
		setJpasTag();// 未读取JSON 将所有JPA tag 设置为0
		Iterator<JsonNode> nodes = nodelist.getElements();
		while (nodes.hasNext()) {
			JsonNode node = nodes.next();
			String idvalue = getIDValueFromJsonNode(node);
			CJPAJSON ccjpa = null;
			if ((idvalue != null) && (!idvalue.isEmpty())) {
				ccjpa = (CJPAJSON) getJPAByID(idvalue);
			}
			if (ccjpa == null) {
				ccjpa = (CJPAJSON) newJPAObjcet(entityClas.getName());
				this.add(ccjpa);
			}
			String[] fds = {};
			ccjpa.readjson(node, fds);
			ccjpa.tag = 1;
		}
		checkJpasTag();// 将所有Tag为0的标记为删除
	}

	/**
	 * @return 创建JPA实体
	 * @throws Exception
	 */
	public CJPABase newJPAObjcet() throws Exception {
		return newJPAObjcet(entityClas.getName());
	}

	/**
	 * 创建JPA实体
	 * 
	 * @param className
	 * @return
	 * @throws Exception
	 */
	public CJPABase newJPAObjcet(String className) throws Exception {
		Class<?> CJPAcd = Class.forName(className);
		if (!CJPABase.class.isAssignableFrom(CJPAcd)) {
			throw new Exception(className + "必须从 com.corsair.server.cjpa.CJPA继承");
		}
		Class<?> paramTypes[] = {};
		Constructor<?> cst = CJPAcd.getConstructor(paramTypes);
		Object o = cst.newInstance();
		return (CJPABase) (o);
	}

	/**
	 * 返回某个字段的合计值
	 * 
	 * @param cfdname
	 * @return
	 */
	public double getSumValue(String cfdname) {
		double sum = 0;
		for (CJPABase jpa : this) {
			sum = sum + jpa.cfieldbycfieldname(cfdname).getAsFloatDefault(0);
		}
		return sum;
	}

	/**
	 * fieldvalues{{pertyname,value},{pertyname,value}}
	 * 
	 * @param fieldvalues
	 * @return
	 * @throws Exception
	 */
	public CJPABase getJPAByValues(String[][] fieldvalues) throws Exception {
		for (String[] fieldvalue : fieldvalues) {
			if (fieldvalue.length != 2)
				throw new Exception("findJPAByValues 参数错误");
		}
		for (CJPABase jpa : this) {
			boolean find = true;
			for (String[] fieldvalue : fieldvalues) {
				String pname = fieldvalue[0];
				String value = fieldvalue[1];
				CField field = jpa.cfieldbycfieldname(pname);
				if (field == null) {
					throw new Exception("JPA对象属性【" + pname + "】不存在");
				}
				String fv = field.getValue();
				if (fv == null) {
					if (value != null) {
						find = false;
						break;
					}
				} else {
					if (!fv.equalsIgnoreCase(value)) {
						find = false;
						break;
					}
				}
			}
			if (find) {
				return (CJPABase) jpa;
			}
		}
		return null;
	}

	/**
	 * 是否支持批量更新
	 * 
	 * @param con
	 * @return
	 */
	public static boolean supportBatch(CDBConnection con) {
		try {
			// 得到数据库的元数据
			DatabaseMetaData md = con.con.getMetaData();
			return md.supportsBatchUpdates();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	// 批量保存需要的ID数
	private int needIDNum() {
		int rst = 0;
		for (CJPABase jpa : this) {
			CJPASave sv = (CJPASave) jpa;
			for (CField cf : sv.cFields) {
				if (cf.isIskey() && cf.isEmpty())
					rst++;
			}
		}
		return rst;
	}

	// 批量设置ID
	private void batchSetID(CDBConnection con, CJPASave js) throws Exception {
		int nednum = needIDNum();
		if (nednum != 0) {
			int id = Integer.valueOf(js.newid(con, nednum));
			for (CJPABase jpa : this) {
				CJPASave sv = (CJPASave) jpa;
				for (CField cf : sv.cFields) {
					if (cf.isIskey() && cf.isEmpty())
						cf.setAsInt(id++);
				}
			}
		}
	}

	/**
	 * 批量保存，不保存自关联数据
	 * 
	 * @param con
	 * @throws Exception
	 */
	public void saveBathcSiple(CDBConnection con) throws Exception {
		if (!supportBatch(con))
			throw new Exception("数据库不支持批量处理!");
		CJPASave jpatem = (CJPASave) newJPAObjcet();
		batchSetID(con, jpatem);
		// long time1 = System.currentTimeMillis();
		ArrayList<PraperedSql> sqllist = new ArrayList<PraperedSql>();
		for (CJPABase jpa : this) {
			CJPASave sv = (CJPASave) jpa;
			sv.buildSqlParms(con, sqllist, false);
		}
		// long time2 = System.currentTimeMillis();
		// System.out.println("生成SQL耗时:" + (time2 - time1));
		if (sqllist.size() > 0) {
			PreparedStatement pstmt = null;
			try {
				long time = System.currentTimeMillis();
				pstmt = con.con.prepareStatement(sqllist.get(0).getSqlstr());
				for (PraperedSql psql : sqllist) {
					for (int i = 0; i < psql.getParms().size(); i++) {
						PraperedValue pv = psql.getParms().get(i);
						CPoolSQLUtil.setSqlPValue(pstmt, i + 1, pv);
					}
					pstmt.addBatch();
				}
				pstmt.executeBatch();
				if (DBPools.getCblog() != null) {
					long et = System.currentTimeMillis();
					DBPools.getCblog().writelog(con, "【执行简单批处理：" + tableName + sqllist.size() + "；耗时:" + (et - time) + "】");
				}
			} finally {
				DBPools.safeCloseS_R(pstmt, null);
			}
		}
		// long time3 = System.currentTimeMillis();
		// System.out.println("执行保存SQL耗时:" + (time3 - time2));
	}

	/**
	 * 批量保存，不保存自关联数据
	 * 
	 * @throws Exception
	 */
	public void saveBatchSimple() throws Exception {
		CJPASave jpatem = (CJPASave) newJPAObjcet();
		CDBConnection con = jpatem.pool.getCon(this);
		con.startTrans();
		try {
			saveBathcSiple(con);
			con.submit();
		} catch (Exception e) {
			con.rollback();
			throw e;
		} finally {
			con.close();
		}
	}

	/**
	 * @param con
	 * @throws Exception
	 */
	public void save(CDBConnection con) throws Exception {
		for (CJPABase jpa : this) {
			CJPASave sv = (CJPASave) jpa;
			sv.save(con);
		}
	}

	/**
	 * @throws Exception
	 */
	public void save() throws Exception {
		CJPABase jpa = newJPAObjcet();
		CDBConnection con = jpa.pool.getCon(this);
		con.startTrans();
		try {
			save(con);
			con.submit();
		} catch (Exception e) {
			con.rollback();
			e.printStackTrace();
		} finally {
			con.close();
		}
	}

	/**
	 * 设置所有列表，某个字段的值
	 * 
	 * @param fdname
	 * @param value
	 */
	public void setFieldValues(String fdname, String value) {
		for (CJPABase jpa : this) {
			CJPASave sv = (CJPASave) jpa;
			sv.cfield(fdname).setValue(value);
		}
	}

	public enum SortType {
		ASC, DESC
	}

	/**
	 * 按指定字段进行排序
	 * 
	 * @param fdname
	 * @param st
	 * SortType.ASC, SortType.DESC
	 */
	@SuppressWarnings("unchecked")
	public void sort(final String fdname, final SortType st) {
		Collections.sort(this, new Comparator<CJPABase>() {
			@Override
			public int compare(CJPABase jpa1, CJPABase jpa2) {
				CField fd1 = jpa1.cfield(fdname);
				CField fd2 = jpa2.cfield(fdname);
				if (fd1 == null) {
					// Exception("【"+jpa1.getClass().getName()+"】不存在【"+fdname+"】字段");
					return 0;
				}
				if (CPoolSQLUtil.eInArray(fd1.getFieldtype(), CPoolSQLUtil.numFDType)) {
					// System.out.println("compare:" + fd1.getAsFloatDefault(0) + "+" + fd2.getAsFloatDefault(0));
					Float f1 = fd1.getAsFloatDefault(0);
					Float f2 = fd2.getAsFloatDefault(0);
					if (st == SortType.ASC) {
						return f1.compareTo(f2);
					} else
						return f2.compareTo(f1);
				}
				if (CPoolSQLUtil.eInArray(fd1.getFieldtype(), CPoolSQLUtil.dateFDType)) {
					Date dt = new Date();
					if (st == SortType.ASC)
						return fd1.getAsDatetime(dt).compareTo(fd2.getAsDatetime(dt));
					else
						return fd2.getAsDatetime(dt).compareTo(fd1.getAsDatetime(dt));
				}
				if (CPoolSQLUtil.eInArray(fd1.getFieldtype(), CPoolSQLUtil.strFDType)) {
					String s1 = fd1.getValue();
					String s2 = fd2.getValue();
					if (s1 == null) {
						if (s2 == null) {
							return 0;
						} else {
							return (st == SortType.ASC) ? -1 : 1;
						}
					} else {
						return (st == SortType.ASC) ? s1.compareTo(s2) : s1.compareTo(s2) * -1;
					}
				}
				return 0;
			}

		});
	}

	/**
	 * @param includedel
	 * 是否包含已经标记为删除 单未实际删除的
	 * @return
	 */
	public int getJpaSize(boolean includedel) {
		if (includedel)
			return this.size();
		int dc = 0;
		for (CJPABase jpa : this) {
			if (jpa.getJpaStat() == CJPAStat.RSDEL)
				dc++;
		}
		return this.size() - dc;
	}

	public int getJpaSize() {
		return getJpaSize(false);
	}

	/**
	 * @return 转成普通List
	 */
	public List<?> toList() {
		List<T> rst = new ArrayList<T>();
		for (CJPABase cjpa : this) {
			rst.add((T) cjpa);
		}
		return rst;
	}

	/**
	 * 从普通List加载数据
	 * 
	 * @param list
	 */
	public void fromList(List<?> list, boolean clear) {
		if (clear)
			clear();
		addAll((Collection<? extends CJPABase>) list);
	}

	// /////////////////geter seter
	public CJPABase getOwner() {
		return owner;
	}

	public void setOwner(CJPABase owner) {
		this.owner = owner;
	}

	public Class<CJPABase> getEntityClas() {
		return entityClas;
	}

	public void setEntityClas(Class<CJPABase> entityClas) {
		this.entityClas = entityClas;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getSqlOrderBystr() {
		return sqlOrderBystr;
	}

	public void setSqlOrderBystr(String sqlOrderBystr) {
		this.sqlOrderBystr = sqlOrderBystr;
	}

	public String getSqlWhereStr() {
		return sqlWhereStr;
	}

	public void setSqlWhereStr(String sqlWhereStr) {
		this.sqlWhereStr = sqlWhereStr;
	}

	public CDBPool getPool() {
		return pool;
	}

	public void setPool(CDBPool pool) {
		this.pool = pool;
	}

	public List<LinkField> getLinkfields() {
		return linkfields;
	}

	public void setLinkfields(List<LinkField> linkfields) {
		this.linkfields = linkfields;
	}

	public int getMaxRecord() {
		return maxRecord;
	}

	public void setMaxRecord(int maxRecord) {
		this.maxRecord = maxRecord;
	}

	public boolean isLazy() {
		return lazy;
	}

	public void setLazy(boolean lazy) {
		this.lazy = lazy;
	}

	public String getCfieldname() {
		return cfieldname;
	}

	public void setCfieldname(String cfieldname) {
		this.cfieldname = cfieldname;
	}

	public int getRowcount() {
		return rowcount;
	}

	public void setRowcount(int rowcount) {
		this.rowcount = rowcount;
	}

	public CLinkFieldInfo getLinkfdinfo() {
		return linkfdinfo;
	}

	public void setLinkfdinfo(CLinkFieldInfo linkfdinfo) {
		this.linkfdinfo = linkfdinfo;
	}

}
