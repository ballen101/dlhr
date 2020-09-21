package com.corsair.server.cjpa;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import com.corsair.cjpa.CJPABase;
import com.corsair.cjpa.JPAControllerBase;
import com.corsair.dbpool.CDBConnection;
import com.corsair.server.base.CSContext;
import com.corsair.server.base.ConstsSw;
import com.corsair.server.generic.Shworg;
import com.corsair.server.util.GetNewSEQID;
import com.corsair.server.util.GetNewSystemCode;

/**
 * 实现接口方法的JPA
 * 
 * @author Administrator
 *
 */
public abstract class CJPA extends CJPAWorkFlow2 {

	public CJPA() throws Exception {
		super();
	}

	public CJPA(String sqlstr) throws Exception {
		super(sqlstr);
	}

	@Override
	protected String getLoginedUserName() {
		return CSContext.getUserNameEx();
	}

	@Override
	protected String getLoginedEntid() {
		try {
			return CSContext.getCurEntID();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	@Override
	protected CJPABase getLoginedUserDefaultOrg() {
		Shworg org = CSContext.getUserDefaultOrg();
		return ((CJPABase) org);
	}

	@Override
	protected String getTableNewID(CDBConnection con, String seqname, int num) throws Exception {
		return GetNewSEQID.dogetnewid1(seqname, num);
	}

	/*
	 * 提供控制器
	 */
	@Override
	public JPAControllerBase getPublicControllerBase() throws Exception {
		if (ConstsSw.publicJPAController == null) {
			// 创建ctrl
			Object o = ConstsSw.getAppParm("PublicJPAEventListener");
			if ((o == null) || (o.toString().isEmpty()))
				return null;
			else {
				String className = o.toString().trim();
				Class<?> CJPAcd = Class.forName(className);
				if (!JPAControllerBase.class.isAssignableFrom(CJPAcd)) {
					throw new Exception(className + "必须从 com.corsair.cjpa.JPAControllerBase继承");
				}
				Class<?> paramTypes[] = {};
				Constructor<?> cst = CJPAcd.getConstructor(paramTypes);
				ConstsSw.publicJPAController = (JPAControllerBase) cst.newInstance();
			}
		}
		return ConstsSw.publicJPAController;
	}

	/*
	 * 提供新编码
	 */
	@Override
	protected String getNewCode(int codeid) {
		GetNewSystemCode sc = new GetNewSystemCode();
		try {
			return sc.dogetnewsyscode(this, String.valueOf(codeid));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
}
