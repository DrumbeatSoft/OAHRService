package com.drumbeat.hrservice;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.drumbeat.hrservice.view.HRActivity;

import java.lang.ref.WeakReference;

/**
 * 提供OA HR 服务
 */
public class HRService {
    private final WeakReference<Context> mContext;
    private String hrToken;
    private String watermarkStr;
    private boolean isTestService;

    private HRService(Context context) {
        this.mContext = new WeakReference<>(context);
    }

    public static HRService from(Context context) {
        return new HRService(context);
    }

    /**
     * 设置HR身份令牌
     *
     * @param hrToken hr身份令牌
     * @return HRService
     */
    public HRService setHrToken(String hrToken) {
        this.hrToken = hrToken;
        return this;
    }

    /**
     * 设置水印文字
     *
     * @param watermarkStr 设置水印文字
     * @return HRService
     */
    public HRService setWatermarkStr(String watermarkStr) {
        this.watermarkStr = watermarkStr;
        return this;
    }

    /**
     * 是否开启测试服务
     *
     * @param isTestService
     * @return HRServic
     */
    public HRService setTestService(boolean isTestService) {
        this.isTestService = isTestService;
        return this;
    }

    /**
     * 开启HR界面
     */
    public void startHR() {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString("hrToken", hrToken);
        bundle.putString("watermarkStr", watermarkStr);
        bundle.putBoolean("isTestService", isTestService);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Context packageContext = mContext.get();
        intent.setClass(packageContext, HRActivity.class);
        packageContext.startActivity(intent);
    }

}
