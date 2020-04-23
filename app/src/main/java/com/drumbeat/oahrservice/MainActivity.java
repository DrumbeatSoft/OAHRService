package com.drumbeat.oahrservice;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.drumbeat.hrservice.HRService;

public class MainActivity extends AppCompatActivity {
    String token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzY29wZSI6WyJhcHAiXSwibmFtZSI6IuadjueRnuiKsyIsImVtcGxveWVlSWQiOiJjZDEzZWE0NS1iNGExLTRlOWItODI2Ny1lMTNlMDI4Y2I5NDAiLCJleHAiOjE1ODgxNDU2MjQsInVzZXJJZCI6IjM0NDIwYzY2LWZkNTEtNDI5Zi04NzQxLWNmYjk0MDk1YTFhZSIsImp0aSI6IjVhYzIzOWRkLTlkNmItNDczZS05NWNhLWUyOTAzZGU1ODkxZCIsImNsaWVudF9pZCI6Ik9hQXBwIiwidXNlcm5hbWUiOiJ6enoxMjYifQ.bI8ooe-JOhxvmNrY6k7baFFHoOOjPTvWmdz6br1uIGQAse9HzslZ_-K4zT1155lFEjrLMGCVqr6a8fx8-SuleAOgF-U03bS1NjoYxi3MSeShH9wBoRlSM0JWVpE0vJSnMIyxBLliXcPZOirs4AqvaStmsqb92JUE3FbpZS_wgjZPtOjxwSLn65GICUtY5ffFp95AjAZGGkBhOXymaark4ZAbnctrGQ--6V5FDMVtlvlRjYYYN-FUzACKyWWt4O0AVhr4Qp-eXocAKtQxuTDEjSZ68yhENA2JJboBPeVOId5ai_52OjVone4gS1-WaSclYhqgdHVwKdNEfqzVsr_90A";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_oa).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HRService.from(MainActivity.this)
                        //设置身份令牌
                        .setHrToken(token)
                        //设置水印文字 非必要参数
                        .setWatermarkStr("")
                        //切换为测试环境 配合调试 默认为不开启
                        .setTestService(true)
                        .startHR();

            }
        });
    }
}
