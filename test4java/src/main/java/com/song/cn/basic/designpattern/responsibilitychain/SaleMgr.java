package com.song.cn.basic.designpattern.responsibilitychain;

public class SaleMgr extends SaleHandler {
    @Override
    public boolean sale(String user, String customer, SaleModel saleModel) {
        System.out.println("功能链完成！");
        return true;
    }
}
