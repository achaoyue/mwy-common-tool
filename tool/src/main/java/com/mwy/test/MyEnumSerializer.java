package com.mwy.test;

import com.alibaba.fastjson.serializer.EnumSerializer;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;

import java.io.IOException;
import java.lang.reflect.Type;

public class MyEnumSerializer extends EnumSerializer {
    @Override
    public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features) throws IOException {
        if(object instanceof Enum && object instanceof CodeEnum){
            SerializeWriter out = serializer.out;
            out.write(((CodeEnum) object).getDesc());
        }else{
            super.write(serializer, object, fieldName, fieldType, features);
        }
    }
}
