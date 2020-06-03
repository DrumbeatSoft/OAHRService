package com.drumbeat.hrservice.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.drumbeat.hrservice.R;
import com.drumbeat.hrservice.util.LogUtils;
import com.tencent.smtt.export.external.interfaces.SslError;
import com.tencent.smtt.export.external.interfaces.SslErrorHandler;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

/**
 * 带进度条的WebView
 * wtq 2016年6月28日14:09:46
 */
public class BaseWebView extends WebView {
    private boolean isSuccess = false;
    private boolean isError = false;
    private WebViewListener mWebViewListener;

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setWebViewListener(WebViewListener mWebViewListener) {
        this.mWebViewListener = mWebViewListener;
    }

    @SuppressLint("SetJavaScriptEnabled")
    public BaseWebView(Context context, AttributeSet attrs) {
        super(context, attrs);

        final LinearLayout errorLayout = getErrorLayout(context);
        addView(errorLayout);

        getSettings().setJavaScriptEnabled(true);
        getSettings().setBlockNetworkImage(false);

        setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    LogUtils.debug("onReceivedTitle:" + title);
                    if (title.contains("404") || title.contains("500") || title.contains("Error") || title.contains("找不到网页") || title.contains("网页无法打开")) {
                        isError = true;
                        isSuccess = false;
                    }
                }
            }
        });

        setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                if (mWebViewListener != null) {
                    mWebViewListener.onPageStarted(view, url, favicon);
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (Build.VERSION.SDK_INT < 26) {
                    view.loadUrl(url);
                    return true;
                }
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (mWebViewListener != null) {
                    mWebViewListener.onPageFinished(view, url);
                }

                if (!isError) {
                    isSuccess = true;
                }
                isError = false;
                if (isSuccess) {
                    errorLayout.setVisibility(GONE);
                } else {
                    errorLayout.setVisibility(VISIBLE);
                }
            }

            @Override
            public void onReceivedError(WebView webView, int i, String s, String s1) {
                super.onReceivedError(webView, i, s, s1);
                isError = true;
                isSuccess = false;
            }

            @Override
            public void onReceivedHttpError(WebView webView, WebResourceRequest webResourceRequest, WebResourceResponse webResourceResponse) {
                super.onReceivedHttpError(webView, webResourceRequest, webResourceResponse);
                int statusCode = webResourceResponse.getStatusCode();
                LogUtils.debug("onReceivedHttpError:" + statusCode);
                if (404 == statusCode || 500 == statusCode) {
                    isError = true;
                    isSuccess = false;
                }
            }

            @Override
            public void onReceivedSslError(WebView webView, SslErrorHandler sslErrorHandler, SslError sslError) {
                super.onReceivedSslError(webView, sslErrorHandler, sslError);
                isError = true;
                isSuccess = false;
            }
        });
    }

    private LinearLayout getErrorLayout(Context context) {
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setGravity(Gravity.CENTER);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.hr_white));
        linearLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        ImageView imageView = new ImageView(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.width = 200;
        params.height = 200;
        params.setMargins(0, 0, 0, 30);
        imageView.setLayoutParams(params);
        imageView.setImageResource(R.drawable.hr_ic_empty);
        linearLayout.addView(imageView);

        TextView textView = new TextView(context);
        textView.setText("数据加载失败，点击重试");
        textView.setTextColor(ContextCompat.getColor(context, R.color.hr_color_333333));
        textView.setTextSize(14);
        textView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mWebViewListener != null) {
                    mWebViewListener.onErrorClick();
                }
            }
        });
        textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        linearLayout.setVisibility(GONE);
        linearLayout.addView(textView);
        return linearLayout;
    }

    public interface WebViewListener {
        void onPageStarted(WebView view, String url, Bitmap favicon);

        void onPageFinished(WebView view, String url);

        void onErrorClick();
    }
}