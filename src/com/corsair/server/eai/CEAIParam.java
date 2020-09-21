package com.corsair.server.eai;

public class CEAIParam extends CEAIParamDBInfo {

	public CEAIParam(String xmlfname) throws Exception {
		super(xmlfname);
		// TODO Auto-generated constructor stub
	}

	public CChildEAIParm getChdEaiByParam(CEAIParam eaip) {
		for (CChildEAIParm cdeai : getChildeais()) {
			if (cdeai.getCdeaiparam().equals(eaip)) {
				return cdeai;
			}
		}
		return null;
	}

}
