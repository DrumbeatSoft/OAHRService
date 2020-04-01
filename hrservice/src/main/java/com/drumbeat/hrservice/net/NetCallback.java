package com.drumbeat.hrservice.net;

public interface NetCallback {
    void onSuccess(String succeed);

    void onFail(String failed);
}
