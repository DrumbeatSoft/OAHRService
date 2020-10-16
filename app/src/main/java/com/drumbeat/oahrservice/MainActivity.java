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
    private final static String token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzY29wZSI6WyJhcHAiXSwibmFtZSI6IuW8oOWAqSIsImVtcGxveWVlSWQiOiI2MGZiOGE3MGZhZGI0YTBlOTcyNmIzMDJkNmU5YTZiNCIsImV4cCI6MTYwMzQxOTA2MiwidXNlcklkIjoiNTc4OGJkNWNmZjAzNDAwMjk1MDk1ZjllYjNiYjVhNTAiLCJqdGkiOiIyZmVlYmYxMS0wZThjLTRkMDAtYTUzZS00NWJjNzdiYTJjZTciLCJjbGllbnRfaWQiOiJPYUFwcCIsInVzZXJuYW1lIjoienFxMTExIn0.FjF9UGgfvn7P652gmPPlifRIa-XcByn2rPx5uodVQZhkEOgY4uP5M0cyVWfGKfGy_3AldqAi74AZItWAqE4jepIJnjpNepGuCpTqOqT5KKKJ4s0XqRtmUQM3nYmLaiHw8nZ11O1Gr90zvDgqbZxpgy77MSAv5v-IUTxz-ACWKqHBh_oIKnS9eHFUAHsCWQX4tA032IXraIfi6WAaP4RBXahAFXqeaAxVQDSZ0Xb3DEBRQoerzqozeqcs7d2h9dnJ9GGo8P4KC17dcLZ4VS2BmCEVQ3qwD52H-qdQpDAD-8x8hblaoDvxGy2QWUGVc64tPLyJ8aKdRZpNOJbyCMUtbg";//测试


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
