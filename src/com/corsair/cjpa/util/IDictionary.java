package com.corsair.cjpa.util;

import java.util.HashMap;
import java.util.List;

/**词汇表
 * @author Administrator
 *
 */
public interface IDictionary {
	public abstract String getValueFeild();

	public abstract String getCaptionFeild();

	public abstract List<HashMap<String, String>> getDatas();
}
