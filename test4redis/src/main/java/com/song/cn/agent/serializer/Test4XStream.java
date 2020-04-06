package com.song.cn.agent.serializer;

import com.thoughtworks.xstream.XStream;

import java.util.ArrayList;
import java.util.List;

public class Test4XStream {

    public List<String> serializeXMLProductsList(List<Products> pList) {
        if (pList == null) {
            System.out.println("【XmlSerializeServiceImpl-serializeProductsListService】pList参数为空");
            return null;
        }
        long start = System.currentTimeMillis();
        XStream x = new XStream();
        x.alias("Products", Products.class);
        List<String> strList = new ArrayList<String>();
        for (Products p : pList) {
            String str = x.toXML(p);
            strList.add(str);
        }
        long end = System.currentTimeMillis();
        long usedTime = end - start;
        return strList;
    }

    public List<Products> deserializeXMLDataListToProductsList(
            List<String> xmlStrList) {
        long start = System.currentTimeMillis();
        List<Products> productsList = new ArrayList<Products>();
        XStream xs = new XStream();
        xs.alias("Products", Products.class);
        for (String xmlStr : xmlStrList) {
            Products p = (Products) xs.fromXML(xmlStr);
            productsList.add(p);
        }
        long end = System.currentTimeMillis();
        long usedTime = end - start;
        return productsList;
    }
}
