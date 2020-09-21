package com.corsair.server.eai;

import java.util.ArrayList;
import java.util.List;

/**
 * 取消
 * 
 * @author Administrator
 *
 */
public class CChildEAIParm {
	private String cldEaiName;
	private List<EAIMapField> linkfields = new ArrayList<EAIMapField>();
	private CEAIParam cdeaiparam;

	public String getCldEaiName() {
		return cldEaiName;
	}

	public void setCldEaiName(String cldEaiName) {
		this.cldEaiName = cldEaiName;
	}

	public List<EAIMapField> getLinkfields() {
		return linkfields;
	}

	public void setLinkfields(List<EAIMapField> linkfields) {
		this.linkfields = linkfields;
	}

	public CEAIParam getCdeaiparam() {
		return cdeaiparam;
	}

	public void setCdeaiparam(CEAIParam cdeaiparam) {
		this.cdeaiparam = cdeaiparam;
	}
}
