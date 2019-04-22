package com.mwy.tool;

import java.util.HashMap;
import java.util.Map;

public class ClassContext {
    private Map<String,ClassInfo> classInfoMap = new HashMap<>();

    private ClassContext() {
    }

    private static ClassContext classContext = new ClassContext();

    public static ClassContext getInstance() {
        return classContext;
    }

    protected ClassInfo get(String  className){
        return classInfoMap.get(className);
    }

    protected void put(String className,ClassInfo classInfo){
        classInfoMap.put(className,classInfo);
    }

    protected boolean containsKey(String key){
        return  classInfoMap.containsKey(key);
    }
}
