package com.drumbeat.hrservice;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.drumbeat.hrservice.net.DataObject;
import com.drumbeat.hrservice.net.JsonConverter;
import com.drumbeat.hrservice.net.KalleCallback;
import com.drumbeat.hrservice.util.LogUtils;
import com.drumbeat.hrservice.view.HRActivity;
import com.drumbeat.zface.ZFace;
import com.drumbeat.zface.config.ZFaceConfig;
import com.drumbeat.zface.constant.ErrorCode;
import com.drumbeat.zface.listener.Action;
import com.drumbeat.zface.listener.CompareListener;
import com.drumbeat.zface.listener.InitListener;
import com.drumbeat.zface.listener.RecognizeListener;
import com.drumbeat.zface.permission.Permission;
import com.tencent.smtt.sdk.QbSdk;
import com.yanzhenjie.kalle.Kalle;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * 提供OA HR 服务
 */
public class HRService {
    private final WeakReference<Context> mContext;
    private String hrToken;
    private String watermarkStr;
    private String baseUrl;
    private String baseUrlH5;
    private TodoCountListener todoCountListener;
    private final static String GET_TODO_COUNT = "flowable/TaskProcess/getCountMyToDo";

    public static boolean zfaceInitSuccess;

    private HRService(Context context) {
        this.mContext = new WeakReference<>(context);
        initZFaceConfig(context);

    }

    public static HRService from(Context context) {
        //x5内核初始化接口
        QbSdk.initX5Environment(context, null);
        return new HRService(context);
    }

    /**
     * 设置BaseUrl
     *
     * @param baseUrl   文件上传接口地址
     * @param baseUrlH5 H5页面地址
     * @return
     */
    public HRService setBaseUrl(String baseUrl, String baseUrlH5) {
        this.baseUrl = baseUrl;
        this.baseUrlH5 = baseUrlH5;
        return this;
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
     * 开启HR界面
     */
    public void startHR() {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString("baseUrl", baseUrl);
        bundle.putString("baseUrlH5", baseUrlH5);
        bundle.putString("hrToken", hrToken);
        bundle.putString("watermarkStr", watermarkStr);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Context packageContext = mContext.get();
        intent.setClass(packageContext, HRActivity.class);
        packageContext.startActivity(intent);
    }

    /**
     * 获取办事项数量，需要设置baseURL、HRtoken
     *
     * @param listener
     */
    public void getTodoCount(TodoCountListener listener) {
        this.todoCountListener = listener;
        if (listener != null) {
            if (TextUtils.isEmpty(hrToken) && TextUtils.isEmpty(baseUrl) && TextUtils.isEmpty(baseUrlH5)) {
                throw new UnsupportedOperationException("请检查hrToken、baseUrl、baseUrlH5");
            }
            requestTodoCount();
        }
    }

    /**
     * 请求代办事项数量
     */
    private void requestTodoCount() {
        Kalle.get(baseUrl + GET_TODO_COUNT)
                .addHeader("Authorization", "Bearer " + hrToken)
                .converter(new JsonConverter())
                .perform(new KalleCallback<DataObject<Integer>>() {
                    @Override
                    protected void onSuccess(DataObject<Integer> succeed) {
                        todoCountListener.getTodoCount(succeed.getData());
                    }

                    @Override
                    protected void onFailed(String failed) {
                        todoCountListener.onFailed(failed);
                    }
                });
    }


    /**
     * 获取代办事件数量的接口
     */
    public interface TodoCountListener {
        void getTodoCount(int todoCount);

        void onFailed(String failed);
    }

    /**
     * zface配置资源路径
     *
     * @param context
     */
    private void initZFaceConfig(Context context) {
        ZFace.setConfig(ZFaceConfig.newBuilder()
                .setResource_model_download_base_url("https://drumbeat-update-app.oss-cn-hangzhou.aliyuncs.com/face/model") // model文件baseurl
                .setResource_so_download_base_url("https://drumbeat-update-app.oss-cn-hangzhou.aliyuncs.com/face/so") // so文件baseurl
                .build());
        initZFace(context);
    }

    /**
     * 初始化zface
     */
    private void initZFace(Context context) {
        ZFace.with(context).recognizer().init(new InitListener() {
            @Override
            public void onSuccess() {
                zfaceInitSuccess = true;
                LogUtils.debug("初始化成功");
            }

            @Override
            public void onFailure(ErrorCode errorCode, String errorMsg) {
                zfaceInitSuccess = false;
                LogUtils.debug("初始化失败，错误码：" + errorCode);
            }
        });

    }

    /**
     * 人脸比对
     *
     * @param context
     * @param featureDataRegister
     */
    public void compareFace(final Context context, final float[] featureDataRegister, final OnCompareFaceListener onCompareFaceListener) {
        if (zfaceInitSuccess) {
            ZFace.with(context)
                    .recognizer()
                    .recognize(new RecognizeListener() {
                        @Override
                        public void onSuccess(float[] featureData, byte[] imgData) {
                            if (featureData != null && featureData.length > 0) {
                                compareFaceData(context, featureDataRegister, featureData, onCompareFaceListener);
                            } else {
                                onCompareFaceListener.onCompareFailed("未检测到人脸特征数据");
                                LogUtils.debug("未检测到人脸特征数据");
                            }
                        }

                        @Override
                        public void onFailure(ErrorCode errorCode, String errorMsg) {
                            LogUtils.debug("识别失败，错误码：" + errorCode);
                            onCompareFaceListener.onCompareFailed("识别失败，请重试");
                        }
                    });
        } else {
            initZFace(context);
        }

    }

    private void compareFaceData(final Context context, float[] data1, float[] data2, final OnCompareFaceListener onCompareFaceListener) {
        ZFace.with(context).recognizer().compare(data1, data2, new CompareListener() {
            @Override
            public void onSuccess(float faceSimilar) {
                if (faceSimilar > 0.7) {
                    onCompareFaceListener.onCompareSuccess();
                } else {
                    onCompareFaceListener.onCompareFailed("比对失败，请重试");
                    LogUtils.debug("compareFaceData 比对失败，相似度：" + faceSimilar);
                }
            }

            @Override
            public void onFailure(ErrorCode errorCode, String errorMsg) {
                LogUtils.debug("compareFaceData 比对失败，错误码：" + errorCode);
                onCompareFaceListener.onCompareFailed("比对失败，请重试");
            }
        });
    }

    public interface OnCompareFaceListener {

        void onCompareSuccess();

        void onCompareFailed(String errorMsg);
    }
}
