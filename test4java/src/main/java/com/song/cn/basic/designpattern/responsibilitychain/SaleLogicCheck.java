package com.song.cn.basic.designpattern.responsibilitychain;

public class SaleLogicCheck extends SaleHandler {
    @Override
    public boolean sale(String user, String customer, SaleModel saleModel) {
        //进行数据的逻辑检查，如：检查ID的唯一性、主外键的对应关系等等
        return this.successor.sale(user, customer, saleModel);
    }
}
