package com.mwy.tool;

import org.springframework.util.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/**
 * author mouwenyao
 * date 2019-04-27 16:11
 * 文件夹路径相关操作
 */
public class DirUtil {
    /**
     * 查找指定路径下，符合规则的文件名
     *
     * @param basePath
     * @param pattern
     * @return
     */
    public static List<String> getFileList(String basePath, String pattern) {
        return getFileList(basePath, pattern, r -> r);
    }

    /**
     * 查找指定路径下，符合规则的文件名
     *
     * @param basePath
     * @param pattern
     * @return
     */
    public static List<String> getFileList(String basePath, String pattern, Function<String, String> fun) {
        File f = new File(basePath);
        if (!f.exists()) {
            return Collections.emptyList();
        }
        List<String> filePaths = new ArrayList<>();
        if (f.isFile() && f.getAbsolutePath().matches(pattern)) {
            String path = fun.apply(f.getPath());
            if (!StringUtils.isEmpty(path)) {
                filePaths.add(path);
            }
        } else if (f.isDirectory()) {
            File[] files = f.listFiles();
            for (File file : files) {
                List<String> fileList = getFileList(file.getAbsolutePath(), pattern, fun);
                filePaths.addAll(fileList);
            }
        }
        return filePaths;
    }

    public static void main(String[] args) {
        List<String> fileList = getFileList("/Users/mouwenyao/Workspace/common-tool", ".*/src/main/java/.*\\.java",e->{
            String[] split = e.split("/src/main/java/");
            if(split!= null && split.length > 1){
                return  split[1].replace(".java","").replace("/",".");
            }else {
                return e;
            }
        });
        System.out.println(fileList);
    }
}
