package com.corsair.cjpa;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.corsair.cjpa.CJPABase.CJPAStat;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.cjpa.util.CJPASqlBuilding;
import com.corsair.cjpa.util.CJPASqlUtil;
import com.corsair.cjpa.util.CLinkFieldInfo;
import com.corsair.cjpa.util.CjpaUtil;
import com.corsair.cjpa.util.LinkFieldItem;
import com.corsair.dbpool.CDBConnection;
import com.corsair.dbpool.DBPools;
import com.corsair.dbpool.util.CPoolSQLUtil;
import com.corsair.dbpool.util.Logsw;
import com.corsair.dbpool.util.PraperedSql;
import com.corsair.dbpool.util.PraperedValue;
import com.corsair.dbpool.util.Systemdate;

/**
 * JPA保存数据实现
 * 
 * @author Administrator
 *
 */
public abstract class CJPASave extends CJPALoad {
	private String[] _creater_fn = new String[] { "creator" };
	private String[] _creattime_fn = new String[] { "createtime", "create_time" };
	private String[] _entid_fn = new String[] { "entid" };
	private String[] _usable_fn = new String[] { "usable" };
	private String[] _isvisible_fn = new String[] { "isvisible" };
	private String[] _stat_fn = new String[] { "stat" };
	private String[] _updator_fn = new String[] { "updator" };
	private String[] _updatime_fn = new String[] { "updatetime", "update_time" };

	public CJPASave() throws Exception {
	}

	public CJPASave(String sqlstr) throws Exception {
		super(sqlstr);
	}

	protected abstract String getLoginedUserName();

	protected abstract String getLoginedEntid();

	protected abstract CJPABase getLoginedUserDefaultOrg();

	protected abstract String getTableNewID(CDBConnection con, String seqname, int num) throws Exception;

	protected abstract String getNewCode(int codeid);

	private boolean strInArray(String str, String[] strs) {
		for (String s : strs) {
			if (s.equalsIgnoreCase(str)) {
				return true;
			}
		}
		return false;
	}

	// /
	/**
	 * 主从JPA互相设置关联字段的值
	 * 
	 * @throws Exception
	 */
	private void putLinkFieldsValue() throws Exception {
		List<CLinkFieldInfo> lfis = CjpaUtil.getLineLinkedInfos(this.getClass(), false);
		if (lfis.size() == 0) {
			putLinkFieldsValue_Old();
		} else {
			putLinkFieldsValue_New(lfis);
		}

	}

	// /
	/**
	 * 主从JPA互相设置关联字段的值
	 * 
	 * @throws Exception
	 */
	private void putLinkFieldsValue_Old() throws Exception {
		for (CJPALineData<CJPABase> jpaline : cJPALileDatas) {
			for (LinkField lf : jpaline.getLinkfields()) {
				CField cfdm = cfield(lf.getMfield());
				if (cfdm == null) {
					throw new Exception("关联字段【" + lf.getMfield() + "】在主JPA中未发现!");
				}
				for (CJPABase cjpa : jpaline) {
					CField cfdl = cjpa.cfield(lf.getLfield());
					if (cfdl == null) {
						throw new Exception("关联字段【" + lf.getLfield() + "】在从JPA中未发现!");
					}

					if (((cfdm.getValue() == null) || cfdm.getValue().isEmpty()) && ((cfdl.getValue() == null) || cfdl.getValue().isEmpty()))
						throw new Exception("关联字段【" + lf.getMfield() + "】【" + lf.getLfield() + "】值不能都为空!");

					if ((cfdm.getValue() == null) || cfdm.getValue().isEmpty()) {
						cfdm.setValue(cfdl.getValue());
						// System.out.println("主字段:" + cfdm.getFieldname() + ":"
						// + cfdm.getValue());
						// System.out.println("JPA状态:" + cJpaStat);
						if (getJpaStat() == CJPAStat.RSLOAD4DB)
							setJpaStat(CJPAStat.RSUPDATED);
					} else
						cfdl.setValue(cfdm.getValue());
				}
			}
		}
	}

