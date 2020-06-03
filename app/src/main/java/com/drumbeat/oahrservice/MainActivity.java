package com.drumbeat.oahrservice;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.drumbeat.hrservice.HRService;

public class MainActivity extends AppCompatActivity {
    private String token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzY29wZSI6WyJhcHAiXSwibmFtZSI6IumfqemdmeS8miIsImVtcGxveWVlSWQiOiI5Y2Q4YmY2Ny0yNjA3LTQ3MWQtODdmYy0xMGE5OTg2NGZlOTUiLCJleHAiOjE1OTE3NzE4MjksInVzZXJJZCI6IjE5YmExYjNiLTdmNmEtNDk3Zi05NWRkLTRkZGZlZmJiMDE4NSIsImp0aSI6ImMxYjI0MjQ5LWZmNDAtNDliNi04YmYyLTE5ODQ5NmQ5M2Q2MSIsImNsaWVudF9pZCI6Ik9hQXBwIiwidXNlcm5hbWUiOiJhYmF1MDMifQ.j2B4XhwV1sIih2dkTFXVLwbbFmx-u9o8Xhg-7P8p1U2oUNY-C73YcaTj5DYtleT5B--XhoJHImW-zfrIGWSCpNlT_b2GFl6hslSYk1mFC05WtNrPE13oHyRZ9lbCQO3OTu5vC09JTaSfSv1JxUTvdcbkpG_LVuRK38N53sKvCr-KMG3fuLNW46CnVeio-aREzQcfntdg9bM9RpiwHCRLIP4S2PXsQtMMtcTjwVptWWX6lSTA-unR7XZW5cJ1C8VQZfcvBHZJuLfrLoRuZj-QoRoqPmDAQ1W8NWV65RVa3AAreRIUxSHy0U4CJ74a3cU9N4Xf6MtRcfQ8DKH0NCz2vA";
    private final static String BASE_URL = "http://47.92.181.31:8866/"; //生产
    private final static String BASE_URL_H5 = "http://47.92.181.31:8822/#/attendance-management";//生产
//    private final static String BASE_URL = "http://192.168.70.50:8866/"; //测试
//    private final static String BASE_URL_H5 = "http://192.168.70.187:8088/#/attendance-management";//测试

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
