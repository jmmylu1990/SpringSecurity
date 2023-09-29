package com.example.security.pojo;

import lombok.Getter;

import java.io.Serializable;
@Getter
public class ResultData<T> {
    private T data;
    private int code;
    private String msg;

    /**
     * 若沒有資料傳回，預設狀態碼為0，提示訊息為：操作成功！
     */
    public ResultData() {
        this.code = 0;
        this.msg = "發布成功!!";
    }

    /**
     * 若沒有資料返回，可以人為指定狀態碼和提示訊息
     */
    public ResultData(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    /**
     * 有資料回傳時，狀態碼為0，預設提示訊息為：操作成功！
     */
    public ResultData(T data) {
        this.data = data;
        this.code = 0;
        this.msg = "發布成功!!";
    }

    /**
     * 有資料返回，狀態碼為0，人為指定提示訊息
     */
    public ResultData(T data, String msg) {
        this.data = data;
        this.code = 0;
        this.msg = msg;
    }
}
