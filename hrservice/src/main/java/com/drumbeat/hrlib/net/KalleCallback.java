package com.drumbeat.hrlib.net;

import com.yanzhenjie.kalle.simple.Callback;
import com.yanzhenjie.kalle.simple.SimpleResponse;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by ZuoHailong on 2019/12/2.
 */
public abstract class KalleCallback<T> extends Callback<T, String> {

    public KalleCallback() {
    }

    @Override
    public Type getSucceed() {
        Type superClass = getClass().getGenericSuperclass();
        return ((ParameterizedType) superClass).getActualTypeArguments()[0];
    }

    @Override
    public Type getFailed() {
        return String.class;
    }

    @Override
    public void onResponse(SimpleResponse<T, String> response) {
        if (response.isSucceed()) {
            onSuccess(response.succeed());
        } else {
            onFailed(response.failed());
        }
    }

    @Override
    public void onStart() {
    }

    @Override
    public void onCancel() {
    }

    @Override
    public void onEnd() {
    }

    @Override
    public void onException(Exception e) {
    }

    protected abstract void onSuccess(T succeed);

    protected abstract void onFailed(String failed);
}
