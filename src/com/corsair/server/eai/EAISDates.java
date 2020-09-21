package com.corsair.server.eai;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class EAISDates {
	public class EAISDate {
		String EAIName;
		Date lastDate;
	}

	public List<EAISDate> Dates = Collections.synchronizedList(new ArrayList<EAISDate>());

	public void load4File(String fname) {

	}

	public EAISDate getEAISdateByName(String eainame) {
		for (EAISDate ed : Dates) {
			if (ed.EAIName.equalsIgnoreCase(eainame)) {
				return ed;
			}
		}
		return null;
	}

	public void saveEaiDate(String eainame, Date lastDate) {
		EAISDate ed = getEAISdateByName(eainame);
		if (ed != null) {
			ed.lastDate = lastDate;
		}
		// ////save to file
	}

	public Date getEaiDate(String eainame) {
		EAISDate ed = getEAISdateByName(eainame);
		if (ed != null) {
			return ed.lastDate;
		}
		return null;
	}

}
