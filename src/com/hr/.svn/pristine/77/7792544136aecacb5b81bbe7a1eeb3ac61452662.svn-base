package com.hr.base.ctr;

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		System.out.println(getpidstr("1,2,3,4", 8));
	}

	private static String getpidstr(String ids, int len) {
		int pl = len;
		String[] ps = ids.split(",");
		int l = ps.length;
		String rst = "";
		for (int i = 0; i < pl; i++) {
			int idx = l - i - 1;
			if (idx < 0)
				break;
			String ts = ps[idx];
			rst = rst + ts + ",";
		}
		if (!rst.isEmpty())
			rst = rst.substring(0, rst.length() - 1);
		return rst;
	}

}
