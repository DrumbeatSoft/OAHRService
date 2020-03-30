package com.drumbeat.hrlib.bean;

/**
 * 接口返回的最外层数据结构
 *
 * @author ZuoHailong
 * @date 2019/3/12.
 */
public class DataObject<T> {
    private int code;
    private String desc;
    private T data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
