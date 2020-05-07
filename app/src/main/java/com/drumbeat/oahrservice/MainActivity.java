package com.drumbeat.oahrservice;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.drumbeat.hrservice.HRService;

public class MainActivity extends AppCompatActivity {
    String token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzY29wZSI6WyJhcHAiXSwibmFtZSI6IumfqemdmeS8miIsImVtcGxveWVlSWQiOiI5Y2Q4YmY2Ny0yNjA3LTQ3MWQtODdmYy0xMGE5OTg2NGZlOTUiLCJleHAiOjE1ODkzNjAxOTYsInVzZXJJZCI6IjE5YmExYjNiLTdmNmEtNDk3Zi05NWRkLTRkZGZlZmJiMDE4NSIsImp0aSI6ImZkN2EyMzNmLTg0MTYtNDBmZS04NDVlLWNhNzg3OTExZDczZiIsImNsaWVudF9pZCI6Ik9hQXBwIiwidXNlcm5hbWUiOiJhYmF1MDMifQ.sxhH9cENDhkm8camQZo-AebcaScKtvHFUM8SXqekSlhAaE0WtmHsvgwMOVViQvP6u_YCg9d2PEP001Gppkss0XKFX8Ly6BL_8x8zzeuKSxFzEAp7srJmNeo5j301Th2srUdBbH3K-fijlPQvraidGn2EXrbtf_YzOqeMJjdszObqVsjp4IB4UVFTLIkgrR4hxN7wg8TbUJMJre7QnCPYk32v23n4zh5OBsn7TzFs-8mq5f6O9ibbXgBnVNQsMNiwtzhSnAdLcc_WmoIRobypT2rBifnYs0Tb7GvbGZs60l47G_0rV80oJHFl6cUpFHutQ3zOxjOE28uhzLI4eIYE6g";

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
//                        .setTestService(true)
                        .startHR();

            }
        });
    }
}