	// /
	/**
	 * 主从JPA互相设置关联字段的值 根据注解
	 * 
	 * @param lfis
	 * @throws Exception
	 */
	private void putLinkFieldsValue_New(List<CLinkFieldInfo> lfis) throws Exception {
		for (CJPALineData<CJPABase> jpaline : cJPALileDatas) {
			CLinkFieldInfo lfi = jpaline.getLinkfdinfo();
			if (lfi == null)
				throw new Exception("JPA【" + this.getClass().toString() + "】列表【" + jpaline.getCfieldname() + "】没发现注解@CLinkFieldInfo!");
			for (LinkFieldItem lkf : lfi.linkFields()) {
				CField cfdm = cfield(lkf.mfield());
				if (cfdm == null) {
					throw new Exception("关联字段【" + lkf.mfield() + "】在主JPA【" + this.getClass().toString() + "】中未发现!");
				}
				for (CJPABase cjpa : jpaline) {
					CField cfdl = cjpa.cfield(lkf.lfield());
					if (cfdl == null) {
						throw new Exception("关联字段【" + lkf.lfield() + "】在从JPA中未发现!");
					}

					if (((cfdm.getValue() == null) || cfdm.getValue().isEmpty()) && ((cfdl.getValue() == null) || cfdl.getValue().isEmpty()))
						throw new Exception("关联字段【" + lkf.mfield() + "】【" + lkf.lfield() + "】值不能都为空!");

					if ((cfdm.getValue() == null) || cfdm.getValue().isEmpty()) {
						cfdm.setValue(cfdl.getValue());
						if (getJpaStat() == CJPAStat.RSLOAD4DB)
							setJpaStat(CJPAStat.RSUPDATED);
					} else
						cfdl.setValue(cfdm.getValue());
				}
			}
		}
	}

	protected void putDefaultValaus() throws Exception {
		for (CField cf : cFields) {
			if (getJpaStat() == CJPAStat.RSINSERT) {
				if (strInArray(cf.getFieldname(), _creater_fn) && (cf.isEmpty()))
					cf.setValue(getLoginedUserName());
				if (strInArray(cf.getFieldname(), _creattime_fn) && (cf.isEmpty()))
					cf.setValue(Systemdate.getStrDate());
				if (strInArray(cf.getFieldname(), _entid_fn) && (cf.isEmpty()))
					cf.setValue(getLoginedEntid());
				if ((strInArray(cf.getFieldname(), _usable_fn) || strInArray(cf.getFieldname(), _isvisible_fn) || strInArray(cf.getFieldname(), _stat_fn))
						&& (cf.isEmpty()))
					cf.setValue("1");
				if ((cf.getFieldname().equalsIgnoreCase("idpath")) && (cf.isEmpty())) {
					CJPABase o = getLoginedUserDefaultOrg();
					if (o != null) {
						cf.setValue(o.cfield("idpath").getValue());
					}
				}

				if (strInArray(cf.getFieldname(), _updator_fn) && (cf.isEmpty())) {
					String un = getLoginedUserName();
					if ((un != null) && (!un.isEmpty()))
						cf.setValue(un);
				}
				if (strInArray(cf.getFieldname(), _updatime_fn) && (cf.isEmpty()))
					cf.setValue(Systemdate.getStrDate());
			}
			if (getJpaStat() == CJPAStat.RSUPDATED) {
				if (strInArray(cf.getFieldname(), _updator_fn)) {// && (cf.isEmpty()) myh 171203
					String un = getLoginedUserName();
					if ((un != null) && (!un.isEmpty()))
						cf.setValue(un);
				}
				if (strInArray(cf.getFieldname(), _updatime_fn))// && (cf.isEmpty()) myh 171203
					cf.setValue(Systemdate.getStrDate());
			}
		}
	}

	protected String newid(CDBConnection con, String seqname, int num) throws Exception {
		return getTableNewID(con, seqname, num);
	}

	/**
	 * 创建KEY
	 * 
	 * @param con
	 * @param num
	 * @return
	 * @throws Exception
	 */
	public String newid(CDBConnection con, int num) throws Exception {
		if ((pool != null) && (pool.pprm != null))
			return newid(con, pool.pprm.name + "." + tablename, num);
		else
			return newid(con, tablename, num);
	}

	private boolean isallnull(boolean selfLink) {
		for (CField cf : cFields) {
			if ((cf.getValue() != null) && (!cf.getValue().isEmpty())) {
				return false;
			}
		}
		if (selfLink)
			for (CJPALineData<CJPABase> ld : cJPALileDatas) {
				if (ld.size() != 0)
					return false;
			}
		return true;
	}

