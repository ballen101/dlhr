package com.corsair.server.eai;

import java.io.File;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class CEAIParamXMLFile extends CEAIParamBase {

	public CEAIParamXMLFile(String xmlfname) throws Exception {
		try {
			loadFromXMLFile(xmlfname);
		} catch (Exception e1) {
			throw new Exception("初始化EAI参数文件<" + xmlfname + ">错误:" + e1.getMessage());
		}
	}

	private void loadFromXMLFile(String xmlfname) throws Exception {
		SAXReader saxReader = new SAXReader();
		Document document = null;
		document = saxReader.read(new File(xmlfname));
		Element root = document.getRootElement();

		Element name = root.element("name");
		if (name == null)
			throw new Exception("EAI配置文件缺少name");
		this.setName(root.element("name").getText());

		Element eaiclass = root.element("eaiclass");
		if (eaiclass == null)
			throw new Exception("EAI配置文件缺少eaiclass");
		this.setEaicalss(root.element("eaiclass").getText());

		Element enable = root.element("enable");
		if (enable == null)
			throw new Exception("EAI配置文件缺少enable");
		this.setEnable(Boolean.valueOf(root.element("enable").getText()));

		Element trans_type = root.element("trans_type");
		if (trans_type == null)
			throw new Exception("EAI配置文件缺少trans_type");
		String tratstr = trans_type.getText();
		if (tratstr.equalsIgnoreCase("row"))
			this.setTrans_type(TRANASTYPE.row);
		else if (tratstr.equalsIgnoreCase("disp"))
			this.setTrans_type(TRANASTYPE.disp);
		else if (tratstr.equalsIgnoreCase("none"))
			this.setTrans_type(TRANASTYPE.none);
		else
			throw new Exception("EAI配置文件trans_type" + tratstr + "未定义");

		Element eaitype = root.element("eaitype");
		if (eaitype == null)
			throw new Exception("EAI配置文件缺少eaitype");
		String eaitypestr = eaitype.getText();
		if (eaitypestr.equalsIgnoreCase("NoticePoll"))
			this.setEaitype(EAITYPE.NoticePoll);
		else if (eaitypestr.equalsIgnoreCase("Poll"))
			this.setEaitype(EAITYPE.Poll);
		else if (eaitypestr.equalsIgnoreCase("ParEAI"))
			this.setEaitype(EAITYPE.ParEAI);
		else
			throw new Exception("EAI配置文件eaitype" + eaitypestr + "未定义");

		Element frequency = root.element("frequency");
		if (frequency == null)
			throw new Exception("EAI配置文件缺少frequency");
		this.setFrequency(Integer.valueOf(frequency.getText()));

		Element dbpool_source = root.element("source");
		if (dbpool_source == null)
			throw new Exception("EAI配置文件缺少source");
		this.setDbpool_source(dbpool_source.getText());

		Element dbpool_target = root.element("target");
		if (dbpool_target == null)
			throw new Exception("EAI配置文件缺少target");
		this.setDbpool_target(dbpool_target.getText());

		Element s_tablename = root.element("s_tablename");
		if (s_tablename == null)
			throw new Exception("EAI配置文件缺少s_tablename");
		this.setS_tablename(s_tablename.getText());

		Element t_tablename = root.element("t_tablename");
		if (t_tablename == null)
			throw new Exception("EAI配置文件缺少t_tablename");
		this.setT_tablename(t_tablename.getText());

		Element lastupdatefield = root.element("lastupdatefield");
		if (lastupdatefield != null)
			setLastupdatefield(lastupdatefield.getText());
		else
			setLastupdatefield(null);

		Element allow_insert = root.element("allow_insert");
		if (allow_insert == null)
			throw new Exception("EAI配置文件缺少allow_insert");
		this.setAllow_insert(Boolean.valueOf(allow_insert.getText()));

		Element childeais = root.element("childeais");
		if (childeais != null) {
			initChildEais(childeais);
		}

		Element eaimap = root.element("eaimap");
		if (eaimap == null)
			throw new Exception("EAI配置文件缺少eaimap");
		Element keyfields = eaimap.element("keyfields");
		if (keyfields == null)
			throw new Exception("EAI配置文件缺少keyfields");

		Element mapfields = eaimap.element("fields");
		if (mapfields == null)
			throw new Exception("EAI配置文件缺少fields");

		Element condts = root.element("condts");
		if (condts == null)
			throw new Exception("EAI配置文件缺少condts");

		initkeyfields(keyfields);
		initmapfields(mapfields);
		initcondts(condts);
	}

	private void initkeyfields(Element keyfields) throws Exception {
		@SuppressWarnings("unchecked")
		List<Element> kfds = keyfields.elements("keyfield");
		if ((kfds == null) || (kfds.isEmpty())) {
			throw new Exception("EAI配置文件至少需要一项keyfield");
		}
		for (Element kfd : kfds) {
			EAIMapField ekf = new EAIMapField();
			ekf.setS_field(kfd.element("s_keyfield").getText());
			ekf.setD_field(kfd.element("d_keyfield").getText());
			if ((ekf.getS_field() == null) || (ekf.getS_field().isEmpty())) {
				throw new Exception("EAI配置文件缺少s_keyfield为空!");
			}
			if ((ekf.getD_field() == null) || (ekf.getD_field().isEmpty())) {
				throw new Exception("EAI配置文件缺少d_keyfield为空!");
			}
			this.getKeyfieds().add(ekf);
		}
	}

	private void initmapfields(Element mapfields) throws Exception {
		@SuppressWarnings("unchecked")
		List<Element> mfds = mapfields.elements("field");
		if ((mfds == null) || (mfds.isEmpty())) {
			throw new Exception("EAI配置文件至少需要一项mapfield");
		}
		for (Element mfd : mfds) {
			EAIMapField emf = new EAIMapField();
			emf.setS_field(mfd.element("s_fieldname").getText());
			emf.setD_field(mfd.element("d_fieldname").getText());
			if ((emf.getS_field() == null) || (emf.getS_field().isEmpty())) {
				throw new Exception("EAI配置文件缺少s_fieldname为空!");
			}
			if ((emf.getD_field() == null) || (emf.getD_field().isEmpty())) {
				throw new Exception("EAI配置文件缺少d_fieldname为空!");
			}
			this.getMapfields().add(emf);
		}
		// System.out.println("init eaimat:" + getMapfields().size());
	}

	@SuppressWarnings("unchecked")
	private void initcondts(Element condts) throws Exception {
		List<Element> econdts = condts.elements("condt");
		for (Element econdt : econdts) {
			CEAICondt cdt = new CEAICondt();
			cdt.setField(econdt.element("field").getText());
			cdt.setOper(econdt.element("oper").getText());
			cdt.setValue(econdt.element("value").getText());
			getCondts().add(cdt);
		}
	}

	@SuppressWarnings("unchecked")
	private void initChildEais(Element childeais) throws Exception {
		List<Element> eceais = childeais.elements("childeai");
		for (Element eceai : eceais) {
			CChildEAIParm ceai = new CChildEAIParm();
			ceai.setCldEaiName(eceai.element("name").getText());
			if ((ceai.getCldEaiName() == null) || (ceai.getCldEaiName().isEmpty())) {
				throw new Exception("EAI配置文件childeai 没有指定 名称");
			}
			List<Element> elinkfields = eceai.element("linkfields").elements("linkfield");
			if (elinkfields.size() <= 0) {
				throw new Exception("EAI配置文件childeai至少需要一个linkfield项目");
			}
			// System.out.println("fdsa");
			for (Element elinkfield : elinkfields) {
				EAIMapField mf = new EAIMapField();
				mf.setS_field(elinkfield.element("fieldname").getText());
				mf.setD_field(elinkfield.element("cfieldname").getText());
				if ((mf.getS_field() == null) || mf.getS_field().isEmpty()) {
					throw new Exception("EAI配置文件childeai中cfieldname不能为空!");
				}
				if ((mf.getD_field() == null) || mf.getD_field().isEmpty()) {
					throw new Exception("EAI配置文件childeai中fieldname不能为空!");
				}
				ceai.getLinkfields().add(mf);
			}
			getChildeais().add(ceai);
		}
		// System.out.println(this.getName() + "  " + getChildeais().size());
	}
}
