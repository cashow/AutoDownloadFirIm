package com.cashow.autodownloadfir.webview;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.cashow.autodownloadfir.R;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.IOException;
import java.io.InputStream;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WebviewActivity extends AppCompatActivity {

    @BindView(R.id.webview)
    WebView webview;
    @BindView(R.id.progressbar)
    ProgressBar progressbar;

    private Context context;

    private String url;
    private String password;

    private boolean isFirstTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        ButterKnife.bind(this);

        context = getApplicationContext();

        url = getIntent().getStringExtra("url");
        password = getIntent().getStringExtra("password");

        initWebView();

        new RxPermissions(this).request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(granted -> {
                    if (granted) {
                        webview.loadUrl(url);
                    } else {
                        Toast.makeText(context, "请授予读写文件的权限", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void initWebView() {
        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDisplayZoomControls(false);
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                injectScriptFile(view, "my_script.js");

                if (!isFirstTime) {
                    isFirstTime = true;
                    if (!TextUtils.isEmpty(password)) {
                        login();
                    } else {
                        install();
                    }
                }
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                // this will ignore the Ssl error and will go forward to your site
                handler.proceed();
                error.getCertificate();
            }
        });

        webview.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                progressbar.setProgress(newProgress);
                if (newProgress < 100) {
                    if (progressbar.getVisibility() == View.GONE)
                        progressbar.setVisibility(View.VISIBLE);
                } else if (newProgress == 100) {
                    progressbar.setVisibility(View.GONE);
                }
                super.onProgressChanged(view, newProgress);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
            }
        });

        webview.setDownloadListener((url1, userAgent, contentDisposition, mimetype, contentLength) -> {
            webview.stopLoading();
            Intent intent = getIntent();
            intent.putExtra("download_url", url1);
            setResult(RESULT_OK, intent);
            finish();
        });

        WebView.setWebContentsDebuggingEnabled(true);
    }

    /**
     * 向 webview 注入自定义的 js
     * https://stackoverflow.com/questions/21552912/android-web-view-inject-local-javascript-file-to-remote-webpage
     */
    private void injectScriptFile(WebView view, String scriptFile) {
        InputStream input;
        try {
            input = getAssets().open(scriptFile);
            byte[] buffer = new byte[input.available()];
            input.read(buffer);
            input.close();

            // String-ify the script byte-array using BASE64 encoding !!!
            String encoded = Base64.encodeToString(buffer, Base64.NO_WRAP);
            view.loadUrl("javascript:(function() {" +
                    "var parent = document.getElementsByTagName('head').item(0);" +
                    "var script = document.createElement('script');" +
                    "script.type = 'text/javascript';" +
                    // Tell the browser to BASE64-decode the string into your script !!!
                    "script.innerHTML = window.atob('" + encoded + "');" +
                    "parent.appendChild(script)" +
                    "})()");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 填入密码进行登录
     */
    private void login() {
        webview.loadUrl("javascript:mAutoLoginAndInstall(\"" + password + "\")");
    }

    /**
     * 开始下载
     */
    private void install() {
        webview.loadUrl("javascript:mAutoLoginAndInstall(\"\")");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        webview.destroy();
    }
}
