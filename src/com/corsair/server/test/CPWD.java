package com.corsair.server.test;

import java.util.Scanner;

import com.corsair.server.base.ConstsSw;
import com.corsair.server.base.Login;
import com.corsair.server.util.DesSw;

public class CPWD {
	public static void main(String[] args) throws Exception {
		System.out.println("输入一个：");
		Scanner strc = new Scanner(System.in);
		String str = strc.next();
		System.out.println(DesSw.DESryStrHex(str, ConstsSw._userkey));
	}
}
