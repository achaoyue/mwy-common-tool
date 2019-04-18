package com.mwy.tool;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ClassInfo {
    private String className;
    private List<MethodInfo> methodInfos;
}