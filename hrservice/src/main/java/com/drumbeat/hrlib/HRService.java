package com.drumbeat.hrlib;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * 提供OA HR 服务
 */
public class HRService {

    /**
     * 打开HR界面
     *
     * @param context      上下文
     * @param hrToken      hr身份令牌
     * @param watermarkStr 水印文字
     * @return
     */
    public static void openHR(Context context, String hrToken, String watermarkStr) {
        Intent intent = new Intent();
        intent.setClass(context.getApplicationContext(), HRActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("watermarkStr", watermarkStr);
        bundle.putString("hrToken", hrToken);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    /**
     * 打开HR界面
     *
     * @param context 上下文
     * @param hrToken hr身份令牌
     * @return
     */
    public static void openHR(Context context, String hrToken) {
        Intent intent = new Intent();
        intent.setClass(context.getApplicationContext(), HRActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("hrToken", hrToken);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

}
