package com.mwy.tool;

import com.alibaba.fastjson.JSON;
import lombok.*;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.Function;

/**
 * author mouwenyao
 * date 2019-04-27 16:58
 */
public class ProjectProgramer {


    public static String getProgramer(String bathPath, Function<String, Boolean> fun){
        List<String> classNames = DirUtil.getFileList(bathPath, ".*/src/main/java/.*\\.java", e->{
            String[] split = e.split("/src/main/java/");
            if(split!= null && split.length > 1){
                return  split[1].replace(".java","").replace("/",".");
            }else {
                return "";
            }
        });
        ClassContext classContext = ClassContext.getInstance();
        for (String className : classNames) {
            try {
                if(classContext.containsKey(className)){
                    continue;
                }
                ClassInfoUtil.getMethodChain(className, fun);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        List<Node> nodes = new ArrayList<>();
        List<Edge> edges = new ArrayList<>();
        for (String className : classContext.keySet()) {
            nodes.add(new Node(className));
            ClassInfo classInfo = classContext.get(className);
            List<MethodInfo> methodInfos = classInfo.getMethodInfos();
            if(!CollectionUtils.isEmpty(methodInfos)){
                for (MethodInfo method : methodInfos) {
                    if(Objects.equals(className,method.getClassName()) && !method.getClassName().contains("$")){
                        continue;
                    }
                    Edge edge = new Edge();
                    edge.setSource(className);
                    edge.setTarget(method.getClassName());
                    edges.add(edge);
                }
            }
        }
        Map<String,Object> map = new HashMap<>();
        map.put("nodes",nodes);
        map.put("edges",edges);
        return JSON.toJSONString(map);
    }

    public static void main(String[] args) {
        String programer = getProgramer("/Users/mouwenyao/Workspace/common-tool/tool/src", e -> {
            return e.contains("com.mwy")
                    && !e.contains("<clinit>")
                    && !e.contains("<init>");
        });
        System.out.println(programer);
    }



    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private static class Node{
        private String name;
    }
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private static class Edge{
        private String source;
        private String target;
        private String label;
    }
}
