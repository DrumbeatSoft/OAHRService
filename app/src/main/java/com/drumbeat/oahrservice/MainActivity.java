package com.drumbeat.oahrservice;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.drumbeat.hrservice.HRService;
import com.drumbeat.hrservice.util.GZIPCompressUtils;

public class MainActivity extends AppCompatActivity {

//            private final static String BASE_URL = "http://47.92.181.31:8866/"; //生产
    private final static String BASE_URL = "http://192.168.71.8:8866/"; //测试
//            private final static String BASE_URL_H5 = "http://47.92.181.31:8822/#/attendance-management";//生产
    private final static String BASE_URL_H5 = "http://192.168.70.35:8088/#/attendance-management";//测试
    private final static String token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzY29wZSI6WyJhcHAiXSwibmFtZSI6IuadjueRnuiKsyIsImVtcGxveWVlSWQiOiJjZDEzZWE0NS1iNGExLTRlOWItODI2Ny1lMTNlMDI4Y2I5NDAiLCJleHAiOjE2MTA1MTc2OTYsInVzZXJJZCI6IjM0NDIwYzY2LWZkNTEtNDI5Zi04NzQxLWNmYjk0MDk1YTFhZSIsImp0aSI6ImNlODQwNTI2LTBmZDktNDQwYy05YjEzLTc3ZjM4OWJmM2EzOCIsImNsaWVudF9pZCI6Ik9hQXBwIiwidXNlcm5hbWUiOiJ6enoxMjYifQ.tbvp63nKT842yzCAhO1dTMirT-e4HAtEGScVLg6Xx1X5IPt_0EiQx5xeuT5JdpTKh_soBw4TBMBwsfGX9bjNGIyMQ4DcVtbRPG0_Gy5PxGZpxA2evSRgenksXRcyuPw0XEoLWiIE9UjzxjndbDlUf7vUw-EPQU5XFcpFtAO4siIltMH_nsOiL0iXi4MekGqJ8paUcQekmJZDlvhB-pr4LlCbdTx5vHZCpi_brFf6HbQ8jRWgl0wVpF9oalxP3Afk6pJ0H9kb3Hh-u3r6kLLjRAuCkTZZmixKQhrghbkjdFk15KCqH7hnBEa9mygjyj6_6SNz0MUQeDthGFjRYE4dxA";//测试


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

        findViewById(R.id.btn_compare).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String facedata = GZIPCompressUtils.unCompress(data);
                String[] array = facedata.split(",");
                float[] tocomparedata = new float[array.length];

                for (int i = 0; i < array.length; i++) {
                    tocomparedata[i] = Float.parseFloat(array[i]);
                }
                HRService.from(MainActivity.this).compareFace(MainActivity.this, tocomparedata, new HRService.OnCompareFaceListener() {
                    @Override
                    public void onCompareSuccess() {
                        Toast.makeText(MainActivity.this, "比对成功", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCompareFailed(String errorMsg) {
                        Toast.makeText(MainActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });


    }

    String data = "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";
}
