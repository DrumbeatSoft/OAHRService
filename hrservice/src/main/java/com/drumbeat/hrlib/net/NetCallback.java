package com.drumbeat.hrlib.net;

public interface NetCallback {
    void onSuccess(String succeed);

    void onFail(String failed);
}
