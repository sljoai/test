package com.song.cn.basic.designpattern.responsibilitychain;

/**
 * 商品销售管理模块的业务逻辑
 */
public class GoodsSaleEbo {

    public boolean sale(String user, String customer, SaleModel saleModel) {
        //权限检查
        SaleSecurityCheck ssc = new SaleSecurityCheck();
        //通用数据检查
        SaleDataCheck sdc = new SaleDataCheck();
        //数据逻辑校验
        SaleLogicCheck slc = new SaleLogicCheck();
        //业务处理
        SaleMgr sd = new SaleMgr();
        ssc.setSuccessor(sdc);
        sdc.setSuccessor(slc);
        slc.setSuccessor(sd);

        //向链上的第一个对象发出处理的请求
        return ssc.sale(user, customer, saleModel);

    }

}
