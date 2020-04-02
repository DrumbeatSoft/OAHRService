package com.drumbeat.hrservice.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.drumbeat.hrservice.R;
import com.drumbeat.hrservice.net.DataObject;
import com.drumbeat.hrservice.net.JsonConverter;
import com.drumbeat.hrservice.util.AndroidBug5497Workaround;
import com.drumbeat.hrservice.util.ImageEngineForEasyPhotos;
import com.drumbeat.hrservice.util.LogUtils;
import com.drumbeat.hrservice.util.Watermark;
import com.ess.filepicker.FilePicker;
import com.ess.filepicker.model.EssFile;
import com.ess.filepicker.util.Const;
import com.huantansheng.easyphotos.EasyPhotos;
import com.huantansheng.easyphotos.callback.SelectCallback;
import com.huantansheng.easyphotos.models.album.entity.Photo;
import com.yanzhenjie.kalle.Kalle;
import com.yanzhenjie.kalle.simple.SimpleCallback;
import com.yanzhenjie.kalle.simple.SimpleResponse;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
    private final static int REQUEST_PERMISSION_CODE = 101;
    //    private final static String BASE_URL = "http://192.168.71.8:8866/";//测试
    //    private final static String BASE_URL_H5 = "http://192.168.70.35:8088/#/attendance-management?hrToken=";//测试 雷鸣
    //    private final static String BASE_URL_H5 = "http://192.168.70.95:8088/#/attendance-management?hrToken=";//测试 李昂

    private final static String BASE_URL_H5 = "http://47.92.181.31:8822/#/attendance-management?hrToken=";//生产
    private final static String BASE_URL = "http://47.92.181.31:8866/"; //生产

    private final static String UPLOAD_CONTENT_FILE = "flowable/contentItem/upLoadContentFile";
    private String callback;
    private int index;
    private String hrToken;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hr_activity_hr);
        AndroidBug5497Workaround.assistActivity(this);

        Window window = getWindow();
        View decorView = window.getDecorView();
        int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        decorView.setSystemUiVisibility(option);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }

        flContainer = findViewById(R.id.hr_flContainer);
        webView = findViewById(R.id.hr_webView);
        Watermark.getInstance().show(flContainer, getIntent().getExtras().getString("watermarkStr"));
        customLoading = new CustomLoading(this);
        initWebView();
    }

    @SuppressLint("JavascriptInterface")
    private void initWebView() {
        hrToken = getIntent().getExtras().getString("hrToken");
        final String URL = BASE_URL_H5 + hrToken;
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
                webView.loadUrl(URL);
            }
        });
        webView.loadUrl(URL);
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

    /**
     * @param index
     */
    @JavascriptInterface
    public void TuneUpPdf(int index) {
        this.index = index;

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_PERMISSION_CODE);
            } else {
                FilePicker
                        .from(HRActivity.this)
                        .chooseForMimeType()
                        .isSingle()
                        .setFileTypes("pdf")
                        .requestCode(REQUEST_CODE_FROM_ACTIVITY)
                        .start();

            }
        }

    }

    /**
     * 执行JS方法
     *
     * @param methodName
     */
    private void loadJsMethod(final String methodName, final String param) {
        Runnable callbackRunnable = new Runnable() {
            @Override
            public void run() {
                String jsCode = "javascript:" + methodName + "(" + param + ")";
                LogUtils.debug("loadJsMethod:" + jsCode);
                webView.loadUrl(jsCode);
            }
        };
        if (Looper.getMainLooper() == Looper.myLooper()) {
            callbackRunnable.run();
        } else {
            new Handler(Looper.getMainLooper()).post(callbackRunnable);
        }
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
                    showToastLong("选中了" + list.size() + "个文件" + essFileList.toString());
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
            Kalle.post(BASE_URL + UPLOAD_CONTENT_FILE)
                    .addHeader("Authorization", "Bearer " + hrToken)
                    .files("files", fileList)
                    .converter(new JsonConverter())
                    .perform(new SimpleCallback<DataObject<List<String>>>() {
                        @Override
                        public void onResponse(SimpleResponse<DataObject<List<String>>, String> response) {
                            hideLoading();
                            if (response.isSucceed()) {
                                DataObject<List<String>> succeed = response.succeed();
                                List<String> fileIdList = succeed.getData();
                                if (fileIdList != null && fileIdList.size() > 0) {
                                    StringBuilder fileIdStr = new StringBuilder();
                                    for (String fileId : fileIdList) {
                                        fileIdStr.append(fileId).append(",");
                                    }
                                    loadJsMethod(callback, "'" + fileIdStr + "'," + index);
                                } else {
                                    uplodImgFile();
                                }
                            } else {
                                uplodImgFile();
                            }
                        }

                    });

        } catch (IOException e) {
            e.printStackTrace();
            uplodImgFile();
        }
    }

    /**
     * 上传图片失败，关闭加载框、提示错误信息
     */
    private void uplodImgFile() {
        showToastLong("上传失败请稍后重试");
        hideLoading();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE) {
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

    /**
     * 去选择PDF文件
     *
     * @param string
     */
    @JavascriptInterface
    public void fromShowPdfFile(String string) {
//        JSONObject jsonObject = JSONObject.parseObject(string);
//
//        String filename = jsonObject.getString("filename").replace(".pdf", "");
//        String fileurl = jsonObject.getString("fileurl");
//        Bundle bundle1 = new Bundle();
//        // 截取pdfName
//        if (!TextUtils.isEmpty(filename)) {
//            bundle1.putString("title", filename);
//        }
//        bundle1.putString("url", fileurl);
//        startActivity(PdfViewerActivity.class, bundle1);
    }

    /**
     * 从相册中选择图片
     *
     * @param callback
     * @param count
     * @param index
     */
    @JavascriptInterface
    public void selectAlbum(String callback, final int count, int index) {
        this.callback = callback;
        this.index = index;

        EasyPhotos.createAlbum(this, true, ImageEngineForEasyPhotos.getInstance())
                .setFileProviderAuthority(this.getApplication().getPackageName() + ".hrservice.fileProvider")
                .setCount(count)
                .setGif(false)//是否显示Gif图，默认不显示
                .setVideo(false)//是否显示视频，默认不显示
                .setPuzzleMenu(false)//是否显示拼图按钮，默认显示
                .setCleanMenu(true)//是否显示清空按钮，默认显示
                .start(new SelectCallback() {
                    @Override
                    public void onResult(ArrayList<Photo> photos, ArrayList<String> paths, boolean isOriginal) {
                        //上传图片
                        if (paths != null && paths.size() > 0) {
                            uploadImg(paths);
                        }
                    }
                });

    }

    /**
     * 返回上一页
     */
    @JavascriptInterface
    public void navBack() {
        finish();
    }

    /**
     * 打电话
     *
     * @param phone 手机号码
     */
    @JavascriptInterface
    public void ringUp(String phone) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
        startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }
}
