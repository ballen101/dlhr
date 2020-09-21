package com.corsair.server.util;

/*
 * 数据字典缓存
 * 用时加载 用完取消
 * */
import java.util.HashMap;
import java.util.List;

import com.corsair.cjpa.util.IDictionary;
import com.corsair.dbpool.DBPools;

public class DictionaryTemp {
	private HashMap<String, DictionaryTempItem> dicts = new HashMap<String, DictionaryTempItem>();

	/**
	 * 获取词汇表
	 * 
	 * @param dicidx
	 * @return
	 */
	public DictionaryTempItem getDictionary(String dicidx) {
		return dicts.get(dicidx);
	}

	/**
	 * @param clsname
	 * @param caption
	 * @return 根据名称获取词汇值
	 * @throws Exception
	 */
	public String getValueByCationCls(String clsname, String caption) throws Exception {
		String dicidx = clsname;
		DictionaryTempItem dic = getDictionary(dicidx);
		if (dic == null)
			dic = addDictionaryCls(dicidx, clsname);
		return dic.getValueByCation(caption);
	}

	/**
	 * @param clsname
	 *            词汇表ID
	 * @param caption
	 *            词汇标题
	 * @param defaultvalue
	 *            默认值 没找到返回默认
	 * @return 词汇ID
	 * @throws Exception
	 */
	public String getValueByCationCls(String clsname, String caption, String defaultvalue) throws Exception {
		String dicidx = clsname;
		DictionaryTempItem dic = getDictionary(dicidx);
		if (dic == null)
			dic = addDictionaryCls(dicidx, clsname);
		String rst = dic.getValueByCation(caption);
		if ((rst == null) || (rst.isEmpty()))
			return defaultvalue;
		else
			return rst;
	}

	/**
	 * @param clsname
	 *            词汇表ID
	 * @param caption
	 *            词汇标题
	 * @return 词汇ID 没找到返回空
	 * @throws Exception
	 */
	public String getCaptionByValueCls(String clsname, String value) throws Exception {
		String dicidx = clsname;
		DictionaryTempItem dic = getDictionary(dicidx);
		if (dic == null)
			dic = addDictionaryCls(dicidx, clsname);
		return dic.getCaptionByValue(value);
	}

	/**
	 * @param dicid词汇组ID
	 * @param caption词汇标题
	 *            为null 根据nullable 参数决定是否报错
	 * @param nullable
	 *            允许空
	 * @param errMsg为空报错信息
	 * @return 词汇ID;
	 * @throws Exception
	 */
	public String getVbCE(String dicid, String caption, boolean nullable, String errMsg) throws Exception {
		if ((caption == null) || (caption.isEmpty()))
			if (nullable)
				return null;
			else
				throw new Exception(errMsg);

		String rst = getValueByCation(dicid, caption);
		if (rst == null) {
			if (nullable)
				return null;
			else
				throw new Exception(errMsg);
		} else
			return rst;
	}

	/**
	 * @param dicid词汇组ID
	 * @param caption词汇标题
	 * @param defaultvalue默认值
	 * @return词汇ID，NULL 返回默认值
	 * @throws Exception
	 */
	public String getValueByCation(String dicid, String caption, String defaultvalue) throws Exception {
		String rst = getValueByCation(dicid, caption);
		return (rst == null) ? defaultvalue : rst;
	}

	public String getValueByCation(String dicid, String caption) throws Exception {
		if ((caption == null) || (caption.isEmpty()))
			return null;
		String dicidx = "dic" + dicid;
		DictionaryTempItem dic = getDictionary(dicidx);
		if (dic == null)
			dic = addDictionary(dicidx, String.valueOf(dicid));
		return dic.getValueByCation(caption);
	}

	public String getCaptionByValue(String dicid, String value) throws Exception {
		String dicidx = "dic" + dicid;
		DictionaryTempItem dic = getDictionary(dicidx);
		if (dic == null)
			dic = addDictionary(dicidx, String.valueOf(dicid));
		return dic.getCaptionByValue(value);
	}

	public DictionaryTempItem addDictionary(String dicidx, String dicid) throws Exception {
		String sqlstr = "select * from shwdict where pid=" + dicid + " and usable=1 ORDER BY dictvalue+0";
		List<HashMap<String, String>> dicvs = DBPools.defaultPool().openSql2List(sqlstr);
		DictionaryTempItem rst = new DictionaryTempItem("dictvalue", "language1", dicvs);
		dicts.put(dicidx, rst);
		return rst;
	}

	public DictionaryTempItem addDictionaryCls(String clsname, String dicid) throws Exception {
		IDictionary dic = (IDictionary) Class.forName(clsname).newInstance();
		DictionaryTempItem rst = new DictionaryTempItem(dic.getValueFeild(), dic.getCaptionFeild(), dic.getDatas());
		dicts.put(clsname, rst);
		return rst;
	}

}
