package com.drumbeat.oahrservice;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.drumbeat.hrservice.HRService;

public class MainActivity extends AppCompatActivity {
    String token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzY29wZSI6WyJhcHAiXSwibmFtZSI6IuadjueRnuiKsyIsImVtcGxveWVlSWQiOiJjZDEzZWE0NS1iNGExLTRlOWItODI2Ny1lMTNlMDI4Y2I5NDAiLCJleHAiOjE1ODc1MzkzNDksInVzZXJJZCI6IjM0NDIwYzY2LWZkNTEtNDI5Zi04NzQxLWNmYjk0MDk1YTFhZSIsImp0aSI6IjBjZDZiZjY3LWFjMGMtNGYwYy1hYjcwLTk2MDI3MDE5YmRlYSIsImNsaWVudF9pZCI6Ik9hQXBwIiwidXNlcm5hbWUiOiJ6enoxMjYifQ.YlrBV_5Q6NKhoanDKaliwspMVxb-W7qC7bUCa1KlHhhhVBYRnPiigtwJ1bY-Dkf6-wQcz9FRKlb2g7fB-SYf8g2BCoI9LX8JuWRDwIOtEzsPuCj2rpbIEcHXKwcQlFGIn3JA4rqfksGK4hdmB_2BNiwYDEu-4_hEtC60oEW9bmZkFAviWb8ZT2SdZHRvrDPkRtnWtJdjlbZ-_gwEb_DuTINX0Zo9wH0Ly3CmyAGHzopm-jzSTX36PIbdGSFUVO5ZpoHIB7Ozp4egXb6IpuMVVlL2OKEagUE0YgGtEdEdlwuraObtV26CMUX_xpDPgLgFcqO8hvJGxB_uwEM6QT6-7Q";

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
