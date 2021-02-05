package com.drumbeat.hrservice.util;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/**
 * intent传输大量数据报错时可以更换使用该单例
 * */
public class DataHelper {

    private static final DataHelper helper = new DataHelper();

    public static DataHelper getInstance() {
        return helper;
    }

    Map<String, WeakReference<Object>> data = new HashMap<String, WeakReference<Object>>();

    public void saveData(String id, Object object) {
        data.put(id, new WeakReference<Object>(object));
    }

    public Object getData(String id) {
        WeakReference<Object> objectWeakReference = data.get(id);
        return objectWeakReference.get();
    }
}
