package com.song.cn.basic.serialization;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class TestGson {

    public static void main(String[] args) {
        Gson gson = new GsonBuilder().create();
        String json = "{\"price\":10.0}";
        Foo foo = gson.fromJson(json, Foo.class);
        System.out.println(foo);
        foo.setPrice(Double.NaN);
        try {
            json = gson.toJson(foo);
        } catch (Exception e) {
            System.out.println(foo.toString() + ":" + e.toString());
        }
        System.out.println(json);
    }
}

class Foo {
    private double price;

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Foo{" +
                "price=" + price +
                '}';
    }
}
