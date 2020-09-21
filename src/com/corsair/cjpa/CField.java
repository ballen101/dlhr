package com.corsair.cjpa;

import java.util.Calendar;
import java.util.Date;

import com.corsair.cjpa.CJPABase.CJPAStat;
import com.corsair.cjpa.util.CFieldinfo;
import com.corsair.dbpool.util.Systemdate;

/**
 * 实体字段
 * 
 * @author Administrator
 *
 */
public class CField {
	private CJPABase owner;
	private CFieldinfo fdinfo;
	private String fieldname = "";// 数据库字段名
	private String cfieldname = "";// 实体字段名
	private int size = 0;
	private int fieldtype = 0;
	private String caption = "";
	private int dicid = 0;
	private String dicclass = "";
	private int codeid = 0;
	private int lookup = 0;
	private boolean nullable = true;
	private boolean iskey = false;
	private boolean autoinc = false;
	private boolean readonly = false;
	private boolean visible = true;
	private boolean Changed;
	private String editconst = null;// 不为空时 保存时候 将作为常量传给sql语句
	private String Value = ""; // 值 日期用 YYYY-MM-DD hh:mm:ss 表示 BLOB 表示为明文
	private int sorIndex = 0; // 字段排列序号
	private boolean ignoreSave = false;

	public CField(CJPABase owner) {
		this(owner, "");

	}

	public CField(CJPABase owner, String fieldname) {
		this.owner = owner;
		this.fieldname = fieldname;
	}

	public boolean isEmpty() {
		return (Value == null) || (Value.isEmpty());
	}

	/**
	 * 字段复制
	 * 
	 * @param cfiled
	 */
	public void assigned(CField cfiled) {
		this.setFieldname(cfiled.fieldname);
		this.setSize(cfiled.size);
		this.setFieldtype(cfiled.fieldtype);
		this.setCaption(cfiled.caption);
		this.setDicid(cfiled.dicid);
		this.setCodeid(cfiled.codeid);
		this.setLookup(cfiled.lookup);
		this.setIskey(cfiled.iskey);
		this.setReadonly(cfiled.readonly);
		this.setVisible(cfiled.visible);
		this.setValue(cfiled.Value);
		this.setNullable(cfiled.isNullable());
		this.setChanged(cfiled.Changed);
		this.setAutoinc(cfiled.isAutoinc());
		this.setDicclass(cfiled.getDicclass());
	}

	// /////////////////as///////////////////

	/**
	 * @return 返回日期，不包括时分秒
	 */
	public Date getAsDate() {
		if ((Value == null) || (Value.isEmpty()))
			return null;
		Date rst = Systemdate.getDateByStr(Value);
		Calendar cal = Calendar.getInstance();
		cal.setTime(rst);
		// 将时分秒,毫秒域清零
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}

	/**
	 * @return 返回日期，不包括时分秒
	 */
	public Date getAsDate(Date date) {
		Date rst = null;
		if ((Value == null) || (Value.isEmpty()))
			rst = date;
		else
			rst = Systemdate.getDateByStr(Value);
		Calendar cal = Calendar.getInstance();
		cal.setTime(rst);
		// 将时分秒,毫秒域清零
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}

	public Date getAsDatetime() {
		if ((Value == null) || (Value.isEmpty()))
			return null;
		return Systemdate.getDateByStr(Value);
	}

	public Date getAsDatetime(Date date) {
		try {
			Date rst = Systemdate.getDateByStr(Value);
			if (rst == null)
				return date;
			else
				return rst;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return date;
		}
	}

	public void setAsDatetime(Date date) {
		if (date == null)
			setValue(null);
		else
			setValue(Systemdate.getStrDate(date));
	}

	public float getAsFloatDefault(float value) {
		try {
			return Float.valueOf(Value);
		} catch (Exception e) {
			return value;
		}
	}

	public float getAsFloat(float value) {
		try {
			return Float.valueOf(Value);
		} catch (Exception e) {
			return value;
		}
	}

	public float getAsFloat() {
		return Float.valueOf(Value);
	}

	public void setAsFloat(float value) {
		setValue(String.valueOf(value));
	}

	public void setValue(float value) {
		setAsFloat(value);
	}

	public long getAsLongDefault(long value) {
		try {
			return Math.round((double) Float.valueOf(Value));// Long.valueOf(Value);
		} catch (Exception e) {
			return value;
		}
	}

