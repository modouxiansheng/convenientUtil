package com.github.easyExcel;

/**
 * @program: Test
 * @description:
 * @author: hu_pf
 * @create: 2019-04-02 11:03
 **/
public class ErrorInf {

    private Boolean success;

    private String message;

    public ErrorInf(){

    }
    public ErrorInf(Boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
