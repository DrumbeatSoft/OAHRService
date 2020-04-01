package com.drumbeat.hrservice.net;

import com.alibaba.fastjson.JSONObject;
import com.drumbeat.hrservice.util.LogUtils;
import com.yanzhenjie.kalle.Response;
import com.yanzhenjie.kalle.simple.Converter;
import com.yanzhenjie.kalle.simple.SimpleResponse;

import java.lang.reflect.Type;

public class JsonConverter implements Converter {
    @Override
    public <S, F> SimpleResponse<S, F> convert(Type succeed, Type failed, Response response, boolean fromCache) throws Exception {

        S succeedData = null; // 业务成功的数据。
        F failedData = null; // 业务失败的数据。

        int code = response.code();
        String data = response.body().string();
        LogUtils.debug("convert data:" + data);
        LogUtils.debug("convert code:" + code);

        if (code >= 200 && code < 300) { // Http请求成功。
            DataObject httpEntity;
            try {
                httpEntity = JSONObject.parseObject(data, DataObject.class);
            } catch (Exception e) {
                httpEntity = new DataObject();
                httpEntity.setCode(0);
                httpEntity.setDesc("服务器数据格式异常");
            }

            if (httpEntity.getCode() == 200) { // 服务端业务成功。
                try {
                    if (succeed == Integer.class) {
                        Integer succeedInt = Integer.parseInt(data);
                        succeedData = (S) succeedInt;
                    } else if (succeed == Long.class) {
                        Long succeedLong = Long.parseLong(data);
                        succeedData = (S) succeedLong;
                    } else if (succeed == String.class) {
                        succeedData = (S) data;
                    } else if (succeed == Boolean.class) {
                        Boolean succeedBoolean = Boolean.parseBoolean(data);
                        succeedData = (S) succeedBoolean;
                    } else if (succeed == JSONObject.class) {
                        JSONObject object = JSONObject.parseObject(data);
                        succeedData = (S) object;
                    } else {
                        succeedData = JSONObject.parseObject(data, succeed);
                    }
                } catch (Exception e) {
                    failedData = (F) "服务器数据格式异常";
                }
            } else {
                // 业务失败，获取服务端提示信息。
                failedData = (F) httpEntity.getDesc();
            }
        } else if (code >= 400 && code < 500) { // 客户端请求不符合服务端要求。
            failedData = (F) "发生未知异常";
        } else if (code >= 500) { // 服务端发生异常。
            failedData = (F) "服务器开小差啦";
        }

        // 包装成SimpleResponse返回。
        return SimpleResponse.<S, F>newBuilder()
                .code(response.code())
                .headers(response.headers())
                .fromCache(fromCache)
                .succeed(succeedData)
                .failed(failedData)
                .build();
    }
}