	/**
	 * 检查KEY CODE 等自动生成的值，为空则创建
	 * 
	 * @param con
	 * @param seqname
	 * @throws Exception
	 */
	public void checkOrCreateIDCode(CDBConnection con, String seqname) throws Exception {
		for (CField cf : cFields) {
			if (cf.isIskey() && cf.isEmpty()) {
				if ((getJpaStat() == CJPAStat.RSUPDATED) || (getJpaStat() == CJPAStat.RSDEL)) {
					throw new Exception("【" + tablename + "】JPA执行更新或删除前Key值不能为空!");
				}
				if (getJpaStat() == CJPAStat.RSINSERT) {
					cf.setValue(newid(con, seqname, 1));
				}
			}
			if ((cf.getCodeid() != 0) && (getJpaStat() == CJPAStat.RSINSERT) && (cf.isEmpty())) {
				int codeid = cf.getCodeid();
				String newcode = null;
				if (getPublicControllerBase() != null)
					newcode = getPublicControllerBase().OngetNewCode((CJPABase) this, codeid);
				if ((newcode == null) && (getController() != null))
					newcode = getController().OngetNewCode((CJPABase) this, codeid);
				if (newcode == null)
					newcode = getNewCode(codeid);
				cf.setValue(newcode);
				if ((cf.getValue() == null) || cf.getValue().isEmpty()) {
					throw new Exception("获取ID为【" + cf.getCodeid() + "】的编码错误!");
				}
			}
		}
		for (CJPALineData<CJPABase> ld : cJPALileDatas) {
			for (CJPABase cjpa : ld) {
				((CJPASave) cjpa).checkOrCreateIDCode(con, seqname);
			}
		}
	}

	/**
	 * 检查KEY CODE 等自动生成的值，为空则创建
	 * 
	 * @param con
	 * @param selfLink
	 * @throws Exception
	 */
	public void checkOrCreateIDCode(CDBConnection con, boolean selfLink) throws Exception {
		for (CField cf : cFields) {
			if (cf.isIskey() && cf.isEmpty()) {
				if ((getJpaStat() == CJPAStat.RSUPDATED) || (getJpaStat() == CJPAStat.RSDEL)) {
					throw new Exception("【" + tablename + "】JPA执行更新或删除前Key值不能为空!");
				}
				if ((getJpaStat() == CJPAStat.RSINSERT) && (!cf.isAutoinc())) {
					cf.setValue(newid(con, 1));
				}
			}
			if ((cf.getCodeid() != 0) && (getJpaStat() == CJPAStat.RSINSERT) && (cf.isEmpty())) {
				int codeid = cf.getCodeid();
				String newcode = null;
				if (getPublicControllerBase() != null)
					newcode = getPublicControllerBase().OngetNewCode((CJPABase) this, codeid);
				if ((newcode == null) && (getController() != null))
					newcode = getController().OngetNewCode((CJPABase) this, codeid);
				if (newcode == null)
					newcode = getNewCode(codeid);
				cf.setValue(newcode);
				if ((cf.getValue() == null) || cf.getValue().isEmpty()) {
					throw new Exception("获取ID为【" + cf.getCodeid() + "】的编码错误!");
				}
			}
		}
		if (selfLink)
			for (CJPALineData<CJPABase> ld : cJPALileDatas) {
				for (CJPABase cjpa : ld) {
					((CJPASave) cjpa).checkOrCreateIDCode(con, selfLink);
				}
			}
	}

	private void checkNotNullFields() throws Exception {
		if (getJpaStat() == CJPAStat.RSDEL)
			return;
		for (CField cf : cFields) {
			CFieldinfo cfi = cf.getFdinfo();
			if (cf.isEmpty() && (cfi.defvalue() != null)) {
				cf.setValue(cfi.defvalue());
			}
			if (!cf.isNullable() && ((cf.getValue() == null) || cf.getValue().isEmpty()) && (!cf.isAutoinc())
					&& ((cf.getEditconst() == null) || cf.getEditconst().isEmpty())) {
				// System.out.println("fieldname:" + cf.getFieldname() +
				// "isNullable:" + cf.isNullable() + " value:" + cf.getValue() +
				// " isAutoinc:"
				// + cf.isAutoinc());
				String cp = cf.getCaption();
				if ((cp == null) || (cp.isEmpty()))
					cp = cf.getFieldname();
				throw new Exception("【" + this.tablename + "】的字段【" + cp + "】【" + cf.getFieldname() + "】不允许为空!");
			}
		}
	}

