package com.corsair.server.eai;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.corsair.cjpa.util.CJPASqlUtil;
import com.corsair.dbpool.CDBConnection;
import com.corsair.dbpool.CDBConnection.DBType;
import com.corsair.dbpool.DBPools;
import com.corsair.dbpool.util.Logsw;
import com.corsair.dbpool.util.Systemdate;
import com.corsair.server.base.ConstsSw;
import com.corsair.server.eai.CEAIParamBase.EAITYPE;
import com.corsair.server.eai.CEAIParamBase.TRANASTYPE;
import com.corsair.server.util.CorUtil;

public class CEAI {
	private CEAI ownerCEAI = null;
	private CEAIParam ceaip = null;
	private DBType sdbtype, ddbtype;
	private Date lastSourseDate = null;
	private List<CEAI> chileCEAIs = new ArrayList<CEAI>();
	private List<CEAIFielValue> sourseRecord = null;

	public CEAI(CEAIParam ceaip) throws Exception {
		this.ceaip = ceaip;
		try {
			String dts = ConstsSw.eaiDatesProtertys.getProperties(ceaip.getName());
			if ((dts == null) || dts.isEmpty()) {
				dts = "1980-01-01 00:00:00";
			}
			// System.out.println("init eai date:"+dts);
			lastSourseDate = Systemdate.getDateByStr(dts);
			try {
				for (CChildEAIParm cdeai : ceaip.getChildeais()) {
					CEAI cceai = new CEAI(cdeai.getCdeaiparam());
					cceai.setOwnerCEAI(this);
					chileCEAIs.add(cceai);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				throw new Exception("创建 Child EAI实例错误:" + e.getMessage());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new Exception("创建EAI实例错误:" + e.getMessage());
		}

	}

	// ///////////////////////允许继承//////////////////////////////

	protected String OnGetCondtFieldValue(String fdname, String value, Date lastSourseDate) throws Exception {
		return value;
	}

	protected String OnGetSourceFieldValue(String fdname, String value, List<CEAIFielValue> srecord) throws Exception {
		return value;
	}

	// //type 1 生成子EAI查询 2 生成更新语句 3 生成插入语句
	protected void OnGetOwnerLinkFieldValue(int type, String fdname, String value, List<CEAIFielValue> srecord) throws Exception {

	}

	// 完成数据行导入
	protected void OnSuccessSysDataRow(CDBConnection cond, List<CEAIFielValue> srecord) throws Exception {

	}

	// 完成一次调度
	protected void OnSuccessSysData(Date cdate) throws Exception {

	}

	// ////////////////////////////////////////////////////

	private String getCEAIFielValue(String fieldname, List<CEAIFielValue> sr) {
		for (CEAIFielValue fl : sr) {
			if (fl.fieldname.equalsIgnoreCase(fieldname)) {
				return fl.value;
			}
		}
		return null;
	}

	private String buildCdtWhere() throws Exception {
		String sqlwhere = "";
		for (CEAICondt cdt : ceaip.getCondts()) {
			String fdname = CJPASqlUtil.getSqlField(sdbtype, cdt.getField());
			String value = OnGetCondtFieldValue(cdt.getField(), cdt.getValue(), lastSourseDate);
			value = CJPASqlUtil.getSqlValue(ddbtype, cdt.getFieldtype(), value);
			sqlwhere = sqlwhere + " and " + fdname + " " + cdt.getOper() + " " + value;
		}

		if ((ceaip.getLastupdatefield() != null) && (!ceaip.getLastupdatefield().isEmpty())) {
			String value = Systemdate.getStrDate(lastSourseDate);
			String aa = CJPASqlUtil.getSqlField(ddbtype, ceaip.getLastupdatefield()) + ">=" + CJPASqlUtil.getSqlValue(ddbtype, Types.TIMESTAMP, value);
			sqlwhere = sqlwhere + " and " + aa;
		}

		// //////考虑关联字段提供条件/
		CEAI owner = getOwnerCEAI();
		if ((ceaip.getEaitype() == EAITYPE.ParEAI) && (owner != null)) {
			List<CEAIFielValue> sr = owner.getSourseRecord();
			// System.out.println("!!!!!!!!!!!!" + owner.ceaip.getName() + ":" +
			// owner.ceaip.getChildeais().size());
			CChildEAIParm cdeai = owner.getCeaip().getChdEaiByParam(ceaip);
			if (cdeai == null) {
				throw new Exception("从EAI" + ceaip.getName() + "的主EAI中查找 ChildEAI 配置为空");
			}
			for (EAIMapField elf : cdeai.getLinkfields()) {
				String value = getCEAIFielValue(elf.getS_field(), sr);
				if (value == null)
					throw new Exception("子EAI关联源字段" + elf.getS_field() + "不存在!");
				String fdname = CJPASqlUtil.getSqlField(sdbtype, elf.getD_field());
				if (elf.getD_fieldtype() == -999) {
					throw new Exception("子EAI关联目标字段" + fdname + "不存在!");
				}
				OnGetOwnerLinkFieldValue(1, fdname, value, sr);
				value = CJPASqlUtil.getSqlValue(ddbtype, elf.getD_fieldtype(), value);
				sqlwhere = sqlwhere + " and " + fdname + "=" + value;
			}
		}

		return sqlwhere;
	}

	public void run() {
		if (!ceaip.isEnable())
			return;
		Logsw.debug("Exceute EAI:" + ceaip.getName());
		CDBConnection cons = null;
		CDBConnection cond = null;
		String sqlstr = null;
		try {
			cons = getScon();
			cond = getDcon();
			sdbtype = cons.getDBType();
			ddbtype = cond.getDBType();
			sqlstr = "select sysdate() as cdate,a.* from " + ceaip.getS_tablename() + " a where 1=1 " + buildCdtWhere();
			Logsw.debug(getCeaip().getName() + "载入EAI数据SQL:" + sqlstr);
			Statement stmt = cons.con.createStatement();
			ResultSet rs = stmt.executeQuery(sqlstr);
			if (ceaip.getTrans_type() == TRANASTYPE.disp)
				cond.startTrans();
			syndata_rset(stmt, rs, cond);
			if (ceaip.getTrans_type() == TRANASTYPE.disp)
				cond.submit();
		} catch (Exception e) {
			if (ceaip.getTrans_type() == TRANASTYPE.disp)
				cond.rollback();
			try {
				Logsw.error(sqlstr, e);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} finally {
			cons.close();
			cond.close();
		}
	}

	private List<CEAIFielValue> buildSKeyMap(ResultSet rs) throws SQLException {
		ResultSetMetaData rsmd = rs.getMetaData(); // 得到结果集的定义结构
		List<CEAIFielValue> rst = new ArrayList<CEAIFielValue>();
		for (int i = 1; i <= rsmd.getColumnCount(); i++) {
			rst.add(new CEAIFielValue(rsmd.getColumnLabel(i), rs.getString(i)));
		}
		return rst;
	}

	// 创建目的表查询条件
	private String buildDWhere(ResultSet rs, List<CEAIFielValue> srecord) throws Exception {
		String swhere = "";
		for (EAIMapField kfd : ceaip.getKeyfieds()) {
			String value = rs.getString(kfd.getS_field());
			value = OnGetSourceFieldValue(kfd.getS_field(), value, srecord);
			String aa = CJPASqlUtil.getSqlField(ddbtype, kfd.getD_field()) + "=" + CJPASqlUtil.getSqlValue(ddbtype, kfd.getD_fieldtype(), value);
			swhere = swhere + " and " + aa;
		}
		if (!swhere.isEmpty())
			swhere = " where 1=1 " + swhere;
		return swhere;
	}

	private String buildInsertSQL(ResultSet rs, List<CEAIFielValue> srecord) throws Exception {
		String sqlstr = "";
		String insfdd = "", insvls = "";
		String value;
		for (EAIMapField kfd : ceaip.getKeyfieds()) {
			insfdd = insfdd + CJPASqlUtil.getSqlField(ddbtype, kfd.getD_field()) + ",";
			value = rs.getString(kfd.getS_field());
			value = OnGetSourceFieldValue(kfd.getS_field(), value, srecord);
			insvls = insvls + CJPASqlUtil.getSqlValue(ddbtype, kfd.getD_fieldtype(), value) + ",";
		}
		for (EAIMapField mfd : ceaip.getMapfields()) {
			insfdd = insfdd + CJPASqlUtil.getSqlField(ddbtype, mfd.getD_field()) + ",";
			value = rs.getString(mfd.getS_field());
			value = OnGetSourceFieldValue(mfd.getS_field(), value, srecord);
			insvls = insvls + CJPASqlUtil.getSqlValue(ddbtype, mfd.getD_fieldtype(), value) + ",";
		}

		// ////关联字段处理///////
		CEAI owner = getOwnerCEAI();
		if ((ceaip.getEaitype() == EAITYPE.ParEAI) && (owner != null)) {
			List<CEAIFielValue> sr = owner.getSourseRecord();
			CChildEAIParm cdeai = owner.getCeaip().getChdEaiByParam(ceaip);
			if (cdeai == null) {
				throw new Exception("从EAI" + ceaip.getName() + "的主EAI中查找 ChildEAI 配置为空");
			}
			for (EAIMapField elf : cdeai.getLinkfields()) {
				value = getCEAIFielValue(elf.getS_field(), sr);
				if (value == null)
					throw new Exception("子EAI关联源字段" + elf.getS_field() + "不存在!");
				String fdname = CJPASqlUtil.getSqlField(sdbtype, elf.getD_field());
				if (elf.getD_fieldtype() == -999) {
					throw new Exception("子EAI关联目标字段" + fdname + "不存在!");
				}
				OnGetOwnerLinkFieldValue(2, fdname, value, sr);
				insfdd = insfdd + fdname + ",";
				insvls = insvls + CJPASqlUtil.getSqlValue(ddbtype, elf.getD_fieldtype(), value) + ",";
			}
		}
		// //
		if ((insfdd != null) && (!insfdd.isEmpty()))
			insfdd = insfdd.substring(0, insfdd.length() - 1);
		if ((insvls != null) && (!insvls.isEmpty()))
			insvls = insvls.substring(0, insvls.length() - 1);
		sqlstr = "insert into " + ceaip.getT_tablename() + "(" + insfdd + ") values(" + insvls + ")";
		return sqlstr;
	}

	private String buildUpdateSQL(ResultSet rs, List<CEAIFielValue> srecord) throws Exception {
		String sqlstr = "update " + ceaip.getT_tablename() + " set ";
		String value;
		// System.out.println(ceaip.getMapfields().size());
		for (EAIMapField mfd : ceaip.getMapfields()) {
			value = rs.getString(mfd.getS_field());
			value = OnGetSourceFieldValue(mfd.getS_field(), value, srecord);
			sqlstr = sqlstr + CJPASqlUtil.getSqlField(ddbtype, mfd.getD_field()) + "=" + CJPASqlUtil.getSqlValue(ddbtype, mfd.getD_fieldtype(), value) + ",";
		}

		// ////关联字段处理///////
		CEAI owner = getOwnerCEAI();
		if ((ceaip.getEaitype() == EAITYPE.ParEAI) && (owner != null)) {
			List<CEAIFielValue> sr = owner.getSourseRecord();
			CChildEAIParm cdeai = owner.getCeaip().getChdEaiByParam(ceaip);
			if (cdeai == null) {
				throw new Exception("从EAI" + ceaip.getName() + "的主EAI中查找 ChildEAI 配置为空");
			}
			for (EAIMapField elf : cdeai.getLinkfields()) {
				value = getCEAIFielValue(elf.getS_field(), sr);
				if (value == null)
					throw new Exception("子EAI关联源字段" + elf.getS_field() + "不存在!");
				String fdname = elf.getD_field();
				if (elf.getD_fieldtype() == -999) {
					throw new Exception("子EAI关联目标字段" + fdname + "不存在!");
				}
				OnGetOwnerLinkFieldValue(3, fdname, value, sr);
				sqlstr = sqlstr + CJPASqlUtil.getSqlField(sdbtype, fdname) + "=" + CJPASqlUtil.getSqlValue(ddbtype, elf.getD_fieldtype(), value) + ",";
			}
		}

		if ((sqlstr != null) && (!sqlstr.isEmpty()))
			sqlstr = sqlstr.substring(0, sqlstr.length() - 1);
		return sqlstr;
	}

	// 导数据主函数
	private void syndata_rset(Statement stmt, ResultSet rs, CDBConnection cond) throws Exception {
		String sqlstr = null;
		Date cdate = null;
		if (!rs.first())
			return;
		while (true) {
			// //////创建 源表 KeyMap 共子类继承时用
			if (ceaip.getTrans_type() == TRANASTYPE.row)
				cond.startTrans();
			try {
				cdate = rs.getTimestamp("cdate");
				sourseRecord = buildSKeyMap(rs);// // 源数据行 /////
				String sqlwhere = buildDWhere(rs, sourseRecord);
				sqlstr = "select * from " + ceaip.getT_tablename() + sqlwhere;
				if (ConstsSw.getAppParmBoolean("Debug_Mode"))
					System.out.println(getCeaip().getName() + "检查目标表存在数据SQL:" + sqlstr);
				Statement stmtd = cond.con.createStatement();
				ResultSet rsd = stmtd.executeQuery(sqlstr);
				if (rsd.first()) {
					sqlstr = buildUpdateSQL(rs, sourseRecord) + sqlwhere;
					try {
						if (ConstsSw.getAppParmBoolean("Debug_Mode"))
							System.out.println(getCeaip().getName() + "更新目标表数据SQL:" + sqlstr);
						stmtd.execute(sqlstr);
						Logsw.debug("EAI " + ceaip.getName() + "执行更新完成");
					} catch (Exception e) {
						Logsw.error("EAI " + ceaip.getName() + "执行更新错误:" + sqlstr, e);
						throw e;
					}
					// System.out.println(sqlstr);
				} else if (ceaip.isAllow_insert()) {
					sqlstr = buildInsertSQL(rs, sourseRecord);
					try {
						if (ConstsSw.getAppParmBoolean("Debug_Mode"))
							System.out.println(getCeaip().getName() + "插入目标表数据SQL:" + sqlstr);
						stmtd.execute(sqlstr);
						Logsw.debug("EAI " + ceaip.getName() + "执行插入完成");
					} catch (Exception e) {
						Logsw.error("EAI " + ceaip.getName() + "执行插入错误:" + sqlstr, e);
						throw e;
					}
					// System.out.println(sqlstr);
				}
				// ////////////////执行子EAI
				try {
					for (CEAI cceai : chileCEAIs) {
						cceai.run(); // 如何传条件到子cceai????? sourseRecord
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					throw new Exception(ceaip.getName() + "执行子EAI错误:" + e.getMessage());
				}
				// //////////////////////
				OnSuccessSysDataRow(cond, sourseRecord);
				if (ceaip.getTrans_type() == TRANASTYPE.row)
					cond.submit();
			} catch (Exception e) {
				if (ceaip.getTrans_type() == TRANASTYPE.row)
					cond.rollback();
				throw new Exception(e.getMessage());
			}

			if (!rs.next()) {
				break;
			}
		}
		lastSourseDate = cdate;
		ConstsSw.eaiDatesProtertys.setProperties(ceaip.getName(), Systemdate.getStrDate(cdate));
		OnSuccessSysData(cdate);
	}

	public CDBConnection getScon() throws Exception {
		CDBConnection con = DBPools.poolByName(ceaip.getDbpool_source()).getCon(this);
		if (con == null)
			new Exception("获取数据库连接错误:" + ceaip.getDbpool_source());
		return con;
	}

	public CDBConnection getDcon() throws Exception {
		CDBConnection con = DBPools.poolByName(ceaip.getDbpool_target()).getCon(this);
		if (con == null)
			new Exception("获取数据库连接错误:" + ceaip.getDbpool_target());
		return con;
	}

	public CEAIParam getCeaip() {
		return ceaip;
	}

	public void setCeaip(CEAIParam ceaip) {
		this.ceaip = ceaip;
	}

	public Date getLastSourseDate() {
		return lastSourseDate;
	}

	public List<CEAI> getChileCEAIs() {
		return chileCEAIs;
	}

	public void setChileCEAIs(List<CEAI> chileCEAIs) {
		this.chileCEAIs = chileCEAIs;
	}

	public CEAI getOwnerCEAI() {
		return ownerCEAI;
	}

	public void setOwnerCEAI(CEAI ownerCEAI) {
		this.ownerCEAI = ownerCEAI;
	}

	public List<CEAIFielValue> getSourseRecord() {
		return sourseRecord;
	}

	public void setSourseRecord(List<CEAIFielValue> sourseRecord) {
		this.sourseRecord = sourseRecord;
	}

}
