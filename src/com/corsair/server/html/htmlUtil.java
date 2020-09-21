package com.corsair.server.html;

import org.mozilla.javascript.Scriptable;

public class htmlUtil {
	public static int parIntParm(Scriptable scope, String varname, int deftvalue) {
		Object fobj = scope.get(varname, scope);
		if (fobj == null)
			return deftvalue;
		try {
			return Float.valueOf(fobj.toString()).intValue();
		} catch (Exception e) {
			return deftvalue;
		}
	}

	public static boolean parBoolParm(Scriptable scope, String varname, boolean deftvalue) {
		Object fobj = scope.get(varname, scope);
		if (fobj == null)
			return deftvalue;
		try {
			return Boolean.valueOf(fobj.toString());
		} catch (Exception e) {
			return deftvalue;
		}
	}
}
