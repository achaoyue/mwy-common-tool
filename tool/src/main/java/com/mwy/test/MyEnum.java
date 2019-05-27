package com.mwy.test;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author mouwenyao
 * @since 2019-05-24
 */
@Getter
@AllArgsConstructor
public enum  MyEnum implements CodeEnum {
    A(1,"aa"),
    B(1,"bb"),
    ;
    int code;
    String desc;
}
