package com.song.cn.basic.designpattern.responsibilitychain;

public class SaleSecurityCheck extends SaleHandler {
    @Override
    public boolean sale(String user, String customer, SaleModel saleModel) {
        //进行权限检查
        if ("小李".equals(user)) {
            return this.successor.sale(user, customer, saleModel);
        } else {
            System.out.println("对不起" + user + ", 你没有保存销售信息的权限！");
            return false;
        }
    }
}
