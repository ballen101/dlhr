package com.corsair.server.eai;

import java.lang.reflect.Constructor;

import com.corsair.dbpool.util.Logsw;
import com.corsair.server.eai.CEAIParamBase.EAITYPE;

public class CEAIThread extends Thread {
	private CEAI eai;
	private CEAIParam cparam;
	private boolean initedsuccess = false; // 是否成功初始化

	public CEAIThread(CEAIParam cparam) throws Exception {
		try {
			this.cparam = cparam;
			String classname = cparam.getEaicalss();
			//System.out.println(cparam);
			if ((classname == null) || (classname.isEmpty()) || (classname.equalsIgnoreCase("com.corsair.server.eai.CEAI"))) {
				eai = new CEAI(cparam);
			} else {
				eai = newEaiObjcet(classname);
			}
			initedsuccess = true;
			// this.start();////////////是否需要等到全部EAI初始化完成后才能启动?????????
		} catch (Exception e) {
			initedsuccess = false;
			throw new Exception("创建线程错误:" + e.getMessage());
		}
	}

	public CEAI newEaiObjcet(String className) throws Exception {
		Class<?> CEAIcd = Class.forName(className);
		if (!CEAI.class.isAssignableFrom(CEAIcd)) {
			throw new Exception(className + "必须从 com.corsair.server.eai.CEAI继承");
		}
		Class<?> paramTypes[] = { CEAIParam.class };
		Constructor<?> cst = CEAIcd.getConstructor(paramTypes);
		Object o = cst.newInstance(cparam);
		return (CEAI) (o);
	}

	@Override
	public void run() {
		while (true) {
			try {
				if (cparam.isEnable() && (cparam.getEaitype() != EAITYPE.ParEAI)) {
					eai.run();
					Logsw.debug("执行EAI:" + cparam.getName());
				}
			} catch (Exception e1) {
				try {
					Logsw.error("执行EAI错误:" + cparam.getName(),e1);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			try {
				sleep(cparam.getFrequency() * 1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public CEAI getEai() {
		return eai;
	}

	public void setEai(CEAI eai) {
		this.eai = eai;
	}

	public boolean isInitedsuccess() {
		return initedsuccess;
	}

	public void setInitedsuccess(boolean initedsuccess) {
		this.initedsuccess = initedsuccess;
	}

}
