package com.song.cn.basic.designpattern.responsibilitychain;

/**
 * 数据通用性检查的职责对象
 */
public class SaleDataCheck extends SaleHandler {
    @Override
    public boolean sale(String user, String customer, SaleModel saleModel) {
        if (user == null || user.trim().length() == 0) {
            System.out.println("申请人不能为空！");
            return false;
        }

        if (customer == null || customer.trim().length() == 0) {
            System.out.println("客户不能为空！");
            return false;
        }

        if (saleModel == null) {
            System.out.println("销售商品的数据不能为空！");
            return false;
        }

        if (saleModel.getGoods() == null || saleModel.getGoods().trim().length() == 0) {
            System.out.println("销售的商品不能为空！");
            return false;
        }

        if (saleModel.getSaleNum() == null || saleModel.getSaleNum() == 0) {
            System.out.println("销售的商品的数量不能为0！");
            return false;
        }

        return this.successor.sale(user, customer, saleModel);
    }
}
