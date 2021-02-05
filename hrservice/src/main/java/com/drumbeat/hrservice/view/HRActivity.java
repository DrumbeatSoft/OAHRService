package com.drumbeat.hrservice.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.drumbeat.hrservice.R;
import com.drumbeat.hrservice.net.DataObject;
import com.drumbeat.hrservice.net.JsonConverter;
import com.drumbeat.hrservice.net.KalleCallback;
import com.drumbeat.hrservice.util.DataHelper;
import com.drumbeat.hrservice.util.GZIPCompressUtils;
import com.drumbeat.hrservice.util.LogUtils;
import com.drumbeat.hrservice.util.PictureHelper;
import com.drumbeat.hrservice.util.Watermark;
import com.drumbeat.hrservice.util.WebViewUtil;
import com.drumbeat.zface.util.PathUtils;
import com.ess.filepicker.FilePicker;
import com.ess.filepicker.model.EssFile;
import com.ess.filepicker.util.Const;
import com.luck.picture.lib.entity.LocalMedia;
import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.yanzhenjie.kalle.Kalle;
import com.yanzhenjie.kalle.download.Callback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import top.zibin.luban.Luban;

public class HRActivity extends AppCompatActivity {
    private BaseWebView webView;
    private FrameLayout flContainer;
    private CustomLoading customLoading;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private final static int REQUEST_CODE_FROM_ACTIVITY = 1000;
    public final static int REQUEST_CODE_FROM = 1001;
    private final static int REQUEST_PERMISSION_FILE_PICK_CODE = 101;
    private final static int REQUEST_PERMISSION_FILE_DOWNLOAD_CODE = 102;
    private final static int REQUEST_FACE_CODE = 1002;

    private String UPLOAD_IMAGE_FILE = "flowable/contentItem/upLoadContentFile";
    private String UPLOAD_PDF_FILE = "organization/file/uploadFiles";