	/*
	 * 只有插入新数据功能
	 */
	public void buildInsParms(CDBConnection con, ArrayList<PraperedSql> sqllist) throws Exception {
		String insfds = "";
		String insvls = "";
		ArrayList<PraperedValue> iparms = new ArrayList<PraperedValue>();
		if (isallnull(false)) {
			Logsw.debug("【" + getClass().getName() + "】JPA自身数据为空,且子表数据也为空，放弃保存!");
			return;
		}

		checkOrCreateIDCode(con, false);// 检查Key 自动Code字段，如果是新建 将 自动生成 子关联数据列表也要设置，
		// 可能会使用子关联数据ID更新主JPA关联字段的值
		putDefaultValaus(); // 设置默认值
		putLinkFieldsValue();// 设置从JPA关联字段的值 主-->从 从-->主
		checkNotNullFields();// 检查不能为空的字段的值

		for (CField cf : cFields) {
			// ///blob 处理
			if (cf.isIskey()) {
				insfds = insfds + CJPASqlUtil.getSqlField(con.pool.getDbtype(), cf.getFieldname()) + ",";
				insvls = insvls + "?,";
				iparms.add(CJPASqlUtil.getSqlPValue(con.pool.getDbtype(), cf));
			} else {
				if (!cf.isIgnoreSave()) {
					insfds = insfds + CJPASqlUtil.getSqlField(con.pool.getDbtype(), cf.getFieldname()) + ",";
					insvls = insvls + "?,";
					iparms.add(CJPASqlUtil.getSqlPValue(con.pool.getDbtype(), cf));
				}
			}
		}
		if ((insfds != null) && (!insfds.isEmpty()))
			insfds = insfds.substring(0, insfds.length() - 1);
		if ((insvls != null) && (!insvls.isEmpty()))
			insvls = insvls.substring(0, insvls.length() - 1);

		String sqlstr = null;
		ArrayList<PraperedValue> sparms = new ArrayList<PraperedValue>();
		sqlstr = "insert into " + tablename + "(" + insfds + ") values(" + insvls + ")";
		sparms = iparms;

		if ((sqlstr != null) && !sqlstr.isEmpty()) {
			PraperedSql psql = new PraperedSql();
			psql.setSqlstr(sqlstr);
			psql.setParms(sparms);
			sqllist.add(psql);
		}
	}

