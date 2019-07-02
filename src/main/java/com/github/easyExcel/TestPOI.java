package com.github.easyExcel;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * @program: Test
 * @description:
 * @author: hu_pf
 * @create: 2019-04-01 16:40
 **/
public class TestPOI {

    public static void main(String[] args) throws Exception {
        InputStream inputStream = new FileInputStream("/Users/hupengfei/Downloads/1c5ec464-c0d4-4138-a786-c177688afe55_1.xlsx");
        EasyExcelUtil<Student> easyExcelUtil = new EasyExcelUtil<>(Student.class);
        Map<String,Object> students = easyExcelUtil.importExcel(inputStream);
    }

}
