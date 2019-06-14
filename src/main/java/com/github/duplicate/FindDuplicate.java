package com.github.duplicate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @program: convenientUtil
 * @description: 找出项目中同名类
 * @author: hu_pf
 * @create: 2019-06-14 14:25
 **/
public class FindDuplicate {

    //全路径名+类名的List集合
    public static List<PackDomain> PACKAGE_LIST = new ArrayList<>();
    //填写项目所在的路径
    public static String PATH = "";

    public static String JAVA_STRING = "java";

    public static String PACKAGE_STRING = "package";

    public static String EMPTY = "";

    public static String SPLIT = "||||||||";

    public static List<String> findDuplicatePath(String path){
        PATH = path;
        try {
            getFilePath(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //得到去重后的集合
        List<PackDomain> duplicateElements = getDuplicateElements();

        return duplicateElements.stream().map(PackDomain::getPath).collect(Collectors.toList());
    }

    public static void main(String[] args) throws IOException {

    }

    private static List<File> getFilePath(String path) throws IOException {
        File file = new File(path);
        File[] files = file.listFiles();
        List<File> fileList = new ArrayList<>();
        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()){
                getFilePath(files[i].getPath());
            }else {
                if (files[i].getName().endsWith(JAVA_STRING)){
                    String pack = getPack(files[i].getPath());
                    PackDomain packDomain = new PackDomain();
                    packDomain.setPath(files[i].getPath().replace(PATH,EMPTY));
                    packDomain.setAllName(pack+files[i].getName());
                    packDomain.setCount(1);
                    PACKAGE_LIST.add(packDomain);
                }
            }
        }
        return fileList;
    }

    private static String getPack(String javaPath) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(javaPath));
        String s;
        while ((s=bufferedReader.readLine())!=null){
            if (s.contains(PACKAGE_STRING)){
                break;
            }
        }
        return s;
    }

    private static List<PackDomain> getDuplicateElements(){
        Map<String,PackDomain> map = new HashMap<>();
        for (PackDomain packDomain : PACKAGE_LIST){
            if (map.containsKey(packDomain.getAllName())){
                PackDomain old = map.get(packDomain.getAllName());
                packDomain.setCount(old.getCount()+1);
                packDomain.setPath(old.getPath()+SPLIT+packDomain.getPath());
                map.put(packDomain.getAllName(),packDomain);
            }
            map.put(packDomain.getAllName(),packDomain);
        }
        return map.entrySet().stream()
                .filter(entry -> entry.getValue().getCount()>2)
                .map(entry -> entry.getValue())
                .collect(Collectors.toList());
    }
}

class PackDomain{
    private String allName;
    private String path;
    private Integer count;

    public String getAllName() {
        return allName;
    }

    public void setAllName(String allName) {
        this.allName = allName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}