	/**
	 * 构建SQL语句
	 * 
	 * @param con
	 * @param sqllist
	 * @param selfLink
	 * @throws Exception
	 */
	public void buildSqlParms(CDBConnection con, ArrayList<PraperedSql> sqllist, boolean selfLink) throws Exception {
		String insfds = "";
		String insvls = "";
		String upfvs = "";
		String upwhe = "";
		ArrayList<PraperedValue> iparms = new ArrayList<PraperedValue>();
		ArrayList<PraperedValue> uparms = new ArrayList<PraperedValue>();
		ArrayList<PraperedValue> warms = new ArrayList<PraperedValue>();

		if (isallnull(selfLink)) {
			Logsw.debug("【" + getClass().getName() + "】JPA自身数据为空,且子表数据也为空，放弃保存!");
			return;
		}
		checkOrCreateIDCode(con, selfLink);// 检查Key 自动Code字段，如果是新建 将 自动生成 子关联数据列表也要设置，
		// 可能会使用子关联数据ID更新主JPA关联字段的值
		putDefaultValaus(); // 设置默认值
		putLinkFieldsValue();// 设置从JPA关联字段的值 主-->从 从-->主
		// System.out.println(this.toString());
		checkNotNullFields();// 检查不能为空的字段的值

		for (CField cf : cFields) {
			// ///blob 处理
			if (cf.isIskey()) {
				if ((cf.getEditconst() != null) && (!cf.getEditconst().isEmpty())) {
					insfds = insfds + CJPASqlUtil.getSqlField(con.pool.getDbtype(), cf.getFieldname()) + ",";
					insvls = insvls + cf.getEditconst() + ",";
					upwhe = upwhe + " and " + CJPASqlUtil.getSqlField(con.pool.getDbtype(), cf.getFieldname()) + "=" + cf.getEditconst();
				} else {
					insfds = insfds + CJPASqlUtil.getSqlField(con.pool.getDbtype(), cf.getFieldname()) + ",";
					insvls = insvls + "?,";
					iparms.add(CJPASqlUtil.getSqlPValue(con.pool.getDbtype(), cf));
					upwhe = upwhe + " and " + CJPASqlUtil.getSqlField(con.pool.getDbtype(), cf.getFieldname()) + "=?";
					warms.add(CJPASqlUtil.getSqlPValue(con.pool.getDbtype(), cf));
				}

			} else {
				if (!cf.isIgnoreSave()) {
					if (cf.isChanged()) {
						// System.out.println("fieldname:" + cf.getFieldname() + " chged:" + cf.getValue());
						if ((cf.getEditconst() != null) && (!cf.getEditconst().isEmpty())) {
							upfvs = upfvs + CJPASqlUtil.getSqlField(con.pool.getDbtype(), cf.getFieldname()) + "=" + cf.getEditconst() + ",";
						} else {
							upfvs = upfvs + CJPASqlUtil.getSqlField(con.pool.getDbtype(), cf.getFieldname()) + "=?,";
							uparms.add(CJPASqlUtil.getSqlPValue(con.pool.getDbtype(), cf));
						}
					} // else
						// System.out.println("fieldname:" + cf.getFieldname() + " not chged");
					insfds = insfds + CJPASqlUtil.getSqlField(con.pool.getDbtype(), cf.getFieldname()) + ",";
					if ((cf.getEditconst() != null) && (!cf.getEditconst().isEmpty())) {
						insvls = insvls + cf.getEditconst() + ",";
					} else {
						insvls = insvls + "?,";
						iparms.add(CJPASqlUtil.getSqlPValue(con.pool.getDbtype(), cf));
					}
				}
			}
		}
		if ((insfds != null) && (!insfds.isEmpty()))
			insfds = insfds.substring(0, insfds.length() - 1);
		if ((insvls != null) && (!insvls.isEmpty()))
			insvls = insvls.substring(0, insvls.length() - 1);
		if ((upfvs != null) && (!upfvs.isEmpty()))
			upfvs = upfvs.substring(0, upfvs.length() - 1);

		// System.out.println(insfds + " " + insvls);

		if ((upwhe == null) || upwhe.isEmpty()) {
			throw new Exception("无法生成条件，是否忘记设置Key字段?");
		}

		this._cjpaact = JPAAction.actNull;
		String sqlstr = null;
		ArrayList<PraperedValue> sparms = new ArrayList<PraperedValue>();
		if ((getJpaStat() == CJPAStat.RSINSERT) && (!insfds.isEmpty())) {
			this._cjpaact = JPAAction.actInsert;
			sqlstr = "insert into " + tablename + "(" + insfds + ") values(" + insvls + ")";
			sparms = iparms;
		}
		if ((getJpaStat() == CJPAStat.RSUPDATED) && (!upfvs.isEmpty())) {
			this._cjpaact = JPAAction.actUpdate;
			sqlstr = "update " + tablename + " set " + upfvs + " where 1=1 " + upwhe;
			sparms = uparms;
			sparms.addAll(warms);
		}
		if ((getJpaStat() == CJPAStat.RSDEL) && (!upwhe.isEmpty())) {
			this._cjpaact = JPAAction.actDel;
			sqlstr = "delete from " + tablename + " where 1=1 " + upwhe;
			sparms = warms;
		}
		if (selfLink) {
			buildSelfLinksSQLParms(con, sqllist);
		}
		if ((sqlstr != null) && !sqlstr.isEmpty()) {
			PraperedSql psql = new PraperedSql();
			psql.setSqlstr(sqlstr);
			psql.setParms(sparms);
			sqllist.add(psql);
		}
	}

	/**
	 * 构建SQL语句
	 * 
	 * @param con
	 * @param sqllist
	 * @throws Exception
	 */
	public void buildSelfLinksSQLParms(CDBConnection con, ArrayList<PraperedSql> sqllist) throws Exception {
		for (CJPALineData<CJPABase> jpaline : cJPALileDatas) {
			for (CJPABase cjpa : jpaline) {
				if (getJpaStat() == CJPAStat.RSDEL) { // 如果主JPA需要删除，所有明细数据必须为删除状态
					((CJPASave) cjpa).setJpaStat(CJPAStat.RSDEL);
				}
				if (getJpaStat() == CJPAStat.RSINSERT) { // 如果主JPA
															// 为新插入状态，所有明细数据必须为新插入状态
					((CJPASave) cjpa).setJpaStat(CJPAStat.RSINSERT);
				}
				((CJPASave) cjpa).buildSqlParms(con, sqllist, true);
			}
		}
	}

