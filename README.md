#print 接口定义如下：
url:/go
method: POST

Header:
Content-Type: application/json;charset=UTF-8

body:
{
    "batchCode": "批次号",
    "machineName": "ZDesigner GK888t_ol",
    "purNo": "采购单号",
    "supplierDesc": "供应商描述",
    "contractNo": "合同号",
    "reqDept": "需求部门",
    "materielCode": "物料编码",
    "materielDesc": "物料描述",
    "inStorageDate": "2017-03-03"
}


return:
{
   "code": "1", //1:成功，0:失败
   "msg": null,
   "data":    {
      "batchCode": "test",
      "purNo": "purNo",
      "supplierDesc": "供应商描述",
      "contractNo": "test-test12",
      "reqDept": "需求部门",
      "materielCode": "xxxxxxx",
      "materielDesc": "test",
      "inStorageDate": "2017-03-03",
      "machineName": "ZDesigner GK888t_ol"
   }
}