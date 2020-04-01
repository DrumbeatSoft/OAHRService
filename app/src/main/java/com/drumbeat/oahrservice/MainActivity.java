package com.drumbeat.oahrservice;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.drumbeat.hrservice.HRService;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_oa).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HRService.openHR(MainActivity.this, "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzY29wZSI6WyJhcHAiXSwibmFtZSI6IuadjueRnuiKsyIsImVtcGxveWVlSWQiOiJjZDEzZWE0NS1iNGExLTRlOWItODI2Ny1lMTNlMDI4Y2I5NDAiLCJleHAiOjE1ODYxNTM4MTAsInVzZXJJZCI6IjM0NDIwYzY2LWZkNTEtNDI5Zi04NzQxLWNmYjk0MDk1YTFhZSIsImp0aSI6ImZkY2RhMmMzLTQ0NWEtNGQ0OC04YTg0LTYzNWRkN2FjNjcxOCIsImNsaWVudF9pZCI6Ik9hQXBwIiwidXNlcm5hbWUiOiJ6enoxMjYifQ.Iju_QCy2Z8oyLWVtemX-c32ftMfFPJGAyTrs64Wsa1j3dQMOrZ7KNNgFnIgUP0lTHzfpWesoiR574ecE2BTVkColIY9GyTzY5TkyOVJPtYAWDYIQDx7zhRUR5VW9KcJ63PQGUVWCg8rMQZjDhRh1tpttgnnGtZBMcvxWYwaB9lhPfIip9reC7jOBk2-keAVDAS3rdhbvcwwNM-H8MWFxIKaHOhYwBGZMVfVPKiTQqqF0x5nTVlmQrSs7brpu79Nv2G8b6byMK2G0vGIUr4tjdpHFHwIRrxxcYfHj_u9SZ41Y7UgMefWROBIj1weA5n92Ta8cCDDc2JZEyWmVn-j1eQ");
            }
        });
    }
}