	/**
	 * 构建SQL语句-旧
	 * 
	 * @param con
	 * @param sqllist
	 * @param ABlobList
	 * @param selfLink
	 * @throws Exception
	 */
	public void buildsql_old(CDBConnection con, ArrayList<String> sqllist, ArrayList<BLOBUpDateRecoed> ABlobList, boolean selfLink) throws Exception {
		String insfds = "";
		String insvls = "";
		String upfvs = "";
		String upwhe = "";
		if (isallnull(selfLink)) {
			Logsw.debug("【" + getClass().getName() + "】JPA自身数据为空,且子表数据也为空，放弃保存!");
			return;
		}
		checkOrCreateIDCode(con, selfLink);// 检查Key 自动Code字段，如果是新建 将 自动生成 子关联数据列表也要设置，
		// 可能会使用子关联数据ID更新主JPA关联字段的值
		putDefaultValaus(); // 设置默认值
		putLinkFieldsValue();// 设置从JPA关联字段的值 主-->从 从-->主
		checkNotNullFields();// 检查不能为空的字段的值
		for (CField cf : cFields) {
			// ///blob 处理
			if (cf.isIskey()) {
				insfds = insfds + CJPASqlUtil.getSqlField(con.pool.getDbtype(), cf.getFieldname()) + ",";
				insvls = insvls + CJPASqlUtil.getSqlValue(con.pool.getDbtype(), cf.getFieldtype(), cf.getValue()) + ",";
				upwhe = upwhe + " and " + CJPASqlUtil.getSqlField(con.pool.getDbtype(), cf.getFieldname()) + "="
						+ CJPASqlUtil.getSqlValue(con.pool.getDbtype(), cf.getFieldtype(), cf.getValue());
			} else {
				if (!cf.isIgnoreSave()) {
					if (cf.isChanged()) {
						upfvs = upfvs + CJPASqlUtil.getSqlField(con.pool.getDbtype(), cf.getFieldname()) + "="
								+ CJPASqlUtil.getSqlValue(con.pool.getDbtype(), cf.getFieldtype(), cf.getValue()) + ",";
					}
					insfds = insfds + CJPASqlUtil.getSqlField(con.pool.getDbtype(), cf.getFieldname()) + ",";
					insvls = insvls + CJPASqlUtil.getSqlValue(con.pool.getDbtype(), cf.getFieldtype(), cf.getValue()) + ",";
				}
			}
		}

		if ((insfds != null) && (!insfds.isEmpty()))
			insfds = insfds.substring(0, insfds.length() - 1);
		if ((insvls != null) && (!insvls.isEmpty()))
			insvls = insvls.substring(0, insvls.length() - 1);
		if ((upfvs != null) && (!upfvs.isEmpty()))
			upfvs = upfvs.substring(0, upfvs.length() - 1);

		// System.out.println(insfds + " " + insvls);

		if ((upwhe == null) || upwhe.isEmpty()) {
			throw new Exception("无法生成条件，是否忘记设置Key字段?");
		}
		this._cjpaact = JPAAction.actNull;
		String sqlstr = null;
		if ((getJpaStat() == CJPAStat.RSINSERT) && (!insfds.isEmpty())) {// 插入
			sqlstr = "insert into " + tablename + "(" + insfds + ") values(" + insvls + ")";
			this._cjpaact = JPAAction.actInsert;
		}
		if ((getJpaStat() == CJPAStat.RSUPDATED) && (!upfvs.isEmpty())) {// 更新
			sqlstr = "update " + tablename + " set " + upfvs + " where 1=1 " + upwhe;
			this._cjpaact = JPAAction.actUpdate;
		}
		if ((getJpaStat() == CJPAStat.RSDEL) && (!upwhe.isEmpty())) {// 删除
			sqlstr = "delete from " + tablename + " where 1=1 " + upwhe;
			this._cjpaact = JPAAction.actDel;
		}

		if (selfLink) {
			buildSelfLinksSQL_old(con, sqllist, ABlobList);
		}
		if ((sqlstr != null) && !sqlstr.isEmpty())
			sqllist.add(sqlstr);
	}

	/**
	 * 构建行表SQL语句-旧
	 * 
	 * @param con
	 * @param sqllist
	 * @param ABlobList
	 * @throws Exception
	 */
	public void buildSelfLinksSQL_old(CDBConnection con, ArrayList<String> sqllist, ArrayList<BLOBUpDateRecoed> ABlobList) throws Exception {
		for (CJPALineData<CJPABase> jpaline : cJPALileDatas) {
			for (CJPABase cjpa : jpaline) {
				if (getJpaStat() == CJPAStat.RSDEL) { // 如果主JPA需要删除，所有明细数据必须为删除状态
					((CJPASave) cjpa).setJpaStat(CJPAStat.RSDEL);
				}
				if (getJpaStat() == CJPAStat.RSINSERT) { // 如果主JPA
															// 为新插入状态，所有明细数据必须为新插入状态
					((CJPASave) cjpa).setJpaStat(CJPAStat.RSINSERT);
				}
				((CJPASave) cjpa).buildsql_old(con, sqllist, ABlobList, true);
			}
		}
	}

	/**
	 * 保存
	 * 
	 * @return
	 * @throws Exception
	 */
	public CJPABase save() throws Exception {
		save(true);
		return (CJPABase) this;
	}

