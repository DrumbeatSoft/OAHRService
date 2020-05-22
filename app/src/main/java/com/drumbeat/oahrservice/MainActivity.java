package com.drumbeat.oahrservice;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.drumbeat.hrservice.HRService;

public class MainActivity extends AppCompatActivity {
//    private String token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzY29wZSI6WyJhcHAiXSwibmFtZSI6Iua9mOiZuSIsImVtcGxveWVlSWQiOiIwNTIxYmJiYS1mMmUzLTRmOWUtOWVhZS1kNDIwM2U0MThjZDEiLCJleHAiOjE1OTA3MTQwOTIsInVzZXJJZCI6IjZiODc5MzExLTdmN2YtNDQxZS05YWQzLTI0OThhMmQwNjc3MiIsImp0aSI6IjY3ZWY2OTgyLTMxZGYtNDQ4MC05MzgyLWJiOTc4NzVhMGI3YyIsImNsaWVudF9pZCI6Ik9hQXBwIiwidXNlcm5hbWUiOiJhYWEwMTUifQ.gwWCO7lODsg-LWRLJVojNxPDgW7v49AHh_I3Was7D5jWXDILlf99N1-58LoW4FssmeNr4k6rlIe1a0kUVKI8TXzXufVfLf-652hgeetXFyAtdrzYrgHppk0tBuh-uE0wqw2a418xSLzm9VMHc-aPjaX8SpeLFmADR2Q93botH5pUNJ_XXosvVDuMT-_dn56n4REoYqxVPBVrDRBx9cFbiyT6iT-FaAQ-wogSBjUdYx9N5GonSvviHuWFcbcqWWgdI99JQq48_Y5H2sMWl08WdKOQTC8y3G7qr1FGf9jl7xpaUkbOpDeJFU9y4uEncnE9grz0DcH7Zjl_6wxbUCEaVQ";
    private String token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzY29wZSI6WyJhcHAiXSwibmFtZSI6Iua9mOiZuSIsImVtcGxveWVlSWQiOiIwNTIxYmJiYS1mMmUzLTRmOWUtOWVhZS1kNDIwM2U0MThjZDEiLCJleHAiOjE1OTA3MTQwOTIsInVzZXJJZCI6IjZiODc5MzExLTdmN2YtNDQxZS05YWQzLTI0OThhMmQwNjc3MiIsImp0aSI6IjY3ZWY2OTgyLTMxZGYtNDQ4MC05MzgyLWJiOTc4NzVhMGI3YyIsImNsaWVudF9pZCI6Ik9hQXBwIiwidXNlcm5hbWUiOiJhYWEwMTUifQ.gwWCO7lODsg-LWRLJVojNxPDgW7v49AHh_I3Was7D5jWXDILlf99N1-58LoW4FssmeNr4k6rlIe1a0kUVKI8TXzXufVfLf-652hgeetXFyAtdrzYrgHppk0tBuh-uE0wqw2a418xSLzm9VMHc-aPjaX8SpeLFmADR2Q93botH5pUNJ_XXosvVDuMT-_dn56n4REoYqxVPBVrDRBx9cFbiyT6iT-FaAQ-wogSBjUdYx9N5GonSvviHuWFcbcqWWgdI99JQq48_Y5H2sMWl08WdKOQTC8y3G7qr1FGf9jl7xpaUkbOpDeJFU9y4uEncnE9grz0DcH7Zjl_6wxbUCEaVQ";
//    private final static String BASE_URL = "http://47.92.181.31:8866/"; //生产
//    private final static String BASE_URL_H5 = "http://47.92.181.31:8822/#/attendance-management";//生产
    private final static String BASE_URL = "http://192.168.70.50:8866/"; //测试
    private final static String BASE_URL_H5 = "http://192.168.70.187:8088/#/attendance-management";//测试

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
