package com.drumbeat.oahrservice;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.drumbeat.hrservice.HRService;

public class MainActivity extends AppCompatActivity {

    //    private final static String BASE_URL = "http://47.92.181.31:8866/"; //生产
    private final static String BASE_URL = "http://192.168.71.8:8866/"; //测试
    //    private final static String BASE_URL_H5 = "http://47.92.181.31:8822/#/attendance-management";//生产
    private final static String BASE_URL_H5 = "http://192.168.70.35:8088/#/attendance-management";//测试
    private final static String token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzY29wZSI6WyJhcHAiXSwibmFtZSI6IuWwj-m4ozUxIiwiZW1wbG95ZWVJZCI6IjcxNmQzNzBkMmFmNTQ3NmQ4NTZlODYwOGZhNGVhMjg5IiwiZXhwIjoxNjAzNjk4MDgyLCJ1c2VySWQiOiIxZmRmMTUzOWFiMDU0MzlhYTdiZTIzODhlZWY5YWY4MyIsImp0aSI6ImE0MGM0ZmZmLTc5OGYtNDBiYy1hOThmLWRmNmY5ZThlZDQ3NCIsImNsaWVudF9pZCI6Ik9hQXBwIiwidXNlcm5hbWUiOiJtbW0wNTEifQ.AATU0vMN-CnDXkDlEM7VzTTcjfFKbvYdyzViTcdT_sSuBNS7TEURO_2MNiei6h0DTMX3siMKsqlnjPXAtp1Z139KTS4J5NmxvB9SNXbsA01CaEG2REj5m6lscwuaNVTJVJqaMgFWSzGUrh0MoSIJq9IGste90kKTz4vAULZ8SKLFXuWrxnJYt_bF4T2svUyq47o3mPu5FOL2_5dkV0E4er9patYYNiTwYFAXUi7c0uxVLB3jaDfeIDwL_fNwOFEGb-K6ni-KXF091FdJnD_vFt9T_wVyCOEB-S183QvK1VyMZNnLVGJe9CDqTKRbNp6c4l9hQxSUHGvaJ25OGeqXaQ";//测试


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_oa).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HRService.from(MainActivity.this)
                        //设置服务器地址
                        .setBaseUrl(BASE_URL, BASE_URL_H5)
                        //设置身份令牌
                        .setHrToken(token)
                        //设置水印文字 非必要参数
                        .setWatermarkStr("")
                        .startHR();

            }
        });
    }
}