	/**
	 * 保存
	 * 
	 * @param selfLink
	 * @return
	 * @throws Exception
	 */
	public CJPABase save(boolean selfLink) throws Exception {
		CDBConnection con = pool.getCon(this);
		con.startTrans();
		try {
			save(con, selfLink);
			con.submit();
			applayChange();
			return (CJPABase) this;
		} catch (Exception e) {
			con.rollback();
			throw e;
		} finally {
			con.close();
		}
	}

	//
	/**
	 * 外部传入 con 的时候 支持 外部事务处理 外部提交事务后 需要 调用applayChange；
	 * 
	 * @param con
	 * @return
	 * @throws Exception
	 */
	public CJPABase save(CDBConnection con) throws Exception {
		save(con, true);
		return (CJPABase) this;
	}

	//
	/**
	 * 外部传入 con 的时候 支持 外部事务处理外部提交事务后 需要 调用applayChange； 跨数据库连接池的事务处理不予支持
	 * 
	 * @param con
	 * @param selfLink
	 * @return
	 * @throws Exception
	 */
	public CJPABase save(CDBConnection con, boolean selfLink) throws Exception {
		if ((pool != null) && (pool.pprm != null))
			if (!con.pool.pprm.name.equalsIgnoreCase(pool.pprm.name)) {
				throw new Exception("保存JPA传入的连接池与JPA本身的连接池不一致!");
			}
		CField kf = getIDField();
		if (kf == null) {
			throw new Exception("实体没有发现主关键字");
		}
		if (kf.isEmpty()) {// //////////////////15 01 07
			setJpaStat(CJPAStat.RSINSERT);
		}

		if (getPublicControllerBase() != null)
			getPublicControllerBase().BeforeSave((CJPABase) this, con, selfLink);
		if (getController() != null)
			getController().BeforeSave((CJPABase) this, con, selfLink);

		ArrayList<PraperedSql> sqllist = new ArrayList<PraperedSql>();
		buildSqlParms(con, sqllist, selfLink);// 生成SQL语句时候重置了JPA Action 状态，所以 docheckdeljpa 方法必须在后面

		if (getPublicControllerBase() != null)
			getPublicControllerBase().OnSave((CJPABase) this, con, sqllist, selfLink);
		if (getController() != null)
			getController().OnSave((CJPABase) this, con, sqllist, selfLink);
		docheckdeljpa(con, selfLink);
		PreparedStatement pstmt = null;
		for (PraperedSql psql : sqllist) {
			Logsw.debug("执行事务处理:" + psql.getSqlstr());
			try {
				long time = System.currentTimeMillis();
				pstmt = con.con.prepareStatement(psql.getSqlstr());
				for (int i = 0; i < psql.getParms().size(); i++) {
					PraperedValue pv = psql.getParms().get(i);
					CPoolSQLUtil.setSqlPValue(pstmt, i + 1, pv);
				}
				if (DBPools.getCblog() != null) {
					DBPools.getCblog().writelog(con, "【计划执行SQL：" + psql.getSqlstr() + "】");
				}
				pstmt.execute();
				if (DBPools.getCblog() != null) {
					long et = System.currentTimeMillis();
					DBPools.getCblog().writelog(con, "耗时:" + (et - time) + "】");
				}
			} catch (Exception e) {
				Logsw.debug("执行事务处理错误:" + this.getClass().getName());
				throw e;
			} finally {
				DBPools.safeCloseS_R(pstmt, null);
			}
		}

		// 保存完后对于标记为删除的自关联数据需要删除

		// blob clob保存
		// setAllJpaStat(CJPAStat.RSLOAD4DB); 屏蔽掉 否则未remove的行表无法remove moyh 170809
		applayChange();// 加上，否则新插入数据 下次保存会试着重新插入 触发ID重复错误 moyh 170809

		if (getPublicControllerBase() != null)
			getPublicControllerBase().AfterSave((CJPABase) this, con, selfLink);
		if (getController() != null)
			getController().AfterSave((CJPABase) this, con, selfLink);

		return (CJPABase) this;
	}

	/**
	 * 保存前检查JPA状态，并触发JPAAction
	 * 
	 * @param con
	 * @param selfLink
	 * @throws Exception
	 */
	private void docheckdeljpa(CDBConnection con, boolean selfLink) throws Exception {
		if (getPublicControllerBase() != null)
			getPublicControllerBase().OnJAPAction(this, con, _cjpaact);
		if (getController() != null)
			getController().OnJAPAction(this, con, _cjpaact);
		if (selfLink) {
			for (CJPALineData<CJPABase> jpaline : cJPALileDatas) {
				for (CJPABase cjpa : jpaline) {
					((CJPASave) cjpa).docheckdeljpa(con, selfLink);
				}
			}
		}
	}

