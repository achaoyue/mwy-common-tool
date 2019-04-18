package com.mwy.tool;



import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MethodInfo {
    private String type;
    private String className;
    private String methodName;
    private String arguments;
    private String returnType;

    List<MethodInfo> nextCalls = new ArrayList<>();

    public String getSimpleName(){
        String[] split = className.split("\\.");
        return split[split.length-1];
    }
    public String getReturnSimpleName(){
        String[] split = returnType.split("\\.");
        return split[split.length-1];
    }
}