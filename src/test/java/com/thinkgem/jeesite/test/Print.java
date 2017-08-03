package com.thinkgem.jeesite.test;

import com.inossem.print.Order;

/**
 * ****************************
 * CreateTime Dec 30, 2013 11:54:15 AM
 * Author MaBingYang
 * FileName Print.java
 * FilePath com.common
 * Explain 热敏打印机
 * ******************************
 */
public class Print {

	public static void main(String[] args) {
		com.inossem.print.Print.getPrintList();
//		for (int i = 0; i < 2; i++) {
//			
//			com.inossem.print.Print.execute("000107020080001655,,,150500001A","2311040086875394", "23000039130", "01",
//					"包装材料、标识-包装材料-胶纸","中国石油化工股份有限公司物资装备部中国石油化工股份有限公司物资装备部","","", "ZDesigner GK888t",0);
//		
//			
//		}
		Order o = new Order();
		o.setBatchCode("10004");
		o.setPurNo("test");
		o.setMachineName("ZDesigner GK888t");
		com.inossem.print.Print.execute(o);
	}
}
