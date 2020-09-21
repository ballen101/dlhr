package com.corsair.server.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class ListHashMap extends ArrayList<HashMap<String, String>> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 306899390643954782L;

	public void Sort(final String key, final String sort) {
		Collections.sort(this, new Comparator<HashMap<String, String>>() {

			public int compare(HashMap<String, String> a, HashMap<String, String> b) {
				int ret = 0;
				try {
					String av = a.get(key);
					String bv = b.get(key);
					if (sort != null && "desc".equals(sort)) {
						ret = bv.compareTo(av);
					} else {
						ret = av.compareTo(bv);
					}
				} catch (Exception ne) {
					System.out.println(ne);
				}
				return ret;
			}
		});
	}

}
