package com.drumbeat.oahrservice;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.drumbeat.hrservice.HRService;

public class MainActivity extends AppCompatActivity {

//        private final static String BASE_URL = "http://47.92.181.31:8866/"; //生产
    private final static String BASE_URL = "http://192.168.71.8:8866/"; //测试
//        private final static String BASE_URL_H5 = "http://47.92.181.31:8822/#/attendance-management";//生产
    private final static String BASE_URL_H5 = "http://192.168.70.35:8088/#/attendance-management";//测试
    private final static String token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzY29wZSI6WyJhcHAiXSwibmFtZSI6IuadjueRnuiKsyIsImVtcGxveWVlSWQiOiJjZDEzZWE0NS1iNGExLTRlOWItODI2Ny1lMTNlMDI4Y2I5NDAiLCJleHAiOjE2MDY2OTkxMDksInVzZXJJZCI6IjM0NDIwYzY2LWZkNTEtNDI5Zi04NzQxLWNmYjk0MDk1YTFhZSIsImp0aSI6ImQ2MTMwZmJkLWZhNGMtNGY4YS04ZTQ4LWNkZTM5OGM2ZDRjMiIsImNsaWVudF9pZCI6Ik9hQXBwIiwidXNlcm5hbWUiOiJ6enoxMjYifQ.qEd12Zwe0AfI0hGrilLu9sIw1VEKGzdN_gJWc_zHVOeK5IC-byd6E2JaaQBZ1BHFrPr9vLnf_f8hcc8x8upZ2y_wDg-i6tZL5jgqEVQ3f4RWoAjQaT1dwRRAslDiiI70gVrwhjA10tgRupKJMHntHROCJFfxrB1yOP1sX13Z3TVSRYtPWpFElHuZegaBzIFer7vXOBAREVgERAwvw-MypQlRZUzpOOCnKklQ3BW4zJLNMWg1G0JNVi1G2fQcmz5pptRUsu9zvjNx8k6jcc2dQgwiU6ztgG9SkMnH5aNADnGo_jlUzY9NwtX94aQtyI3twzbMc8bHQqMkCItCTRxBgA";//测试


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