	/*
	 * 完成数据库提交后 需要将标记为删除的jap干掉， 如果主JPA不空，则设置为rsLoad4DB,否则设置为rsInsert
	 */
	public void applayChange() throws Exception {
		String id = getid();
		if ((id == null) || (id.isEmpty()) || (getJpaStat() == CJPAStat.RSDEL)) {// 没有主键值或需要删除的JPA
			clear();
			return;
		}
		for (CField fd : cFields) {
			if (fd != null)
				fd.setChanged(false);
		}
		setJpaStat(CJPAStat.RSLOAD4DB);
		clearJPAAction();
		for (CJPALineData<CJPABase> clds : cJPALileDatas) {
			Iterator<CJPABase> iclds = clds.iterator();
			while (iclds.hasNext()) {
				CJPABase jpa = iclds.next();
				// ((CJPASave) jpa).applayChange(); 不能放这里， 该方法会改变
				// cJpaStat值，导致不能移除数据；
				if (jpa.getJpaStat() == CJPAStat.RSDEL) {
					// System.out.println("del:" + ((CJPASave) jpa).tojson());
					((CJPASave) jpa).applayChange(); // 2017-08-09
					iclds.remove();
					jpa = null;
				} else
					((CJPASave) jpa).applayChange();
			}
		}
	}

	/**
	 * 删除
	 * 
	 * @param con
	 * @param id
	 * @param selfLink
	 * @throws Exception
	 */
	public void delete(CDBConnection con, String id, boolean selfLink) throws Exception {
		// List<CLinkFieldInfo> lfis = CjpaUtil.getLineLinkedInfos(this.getClass(), false);
		getIDField().setValue(id);
		// if (lfis.size() == 0) {// 没有注解用老的方式删除
		findByID(con, id, selfLink);// 16 04 12
		setJpaStat(CJPAStat.RSDEL);
		if (getPublicControllerBase() != null)
			getPublicControllerBase().OnDelete((CJPABase) this, con, selfLink);
		if (getController() != null)
			getController().OnDelete((CJPABase) this, con, selfLink);
		save(con, selfLink);
		// } else {// 根据注解关联 删除数据 新的删除方式，快，但对于数据库行锁会有问题
		// if (getPublicControllerBase() != null)
		// getPublicControllerBase().OnDelete((CJPABase) this, con, selfLink);
		// if (getController() != null)
		// getController().OnDelete((CJPABase) this, con, selfLink);
		// CJPASqlBuilding csb = new CJPASqlBuilding();
		// List<String> sqls = csb.getDelSqlsByAnno(this.getClass(), id, selfLink);
		// if (getController() != null)
		// getController().OnJAPAction(this, con, JPAAction.actDel);
		// con.execSqls(sqls);
		// // 检查删除关联数据
		// // 1 删除前检查： 如果关联的数据在在本次删除之内,导致异常错误不允许删除；
		// // 2 删除后检查： 数据已经删除了，检查个毛线
		// }
	}

	/**
	 * 删除
	 * 
	 * @param id
	 * @param selfLink
	 * @throws Exception
	 */
	public void delete(String id, boolean selfLink) throws Exception {
		CDBConnection con = pool.getCon(this);
		con.startTrans();
		try {
			delete(con, id, selfLink);
			con.submit();
		} catch (Exception e) {
			con.rollback();
			throw e;
		} finally {
			con.close();
		}
	}

	/**
	 * 删除
	 * 
	 * @param con
	 * @param selfLink
	 * @throws Exception
	 */
	public void delete(CDBConnection con, boolean selfLink) throws Exception {
		delete(con, this.getid(), selfLink);
	}

	/**
	 * 删除
	 * 
	 * @param selfLink
	 * @throws Exception
	 */
	public void delete(boolean selfLink) throws Exception {
		CDBConnection con = pool.getCon(this);
		con.startTrans();
		try {
			delete(con, this.getid(), selfLink);
			con.submit();
		} catch (Exception e) {
			con.rollback();
			throw e;
		} finally {
			con.close();
		}
	}

	/**
	 * 删除
	 * 
	 * @throws Exception
	 */
	public void delete() throws Exception {
		CDBConnection con = pool.getCon(this);
		con.startTrans();
		try {
			delete(con, this.getid(), true);
			con.submit();
		} catch (Exception e) {
			con.rollback();
			throw e;
		} finally {
			con.close();
		}
	}

}
