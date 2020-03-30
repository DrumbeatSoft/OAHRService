package com.drumbeat.hrlib;

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
import android.os.Message;
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

import com.alibaba.fastjson.JSONObject;
import com.ess.filepicker.FilePicker;
import com.ess.filepicker.model.EssFile;
import com.ess.filepicker.util.Const;
import com.huantansheng.easyphotos.EasyPhotos;
import com.huantansheng.easyphotos.callback.SelectCallback;
import com.huantansheng.easyphotos.models.album.entity.Photo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.Response;
import top.zibin.luban.Luban;

public class HRActivity extends AppCompatActivity {
    private BaseWebView webView;
    private FrameLayout flContainer;

    private CustomLoading customLoading;

    private final static int MSG_WHAT_SELECT_ALBUM = 101;
    private final static int MSG_WHAT_SHOW_LOADING = 102;
    private final static int MSG_WHAT_SELECT_ALBUM_ERROR = 103;//从相册选择图片失败
    //读写权限
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    //跳转第三方的文件管理
    private final static int REQUEST_CODE_FROM_ACTIVITY = 1000;
    //请求状态码
    public final static int REQUEST_CODE_FROM = 1001;
    private final static int REQUEST_CODE_SELECT_CONSIGNOR = 1002;
    private final static int REQUEST_PERMISSION_CODE = 101;

    public static final int RESULT_CODE_OK = 10000;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_WHAT_SELECT_ALBUM:
                    selectAlbum(msg.arg1);
                    break;
                case MSG_WHAT_SHOW_LOADING:
                    showLoading();
                    break;
                case MSG_WHAT_SELECT_ALBUM_ERROR:
                    showToastLong("操作失败，请稍后重试");
                    hideLoading();
                    break;
            }
        }
    };

    private String callback;
    private int index;
    private String hrToken;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        AndroidBug5497Workaround.assistActivity(this);
        setContentView(R.layout.activity_hr);

        Window window = getWindow();
        View decorView = window.getDecorView();
        int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        decorView.setSystemUiVisibility(option);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }

        flContainer = findViewById(R.id.flContainer);
        webView = findViewById(R.id.webView);
        Watermark.getInstance().show(flContainer, getIntent().getExtras().getString("watermarkStr"));
        customLoading = new CustomLoading(this);
        initWebView();
    }

    @SuppressLint("JavascriptInterface")
    private void initWebView() {
        hrToken = getIntent().getExtras().getString("hrToken");
        //final String URL = "http://192.168.70.35:8088/#/attendance-management?hrToken=" + hrToken;
        final String URL = "http://192.168.70.95:8088/#/attendance-management?hrToken=" + hrToken;

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

    public void showToastLong(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG);
    }

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
    private void FormJsMethod(final String methodName, final JSONObject param) {
        Runnable callbackRunnable = new Runnable() {
            @Override
            public void run() {
                String jsCode = "javascript:" + methodName + "(" + param + "" + ")";
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
//                        FormJsMethod("SubmitAssociationForm", jsonObject);
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
                    Toast.makeText(getApplicationContext(), "小可爱！没有选择pdf文件", Toast.LENGTH_SHORT).show();
                } else {
                    ArrayList<EssFile> essFileList = data.getParcelableArrayListExtra(Const.EXTRA_RESULT_SELECTION);
                    Toast.makeText(getApplicationContext(), "选中了" + essFileList.size() + "个文件" + essFileList.toString(), Toast.LENGTH_SHORT).show();
                    List<String> list = new ArrayList<>();
                    for (int i = 0; i < essFileList.size(); i++) {
                        list.add(essFileList.get(i).getAbsolutePath());
                    }
                    Toast.makeText(getApplicationContext(), "selected " + list.size(), Toast.LENGTH_SHORT).show();
                    UploadMessageFiles(list);
                }
                break;
            case REQUEST_CODE_SELECT_CONSIGNOR:
                if (resultCode == RESULT_CODE_OK) {
                    finish();
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
        mHandler.sendEmptyMessage(MSG_WHAT_SHOW_LOADING);
        new Thread(new Runnable() {
            @Override
            public void run() {
                MultipartBody.Part[] files = new MultipartBody.Part[pdfPaths.size()];
                for (int index = 0; index < pdfPaths.size(); index++) {
                    File file = new File(pdfPaths.get(index));
                    RequestBody requestBody = RequestBody.create(MediaType.parse("application/pdf"), file);
                    MultipartBody.Part mul = MultipartBody.Part.createFormData("files", file.getName(), requestBody);
                    files[index] = mul;
                }

                OkHttpUtil.getInstance().upload("", hrToken, files, new OkHttpUtil.NetCall() {
                    @Override
                    public void success(Call call, Response response) throws IOException {

                    }

                    @Override
                    public void failed(Call call, IOException e) {

                    }
                });
            }
        }).run();

    }

    /**
     * 从相册中选择图片
     */
    private void selectAlbum(int count) {
        EasyPhotos.createAlbum(this, true, ImageEngineForEasyPhotos.getInstance())
                .setFileProviderAuthority(this.getApplication().getPackageName() + ".fileProvider")
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
     * 压缩并上传图片
     *
     * @param imgPaths
     */
    private void uploadImg(final List<String> imgPaths) {
        mHandler.sendEmptyMessage(MSG_WHAT_SHOW_LOADING);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    MultipartBody.Part[] files = new MultipartBody.Part[imgPaths.size()];

                    //压缩
                    List<File> fileList = Luban.with(HRActivity.this)
                            .load(imgPaths)
                            .ignoreBy(100)//压缩阈值，小于100K的图片不压缩
                            .setTargetDir(HRActivity.this.getApplication().getCacheDir().getAbsolutePath())
                            .get();

                    //包装为MultipartBody.Part
                    for (int index = 0; index < fileList.size(); index++) {
                        File file = fileList.get(index);
                        RequestBody requestBody = RequestBody.create(MediaType.parse("image/png"), file);
                        MultipartBody.Part mul = MultipartBody.Part.createFormData("files", file.getName(), requestBody);
                        files[index] = mul;
                    }

                    //上传
                    OkHttpUtil.getInstance().upload("", hrToken, files, new OkHttpUtil.NetCall() {
                        @Override
                        public void success(Call call, Response response) throws IOException {

                        }

                        @Override
                        public void failed(Call call, IOException e) {

                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                    mHandler.sendEmptyMessage(MSG_WHAT_SELECT_ALBUM_ERROR);
                }
            }
        }).start();
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

    @Override
    public void onBackPressed() {

        webView.goBack();
//        if (webView.canGoBack()) {
//            webView.goBack();
//        } else {
//            finish();
//        }
    }

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

    @JavascriptInterface
    public void selectAlbum(String callback, int count, int index) {
        this.callback = callback;
        this.index = index;
        Message msg = new Message();
        msg.what = MSG_WHAT_SELECT_ALBUM;
        msg.arg1 = count;
        mHandler.sendMessage(msg);
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
     */
    @JavascriptInterface
    public void ringUp(String phone) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
        startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }
}
