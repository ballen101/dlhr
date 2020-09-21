//EAI 控制器
package com.corsair.server.eai;

import java.io.File;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.corsair.dbpool.CDBConnection;
import com.corsair.dbpool.DBPools;
import com.corsair.server.base.ConstsSw;
import com.corsair.server.eai.CEAIParamBase.EAITYPE;
import com.corsair.server.eai.CEAIParamBase.TRANASTYPE;

public class EAIController {
	private List<CEAIParam> eaips = new ArrayList<CEAIParam>();
	private List<CEAIThread> eaits = new ArrayList<CEAIThread>();

	public EAIController() {
		try {
			System.out.println("------------初始化 EAI-------------");
			initEaiPs();
			System.out.println("检测EAI");
			checkEaisType();
			initThreads();
			System.out.println("启动EAI线程");
			startAllthread();
			System.out.println("------------初始化 EAI-------------");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// //初始化所有XML文件
	private void initEaiPs() throws Exception {

		String dirname = ConstsSw.eaiXMLFilePath;
		File file = new File(dirname);
		File[] files = file.listFiles();
		for (int i = 0; i < files.length; i++) {
			if (!files[i].isDirectory()) {
				String fname = files[i].getName();
				if (getExtensionName(fname).equalsIgnoreCase("xml")) {
					System.out.println("载入EAI配置文件" + fname);
					fname = dirname + fname;
					CEAIParam cep = new CEAIParam(fname);
					CEAIParam cep2 = findEAIPByName(cep.getName());
					if (cep2 != null) {
						throw new Exception("命名为<" + cep.getName() + ">的EAI重名，请重新修改配置文件");
					}
					eaips.add(cep);
				}
			}
		}
	}

	// /////检查所有eai关联性
	// /////作为其它EAI的子EAI，其 eaitype 必须为 ParEAI:非轮询,由级联EAI触发
	// /////所有EAI不允许同名
	// /////所有子EAI只允许一个主EAI指向它
	private void checkEaisType() throws Exception {
		for (CEAIParam cep : eaips) {
			if (!cep.isEnable()) {
				System.out.println("EAI:" + cep.getName() + "已经禁用，跳过检查...");
				continue;
			}
			for (CChildEAIParm ccep : cep.getChildeais()) {
				CEAIParam ceaip = findEAIPByName(ccep.getCldEaiName());// 找到
																		// 从EAI
																		// 参数对象
				if ((ceaip == null) || (!ceaip.isEnable())) {
					throw new Exception("检查EAI失败<" + cep.getName() + ">的关联EAI<" + ccep.getCldEaiName() + ">不存在或已禁用!");
				}
				if (ceaip.getEaitype() != EAITYPE.ParEAI) {
					throw new Exception("检查EAI失败<" + cep.getName() + ">的关联EAI<" + ccep.getCldEaiName() + ">的eaitype必须为ParEAI!");
				}
				ccep.setCdeaiparam(ceaip);
				if (ceaip.getOwnerEaiParam() != null) {
					throw new Exception("检查EAI失败<" + cep.getName() + ">的关联EAI<" + ccep.getCldEaiName() + ">被多个主EAI指定!");
				}
				ceaip.setOwnerEaiParam(cep);
				if (ceaip.getTrans_type() != TRANASTYPE.none) {
					throw new Exception("检查EAI失败,主EAI<" + ccep.getCldEaiName() + ">使用事务处理的时候，从EAI<" + ccep.getCldEaiName() + ">事务处理只允许为none类型!");
				}

				// 用 ceaip 的原表字段 初始化 ChildEAI 的 cfieldtype;
				System.out.println("初始化ChileEAI 关联字段数据!");
				initChileEAICfieldType(ceaip, ccep);
				// //检查所有字段 如果发现类型为 -999 的 ，说明字段不存
			}
		}
	}

	private EAIMapField findLinkField(CChildEAIParm mainChildEAI, String cfieldname) {
		for (EAIMapField lf : mainChildEAI.getLinkfields()) {
			if (lf.getD_field().equalsIgnoreCase(cfieldname)) {
				return lf;
			}
		}
		return null;
	}

	private void initChileEAICfieldType(CEAIParam ceaip, CChildEAIParm mainChildEAI) throws Exception {
		CDBConnection con = DBPools.poolByName(ceaip.getDbpool_target()).getCon(this);
		if (con == null)
			throw new Exception("获取数据库连接错误:" + ceaip.getDbpool_target());
		try {
			String sqlstr = "select * from " + ceaip.getS_tablename() + " where 0=1";
			Statement stmt = con.con.createStatement();
			// System.out.println(sqlstr);
			ResultSet rs = stmt.executeQuery(sqlstr);
			ResultSetMetaData rsmd = rs.getMetaData(); // 得到结果集的定义结构
			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				String fdname = rsmd.getColumnLabel(i);
				EAIMapField lf = findLinkField(mainChildEAI, fdname);
				if (lf != null) {
					lf.setD_fieldtype(rsmd.getColumnType(i));
				}
			}
		} finally {
			con.close();
		}
	}

	private CEAIParam findEAIPByName(String name) {
		for (CEAIParam eaip : eaips) {
			if (eaip.getName().equalsIgnoreCase(name))
				return eaip;
		}
		return null;
	}

	private void initThreads() throws Exception {
		for (CEAIParam eaip : eaips) {
			if ((eaip.isEnable()) && (eaip.getEaitype() != EAITYPE.ParEAI)) {
				System.out.println("创建EAI线程:" + eaip.getName());
				CEAIThread eait = new CEAIThread(eaip);
				eaits.add(eait);
			}
		}
	}

	private void startAllthread() {
		for (CEAIThread eait : eaits) {
			eait.start();
		}
	}

	public String getExtensionName(String filename) {
		if ((filename != null) && (filename.length() > 0)) {
			int dot = filename.lastIndexOf('.');
			if ((dot > -1) && (dot < (filename.length() - 1))) {
				return filename.substring(dot + 1);
			}
		}
		return filename;
	}

	public List<CEAIThread> getEaits() {
		return eaits;
	}

	public void setEaits(List<CEAIThread> eaits) {
		this.eaits = eaits;
	}

	public List<CEAIParam> getEaips() {
		return eaips;
	}

	public void setEaips(List<CEAIParam> eaips) {
		this.eaips = eaips;
	}

}
