package com.github.ptnfx;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @program: convenientUtil
 * @description: 从xml中获取到表名
 * @author: hu_pf
 * @create: 2018-11-23 13:40
 **/
public final class GetTableName {

    public static HashSet<String> getTableName(String path) throws IOException {
        return getFileName(path);
    }

    private static HashSet<String> getFileName(String path) throws IOException {
        File file = new File(path);
        File[] tempList = file.listFiles();
        HashSet<String> resultHash = new HashSet<>();
        for (int i = 0; i < tempList.length; i++) {
            if (tempList[i].isDirectory()){
                HashSet<String> tableName =getFileName(tempList[i].getPath());
                resultHash.addAll(tableName);
            }
            else {
                String str = readFile(path+"/"+tempList[i].getName());
                HashSet<String> tableName = (HashSet<String>) new TableNameParser(str).tables();
                resultHash.addAll(tableName);
            }
        }
        return resultHash;
    }

    //获得文件内容
    private static String readFile(String fileName) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName));
        String str;
        StringBuilder stringBuilder = new StringBuilder();
        while ((str = bufferedReader.readLine()) != null){
            stringBuilder.append(str.toUpperCase() +"\n");
        }
        bufferedReader.close();
        return stringBuilder.toString();
    }
}
