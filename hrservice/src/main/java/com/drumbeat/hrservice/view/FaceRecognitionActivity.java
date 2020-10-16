package com.drumbeat.hrservice.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.drumbeat.hrservice.R;
import com.drumbeat.zface.ZFace;
import com.drumbeat.zface.config.CameraConfig;
import com.drumbeat.zface.constant.ErrorCode;
import com.drumbeat.zface.listener.DownloadListener;
import com.drumbeat.zface.listener.QueryListener;
import com.drumbeat.zface.listener.RecognizeListener;
import com.drumbeat.zface.util.Logger;
import com.drumbeat.zface.util.PathUtils;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author ZhangYuhang
 * @describe 人脸识别
 * @date 2020/10/12
 * @updatelog
 */
public class FaceRecognitionActivity extends AppCompatActivity {

    private CustomLoading customLoading;
    private ImageView iv_face, iv_back;
    private Button btn_confirm;
    private Button btn_start_recognize;
    private float[] featureDataRegister;
    private String initZFaceResult;
    private Bitmap faceBitmap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hr_activity_face_recognition);
        Window window = getWindow();
        View decorView = window.getDecorView();
        int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        decorView.setSystemUiVisibility(option);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }

        Bundle extras = getIntent().getExtras();
        initZFaceResult = extras.getString("initZFaceResult");
        initView();
    }

    private void initView() {
        customLoading = new CustomLoading(this);
        iv_face = findViewById(R.id.iv_face);
        iv_back = findViewById(R.id.iv_back);
        btn_start_recognize = findViewById(R.id.btn_start_recognize);
        btn_confirm = findViewById(R.id.btn_confirm);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btn_start_recognize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveBitmapFile();
                Intent intent = new Intent();
                intent.putExtra("faceFeatureData", featureDataRegister);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        if (TextUtils.equals(initZFaceResult, ErrorCode.ERROR_NO_RESOURCE.toString())) {
            initZFace();
        }
    }

    /**
     * 查询是否需要下载资源文件
     */
    public void queryResource() {
        showLoading();
        ZFace.with(this).resource().query(new QueryListener() {
            @Override
            public void onSuccess(boolean needDownload) {
                if (needDownload) {
                    hideLoading();
                    ZFace.with(FaceRecognitionActivity.this).resource().download(new DownloadListener() {
                        @Override
                        public void onSuccess() {
                            showToastShort("资源文件下载成功,点击开始按钮识别");
                        }

                        @Override
                        public void onFailure(ErrorCode errorCode, String errorMsg) {
                            showToastShort("资源文件下载失败，错误码：" + errorCode);
                        }
                    });
                } else {
                    hideLoading();
                }
            }
        });
    }

    /**
     * 初始化zface
     */
    public void initZFace() {
        new AlertDialog.Builder(this).setMessage("识别功能需要下载资源包,请等待下载完成")
                .setPositiveButton("同意", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        queryResource();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).create().show();

    }

    /**
     * 开始识别
     */
    public void register() {
        ZFace.with(this)
                .recognizer()
                .recognize(new RecognizeListener() {
                    @Override
                    public void onSuccess(float[] featureData, byte[] faceData) {
                        if (featureData != null && featureData.length > 0) {
                            featureDataRegister = new float[featureData.length];
                            System.arraycopy(featureData, 0, featureDataRegister, 0, featureData.length);
                            showToastShort("人脸识别完成，已取得人脸特征数据");
                            Logger.i("人脸识别完成，已取得人脸特征数据");

                            faceBitmap = convertBitmap(270, bytes2Bitmap(faceData));
                            Glide.with(FaceRecognitionActivity.this)
                                    .asBitmap()
                                    .load(faceBitmap)
                                    .transform(new CenterCrop(), new RoundedCorners(8))
                                    .into(iv_face);

                        }
                    }

                    @Override
                    public void onFailure(ErrorCode errorCode, String errorMsg) {
                        showToastShort("人脸识别失败，错误码：" + errorCode);
                    }
                });
    }

    /**
     * 保存图片
     */
    private void saveBitmapFile() {
        BufferedOutputStream bos = null;
        // 创建文件
        try {
            File faceFile = new File(PathUtils.getExternalAppDataPath(FaceRecognitionActivity.this), "/face.jpg");
            bos = new BufferedOutputStream(new FileOutputStream(faceFile));
            faceBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);//0压缩100%，100不压缩
            bos.flush();
            bos.close();
        } catch (IOException e) {
            showToastShort("保存失败，请重新识别");
        } finally {
            try {
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Bytes to bitmap.
     *
     * @param data The bytes.
     * @return bitmap
     */
    public static Bitmap bytes2Bitmap(final byte[] data) {
        Bitmap bitmap;
        YuvImage yuvimage = new YuvImage(data, ImageFormat.NV21, CameraConfig.getInstance().getCameraPreviewWidth(), CameraConfig.getInstance().getCameraPreviewHeight(), null); //20、20分别是图的宽度与高度
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        yuvimage.compressToJpeg(new Rect(0, 0, CameraConfig.getInstance().getCameraPreviewWidth(), CameraConfig.getInstance().getCameraPreviewHeight()), 80, baos);//80--JPG图片的质量[0-100],100最高
        byte[] jdata = baos.toByteArray();
        bitmap = BitmapFactory.decodeByteArray(jdata, 0, jdata.length);

        return bitmap;
    }

    /**
     * 拍出来的是旋转90度加左右颠倒的
     */
    public Bitmap convertBitmap(int angle, Bitmap bmp) {
        int w = bmp.getWidth();
        int h = bmp.getHeight();
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);//旋转
        matrix.postScale(-1, 1); // 镜像水平翻转

        return Bitmap.createBitmap(bmp, 0, 0, w, h, matrix, true);

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
     */
    public void showToastShort(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

}
