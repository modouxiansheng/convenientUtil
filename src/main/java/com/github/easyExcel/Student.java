package com.github.easyExcel;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @program: Test
 * @description:
 * @author: hu_pf
 * @create: 2019-04-01 16:46
 **/


public class Student {
    @Excel(name = "姓名")
    private String name;

    @Excel(name = "年龄")
    private Integer age;


    @Excel(name = "生日",dateFormat="yyyyMMdd")
    private Date date;

    @Excel(name = "金额", isNull = 1)
    private BigDecimal bigDecimal;

    public BigDecimal getBigDecimal() {
        return bigDecimal;
    }

    public void setBigDecimal(BigDecimal bigDecimal) {
        this.bigDecimal = bigDecimal;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }
}