    private String callback;
    private int index;
    private int count;
    private String hrToken;
    private String baseUrl;
    private String baseUrlH5;
    private float[] faceFeatureData;//人脸特征值

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hr_activity_hr);

        Window window = getWindow();
        View decorView = window.getDecorView();
        int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        decorView.setSystemUiVisibility(option);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }

        Bundle extras = getIntent().getExtras();
        baseUrl = getIntent().getExtras().getString("baseUrl");
        baseUrlH5 = getIntent().getExtras().getString("baseUrlH5");
        hrToken = getIntent().getExtras().getString("hrToken");
        String watermarkStr = extras.getString("watermarkStr");

        LogUtils.debug("Bundle:" + extras.toString());

        flContainer = findViewById(R.id.hr_flContainer);
        webView = findViewById(R.id.hr_webView);
        customLoading = new CustomLoading(this);
        Watermark.getInstance().show(flContainer, watermarkStr);

        WebViewUtil.clearCache(this);

        initWebView();
    }

    @SuppressLint("JavascriptInterface")
    private void initWebView() {
        WebSettings webSettings = webView.getSettings();
        webSettings.setAllowContentAccess(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);     //设置js可以直接打开窗口，如window.open()，默认为false
        webSettings.setJavaScriptEnabled(true);     //是否允许执行js，默认为false。设置true时，会提醒可能造成XSS漏洞
        webSettings.setSupportZoom(true);           //是否可以缩放，默认true
        webSettings.setAllowFileAccess(true);       // 设置允许访问文件数据
        webSettings.setBuiltInZoomControls(false);   //是否显示缩放按钮，默认false
        webSettings.setUseWideViewPort(true);       //设置此属性，可任意比例缩放。大视图模式
        webSettings.setLoadWithOverviewMode(true);  //和setUseWideViewPort(true)一起解决网页自适应问题
        webSettings.setAppCacheEnabled(true);       //是否使用缓存
        webSettings.setDomStorageEnabled(true);     //DOM Storage

        webView.addJavascriptInterface(HRActivity.this, "androidOA");

        webView.setWebViewListener(new BaseWebView.WebViewListener() {
            @Override
            public void onPageFinished(WebView view, String url) {
                hideLoading();
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                showLoading();
            }

            @Override
            public void onErrorClick() {
                showLoading();
                webView.loadUrl(baseUrlH5);
            }
        });

        webView.loadUrl(baseUrlH5);

        LogUtils.debug(baseUrlH5);
    }

    /**
     * 显示圆形进度条
     */
    public void showLoading() {
        if (!customLoading.isShowing()) {
            customLoading.show();
        }
    }

    /**
     * 隐藏圆形进度条
     */
    public void hideLoading() {
        if (customLoading != null && customLoading.isShowing()) {
            customLoading.dismiss();
        }
    }

    /**
     * Toast
     *
     * @param msg
     */
    public void showToastLong(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG);
    }


    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.destroy();
        }
        super.onDestroy();
    }

    /**
     * H5返回上一页
     */
    @JavascriptInterface
    public void navBack() {
        finish();
    }

    /**
     * H5获取HR token
     *
     * @return
     */
    @JavascriptInterface
    public String getHrToken() {
        return hrToken;
    }

    /**
     * H5选择PDF
     */
    @JavascriptInterface
    public void TuneUpPdf(int count) {
        this.count = count;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_PERMISSION_FILE_PICK_CODE);
            } else {
                FilePicker
                        .from(HRActivity.this)
                        .chooseForMimeType()
                        .setMaxCount(count)
                        .setFileTypes("pdf")
                        .requestCode(REQUEST_CODE_FROM_ACTIVITY)
                        .start();

            }
        }

    }

    /**
     * H5选择PDF
     */
    @JavascriptInterface
    public void TuneUpPdf(String interfaceName, int count) {
        this.UPLOAD_PDF_FILE = interfaceName;
        this.count = count;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_PERMISSION_FILE_PICK_CODE);
            } else {
                FilePicker
                        .from(HRActivity.this)
                        .chooseForMimeType()
                        .setMaxCount(count)
                        .setFileTypes("pdf")
                        .requestCode(REQUEST_CODE_FROM_ACTIVITY)
                        .start();

            }
        }

    }

    /**
     * H5预览PDF文件
     */
    @JavascriptInterface
    public void showPdfFile(String fileName, String fileUrl) {
        this.fileName = fileName;
        this.fileUrl = fileUrl;
        requestPermission();
    }

    /**
     * H5选择图片
     *
     * @param callback
     * @param count
     * @param index
     */
    @JavascriptInterface
    public void selectAlbum(String callback, final int count, int index) {
        this.callback = callback;
        this.index = index;
        this.count = count;

        handlerUI.sendEmptyMessage(1);
    }

    /**
     * H5选择图片
     *
     * @param callback
     * @param count
     * @param index
     */
    @JavascriptInterface
    public void selectAlbum(String interfaceName, String callback, final int count, int index) {
        this.UPLOAD_IMAGE_FILE = interfaceName;
        this.callback = callback;
        this.index = index;
        this.count = count;

        handlerUI.sendEmptyMessage(1);
    }

    /**
     * H5打电话
     *
     * @param phone 手机号码
     */
    @JavascriptInterface
    public void ringUp(String phone) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
        startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    /**
     * 执行JS方法
     *
     * @param methodName
     */
    private void loadJsMethod(final String methodName, final String param) {
        webView.post(new Runnable() {
            @Override
            public void run() {
                String jsCode = "javascript:" + methodName + "(" + param + ")";
                LogUtils.debug("loadJsMethod:" + jsCode);
                webView.loadUrl(jsCode);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_FROM:
//                if (resultCode == FormAssociationActivity.FORM_BACK) {
//                    if (data != null) {
//                        String formback = "";
//                        selectBusiness = data.getStringArrayListExtra("selectBusiness");
//                        for (int i = 0; i < selectBusiness.size(); i++) {
//                            if (TextUtils.isEmpty(formback)) {
//                                formback = selectBusiness.get(i);
//                            } else {
//                                formback = formback + "," + selectBusiness.get(i);
//                            }
//                        }
//                        String Stringjson = "{\"processInstanceId\":\"" + formback + "\"}";
//                        JSONObject jsonObject = JSONObject.parseObject(Stringjson);
//                        loadJsMethod("SubmitAssociationForm", jsonObject);
//
//                        if (selectBusiness.size() == 0) {
//                            showToastLong("小可爱，没有选择关联表单！");
//                            selectBusiness.clear();
//                        }
//                    }
//                }

                break;
            case REQUEST_CODE_FROM_ACTIVITY:
                if (data == null) {
                    showToastLong("小可爱！没有选择pdf文件");
                } else {
                    ArrayList<EssFile> essFileList = data.getParcelableArrayListExtra(Const.EXTRA_RESULT_SELECTION);
                    List<String> list = new ArrayList<>();
                    for (int i = 0; i < essFileList.size(); i++) {
                        list.add(essFileList.get(i).getAbsolutePath());
                    }
                    UploadMessageFiles(list);
                }
                break;

            case REQUEST_FACE_CODE:
                if (data != null) {
                    faceFeatureData = data.getFloatArrayExtra("faceFeatureData");
                    sendFaceDataToH5();
                }
                break;

        }

    }

    /**
     * 上传pdf文件
     *
     * @param pdfPaths
     */
    private void UploadMessageFiles(final List<String> pdfPaths) {

        showLoading();

        List<File> fileList = new ArrayList<>();
        for (int i = 0; i < pdfPaths.size(); i++) {
            fileList.add(new File(pdfPaths.get(i)));
        }
        Kalle.post(baseUrl + UPLOAD_PDF_FILE)
                .addHeader("Authorization", "Bearer " + hrToken)
                .files("files", fileList)
                .converter(new JsonConverter())
                .perform(new KalleCallback<DataObject<String>>() {
                    @Override
                    protected void onSuccess(DataObject<String> succeed) {
                        hideLoading();
                        if (!TextUtils.isEmpty(succeed.getData())) {
                            loadJsMethod("getPDF", "'" + succeed.getData() + "'");
                        } else {
                            uploadFileFailed();
                        }
                    }

                    @Override
                    protected void onFailed(String failed) {
                        uploadFileFailed();
                    }
                });
    }


    /**
     * 压缩并上传图片
     *
     * @param imgPaths 图片路径集合
     */
    private void uploadImg(final List<String> imgPaths) {
        showLoading();
        try {
            List<File> fileList = Luban.with(HRActivity.this)
                    .load(imgPaths)
                    .ignoreBy(100)
                    .setTargetDir(HRActivity.this.getApplication().getCacheDir().getAbsolutePath())
                    .get();
            Kalle.post(baseUrl + UPLOAD_IMAGE_FILE)
                    .addHeader("Authorization", "Bearer " + hrToken)
                    .files("files", fileList)
                    .converter(new JsonConverter())
                    .perform(new KalleCallback<DataObject<List<String>>>() {
                        @Override
                        protected void onSuccess(DataObject<List<String>> succeed) {
                            hideLoading();
                            List<String> fileIdList = succeed.getData();
                            if (fileIdList != null && fileIdList.size() > 0) {
                                StringBuilder fileIdStr = new StringBuilder();
                                for (String fileId : fileIdList) {
                                    fileIdStr.append(fileId).append(",");
                                }
                                loadJsMethod(callback, "'" + fileIdStr + "'," + index);
                            } else {
                                uploadFileFailed();
                            }
                        }

                        @Override
                        protected void onFailed(String failed) {
                            uploadFileFailed();
                        }
                    });

        } catch (IOException e) {
            e.printStackTrace();
            uploadFileFailed();
        }
    }

    /**
     * 上传图片失败，关闭加载框、提示错误信息
     */
    private void uploadFileFailed() {
        hideLoading();
        showToastLong("上传失败，请稍后重试");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_FILE_PICK_CODE) {
            if (grantResults[0] == 0) {
                FilePicker
                        .from(HRActivity.this)
                        .chooseForMimeType()
                        .isSingle()
                        .setFileTypes("pdf")
                        .requestCode(REQUEST_CODE_FROM_ACTIVITY)
                        .start();
            }
        }
        if (requestCode == REQUEST_PERMISSION_FILE_DOWNLOAD_CODE) {
            if (grantResults[0] == 0) {
                downloadPdf();
            }
        }

    }

    /**
     * 返回事件全部交给H5来处理，除非页面报错
     */
    @Override
    public void onBackPressed() {
        if (webView.isSuccess()) {
            loadJsMethod("androidBack", "");
        } else {
            finish();
        }
    }


    private Handler handlerUI = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == 1) {
                PictureHelper.selectAlbum(HRActivity.this, count, null, new PictureHelper.OnSelectListener() {
                    @Override
                    public void selected(List<LocalMedia> data) {
                        //上传图片
                        if (data != null && data.size() > 0) {

                            ArrayList<String> paths = new ArrayList<>();
                            //安卓9以上该框架为兼容path返回的是uri，取realpath，9以下取path
                            if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                                for (LocalMedia localMedia : data) {
                                    paths.add(localMedia.getRealPath());
                                }
                            } else {
                                for (LocalMedia localMedia : data) {
                                    paths.add(localMedia.getPath());
                                }
                            }

                            uploadImg(paths);
                        }
                    }
                });
            }

            return false;
        }
    });

    //*****************************人脸识别

    /**
     * H5点击调用android识别
     *
     * @return
     */
    @JavascriptInterface
    public void faceRecognition(String imgBase64) {
        DataHelper.getInstance().saveData("oldHeader",imgBase64);
        Intent intent = new Intent();
        intent.setClass(HRActivity.this, FaceRecognitionActivity.class);
//        intent.putExtra("oldHeader", imgBase64);
        startActivityForResult(intent, REQUEST_FACE_CODE);
    }

    /**
     * 给H5传递人脸参数
     */
    private void sendFaceDataToH5() {
        String faceData = floatArray2String(faceFeatureData);
        File faceFile = new File(PathUtils.getExternalAppDataPath(HRActivity.this), "/face.jpg");
        String faceFileBase64 = file2Base64(faceFile);
        if (TextUtils.isEmpty(faceData) || TextUtils.isEmpty(faceFileBase64)) {
            showToastLong("照片获取失败，请重新识别");
        } else {
            loadJsMethod("androidFaceData", "'" + faceData + "'");
            loadJsMethod("androidFaceBase64", "'" + faceFileBase64 + "'");
        }


    }

    /**
     * file转base64 获取人脸图片文件的base64
     */
    private String file2Base64(File file) {
        if (file == null) {
            return null;
        }
        String base64 = null;
        FileInputStream fin = null;
        try {
            fin = new FileInputStream(file);
            byte[] buff = new byte[fin.available()];
            fin.read(buff);
            base64 = Base64.encodeToString(buff, Base64.NO_WRAP);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fin != null) {
                try {
                    fin.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return base64;
    }

    /**
     * float数组转string
     */
    private String floatArray2String(float[] floats) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < floats.length; i++) {
            stringBuilder.append(floats[i] + ",");
        }
        return GZIPCompressUtils.compress(stringBuilder.toString());//压缩

    }

    //*****************************预览pdf
    /**
     * PDF预览
     */
    private String fileName, fileUrl;

    private void requestPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_PERMISSION_FILE_DOWNLOAD_CODE);
        } else {
            webView.post(new Runnable() {
                @Override
                public void run() {
                    downloadPdf();
                }
            });

        }
    }

    /**
     * 下载pdf
     */
    private void downloadPdf() {

        String fileDir = PathUtils.getExternalAppDataPath(HRActivity.this) + "/files";
        File dir = new File(fileDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        if (TextUtils.isEmpty(fileName)) {
            fileName = fileUrl.substring(fileUrl.lastIndexOf("/"));
        }
        Kalle.Download.get(fileUrl)// /sdcard
                .addHeader("Authorization", "Bearer " + hrToken)
                .directory(fileDir)// /RingTone
                .fileName(fileName)
                .perform(new Callback() {
                    @Override
                    public void onStart() {
                        showLoading();
                        LogUtils.debug("请求开始了");
                    }

                    @Override
                    public void onFinish(String path) {
                        openFileReader(HRActivity.this, path);
                    }

                    @Override
                    public void onException(Exception e) {
                        hideLoading();
                        showToastLong("请重新点击预览");
                    }

                    @Override
                    public void onCancel() {
                        hideLoading();
                    }

                    @Override
                    public void onEnd() {
                        hideLoading();
                        LogUtils.debug("请求结束了");
                    }
                });

    }

    /**
     * 预览pdf
     */
    public void openFileReader(Context context, String pathName) {

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("local", "true");
        JSONObject Object = new JSONObject();
        try {
            Object.put("pkgName", context.getApplicationContext().getPackageName());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.put("menuData", Object.toString());
        QbSdk.getMiniQBVersion(context);
        int ret = QbSdk.openFileReader(context, pathName, params, new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String s) {
                Log.d("test", "onReceiveValue,val =" + s);
            }
        });

    }
}