	public long getAsLong(long value) {
		try {
			return Math.round((double) Float.valueOf(Value));// Long.valueOf(Value);
		} catch (Exception e) {
			return value;
		}
	}

	public long getAsLong() {
		return Math.round((double) Float.valueOf(Value));
	}

	public void setAsLong(long value) {
		setValue(String.valueOf(value));
	}

	public void setValue(long value) {
		setAsLong(value);
	}

	public int getAsIntDefault(int v) {
		try {
			return Math.round(Float.valueOf(Value));// Integer.valueOf(Value);
		} catch (Exception e) {
			return v;
		}
	}

	public int getAsInt() {
		return Math.round(Float.valueOf(Value));// Integer.valueOf(Value);
	}

	public int getAsInt(int v) {
		try {
			return Math.round(Float.valueOf(Value));// Integer.valueOf(Value);
		} catch (Exception e) {
			return v;
		}
	}

	public void setAsInt(int value) {
		setValue(String.valueOf(value));
	}

	public void setValue(int value) {
		setValue(String.valueOf(value));
	}

	public void setAsBoolean(boolean v) {
		if (v)
			setAsInt(1);
		else
			setAsInt(2);
	}

	public void setValue(boolean value) {
		setAsBoolean(value);
	}

	public boolean getAsBoolean() {
		return Boolean.getBoolean(Value);
	}

	// //////////////////////////////////////////////

	public String getFieldname() {
		return fieldname;
	}

	public void setFieldname(String fieldname) {
		this.fieldname = fieldname;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getFieldtype() {
		return fieldtype;
	}

	public void setFieldtype(int fieldtype) {
		this.fieldtype = fieldtype;
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public int getDicid() {
		return dicid;
	}

	public void setDicid(int dicid) {
		this.dicid = dicid;
	}

	public int getCodeid() {
		return codeid;
	}

	public void setCodeid(int codeid) {
		this.codeid = codeid;
	}

	public int getLookup() {
		return lookup;
	}

	public void setLookup(int lookup) {
		this.lookup = lookup;
	}

	public boolean isIskey() {
		return iskey;
	}

	public void setIskey(boolean iskey) {
		this.iskey = iskey;
	}

	public boolean isReadonly() {
		return readonly;
	}

	public void setReadonly(boolean readonly) {
		this.readonly = readonly;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public boolean isChanged() {
		return Changed;
	}

	public void setChanged(boolean changed) {
		if (changed) {
			if (owner.getJpaStat() == CJPAStat.RSLOAD4DB) {
				owner.setJpaStat(CJPAStat.RSUPDATED);
			}
		}
		Changed = changed;
	}

	public String getValue() {
		return Value;
	}

	public void setValue(String value) {
		if ((value != null) && !value.equals(Value))
			setChanged(true);
		if ((value == null) && (Value != null))
			setChanged(true);
		this.Value = value;
	}

	public int getSorIndex() {
		return sorIndex;
	}

	public void setSorIndex(int sorIndex) {
		this.sorIndex = sorIndex;
	}

	public boolean isNullable() {
		return nullable;
	}

	public void setNullable(boolean nullable) {
		this.nullable = nullable;
	}

	public CJPABase getOwner() {
		return owner;
	}

	public void setOwner(CJPABase owner) {
		this.owner = owner;
	}

	public boolean isAutoinc() {
		return autoinc;
	}

	public void setAutoinc(boolean autoinc) {
		this.autoinc = autoinc;
	}

	public String getCfieldname() {
		return cfieldname;
	}

	public void setCfieldname(String cfieldname) {
		this.cfieldname = cfieldname;
	}

	public String getDicclass() {
		return dicclass;
	}

	public void setDicclass(String dicclass) {
		this.dicclass = dicclass;
	}

	public boolean isIgnoreSave() {
		return ignoreSave;
	}

	public void setIgnoreSave(boolean ignoreSave) {
		this.ignoreSave = ignoreSave;
	}

	public CFieldinfo getFdinfo() {
		return fdinfo;
	}

	public void setFdinfo(CFieldinfo fdinfo) {
		this.fdinfo = fdinfo;
	}

	public String getEditconst() {
		return editconst;
	}

	public void setEditconst(String Value) {
		if ((editconst != null) && !editconst.equals(Value))
			setChanged(true);
		if ((editconst == null) && (Value != null))
			setChanged(true);
		this.editconst = Value;
	}

}
