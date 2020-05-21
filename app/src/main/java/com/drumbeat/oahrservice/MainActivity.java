package com.drumbeat.oahrservice;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.drumbeat.hrservice.HRService;

public class MainActivity extends AppCompatActivity {
    String token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzY29wZSI6WyJhcHAiXSwibmFtZSI6IuS-r-Wjq-i0pCIsImVtcGxveWVlSWQiOiI5MmMwZDBmZC05ZWRjLTQ0OTItYjA5YS1mM2EzYWFjNTMzOTQiLCJleHAiOjE1OTAzOTk3ODMsInVzZXJJZCI6ImQ2M2MwODZiLTdmNGQtNDVlOS1iNWM5LTk4MGYxYTgzYjk1MSIsImp0aSI6Ijk1NGZiZDRjLTM2MzEtNDA2ZC05MDhhLTc5ZWMwZGNlOGMwOCIsImNsaWVudF9pZCI6Ik9hQXBwIiwidXNlcm5hbWUiOiJhYmF0MjUifQ.t5ONCKmQ8UYjDCGNVpB_S2MxAjtvz5j052mawiO2P5gqNENmE8JDLHp37W_UCzDly66mGO9g4DFV834T3CcyzZa8jqLtk4sw6BGX5JvB3i6OOKmag8MgO0CHTsHrGbuLRbwf8XXedS5kbu6pU9UugM1l3yBmLopCenHrXpmUNZ5Oald6svDIo0l9W8e4Pvlc858K_ZvhoMCCNhyioTMDp303rIU6GGrYR5zb92ABBN1to6mES2s4r0Di2_9uFE7vXDT_-yjuS-hzWegyQBfvJOd0l1H_4ay6g4rjt7anIM1UyCFQIPEwOsPTxYCJx-38atSDQzrAWUdY7JSV90S73Q";
//    private final static String BASE_URL = "http://192.168.71.8:8866/";//测试
    //    private final static String BASE_URL_H5 = "http://192.168.70.35:8088/#/attendance-management";//测试 雷鸣
//    private final static String BASE_URL_H5 = "http://192.168.70.95:8088/#/attendance-management";//测试 李阳
//    private final static String BASE_URL_H5 = "http://192.168.70.187:8088/#/attendance-management";//测试 高博

    private final static String BASE_URL = "http://47.92.181.31:8866/"; //生产
    private final static String BASE_URL_H5 = "http://47.92.181.31:8822/#/attendance-management";//生产


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
