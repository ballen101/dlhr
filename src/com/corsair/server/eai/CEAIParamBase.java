package com.corsair.server.eai;

import java.util.ArrayList;
import java.util.List;

public abstract class CEAIParamBase {
	public enum EAITYPE {
		Poll, NoticePoll, ParEAI
	};

	public enum TRANASTYPE {
		row, disp, none
	};

	/*
	 * public enum Machtype { direct, java, sdbfunction, fdbfunction }
	 */
	private CEAIParamBase ownerEaiParam = null;
	private String name;
	private String eaicalss;
	private boolean enable;
	private TRANASTYPE trans_type; 
	private EAITYPE eaitype;
	private int frequency;
	private String dbpool_source;
	private String dbpool_target;
	private String lastupdatefield;
	// private String sdatasql;// 查询源数据的sql 用来获取源数据
	private String s_tablename; // 源表
	private String t_tablename;
	private boolean allow_insert;
	private List<EAIMapField> keyfieds = new ArrayList<EAIMapField>();
	private List<EAIMapField> mapfields = new ArrayList<EAIMapField>();
	private List<CEAICondt> condts = new ArrayList<CEAICondt>();
	private List<CChildEAIParm> childeais = new ArrayList<CChildEAIParm>();

	public String getName() {
		return name;
	}

	public boolean isEnable() {
		return enable;
	}

	public EAITYPE getEaitype() {
		return eaitype;
	}

	public int getFrequency() {
		return frequency;
	}

	public String getDbpool_source() {
		return dbpool_source;
	}

	public String getDbpool_target() {
		return dbpool_target;
	}

	public String getT_tablename() {
		return t_tablename;
	}

	public boolean isAllow_insert() {
		return allow_insert;
	}

	public List<EAIMapField> getKeyfieds() {
		return keyfieds;
	}

	public List<EAIMapField> getMapfields() {
		return mapfields;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	public void setEaitype(EAITYPE eaitype) {
		this.eaitype = eaitype;
	}

	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}

	public void setDbpool_source(String dbpool_source) {
		this.dbpool_source = dbpool_source;
	}

	public void setDbpool_target(String dbpool_target) {
		this.dbpool_target = dbpool_target;
	}

	public void setT_tablename(String t_tablename) {
		this.t_tablename = t_tablename;
	}

	public void setAllow_insert(boolean allow_insert) {
		this.allow_insert = allow_insert;
	}

	public void setKeyfieds(List<EAIMapField> keyfieds) {
		this.keyfieds = keyfieds;
	}

	public void setMapfields(List<EAIMapField> mapfields) {
		this.mapfields = mapfields;
	}

	public String getS_tablename() {
		return s_tablename;
	}

	public void setS_tablename(String s_tablename) {
		this.s_tablename = s_tablename;
	}

	public String getEaicalss() {
		return eaicalss;
	}

	public void setEaicalss(String eaicalss) {
		this.eaicalss = eaicalss;
	}

	public List<CEAICondt> getCondts() {
		return condts;
	}

	public void setCondts(List<CEAICondt> condts) {
		this.condts = condts;
	}

	public List<CChildEAIParm> getChildeais() {
		return childeais;
	}

	public void setChildeais(List<CChildEAIParm> childeais) {
		this.childeais = childeais;
	}

	public CEAIParamBase getOwnerEaiParam() {
		return ownerEaiParam;
	}

	public void setOwnerEaiParam(CEAIParamBase ownerEaiParam) {
		this.ownerEaiParam = ownerEaiParam;
	}

	public TRANASTYPE getTrans_type() {
		return trans_type;
	}

	public void setTrans_type(TRANASTYPE trans_type) {
		this.trans_type = trans_type;
	}

	public String getLastupdatefield() {
		return lastupdatefield;
	}

	public void setLastupdatefield(String lastupdatefield) {
		this.lastupdatefield = lastupdatefield;
	}

}
