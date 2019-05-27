package com.mwy.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeConfig;

/**
 * @author mouwenyao
 * @since 2019-05-24
 */
public class TestJSON {
    public static void main(String[] args) {
        SerializeConfig.globalInstance.put(MyEnum.class,new MyEnumSerializer());

        String s = JSON.toJSONString(MyEnum.A);
        System.out.println(s);

        Order order = new Order();
        order.setStatue(MyEnum.B);
        order.setVin("aaaaa");

        String s1 = JSON.toJSONString(order);
        System.out.println(s1);
    }
}
