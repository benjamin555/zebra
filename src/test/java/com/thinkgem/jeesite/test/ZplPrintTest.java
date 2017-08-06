package com.thinkgem.jeesite.test;

import org.junit.Test;

import com.inossem.print.Order;
import com.inossem.print.ZplPrint;

public class ZplPrintTest {
	
	@Test
	public void testExec() throws Exception {
		Order o = new Order();
		o.setBatchCode("test");
		o.setContractNo("test-test12");
		o.setInStorageDate("2017-02-02");
		o.setMachineName("ZDesigner GK888t_ol");
		o.setMaterielCode("test123");
		o.setMaterielDesc("物料描述1231231撕掉拉时间到了卡审多久");
		o.setPurNo("asfa-asdfads-12312");
		o.setReqDept("提昂看接口填写");
		o.setSupplierDesc("asdfasdfa是打发点算法");
		ZplPrint.execute(o );
	}

}
