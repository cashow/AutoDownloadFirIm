package com.cashow.autodownloadfir.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.CheckBox;

import com.cashow.autodownloadfir.R;
import com.cashow.autodownloadfir.main.model.FirInfo;
import com.cashow.autodownloadfir.main.model.FirInfoList;
import com.cashow.autodownloadfir.webview.WebviewActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements MainContact.View {
    @BindView(R.id.layout_info_list)
    RecyclerView recyclerView;
    @BindView(R.id.checkbox_auto_download)
    CheckBox checkboxAutoDownload;

    private MainPresenter presenter;

    private MainAdapter mainAdapter;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        context = getApplicationContext();
        presenter = new MainPresenter(context, this);

        initRecyclerView();
        restoreInfoList();
    }

    private void initRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(linearLayoutManager);

        mainAdapter = new MainAdapter(context);
        recyclerView.setAdapter(mainAdapter);
    }

    /**
     * 退出页面时保存当前的配置
     */
    @Override
    protected void onPause() {
        super.onPause();
        presenter.saveInfoList(getCurrentInfoList());
    }

    /**
     * 恢复之前保存的链接
     */
    private void restoreInfoList() {
        FirInfoList firInfoList = presenter.getSavedInfoList();
        mainAdapter.setFirInfoList(firInfoList);
        checkboxAutoDownload.setChecked(firInfoList.isAutoDownload);
        recyclerView.post(() -> {
            if (firInfoList.isAutoDownload) {
                startWebviewActivity();
            }
        });
    }

    @OnClick(R.id.button)
    void onNextClick() {
        startWebviewActivity();
    }

    @OnClick(R.id.button_new_link)
    void onNewLinkClick() {
        FirInfo firInfo = new FirInfo("", true, "");
        mainAdapter.addFirInfo(firInfo);
    }

    private FirInfoList getCurrentInfoList() {
        FirInfoList firInfoList = mainAdapter.getFirInfoList();
        firInfoList.isAutoDownload = checkboxAutoDownload.isChecked();
        return firInfoList;
    }

    /**
     * 打开 webview 页面
     */
    private void startWebviewActivity() {
        Intent intent = new Intent(this, WebviewActivity.class);
        intent.putExtra("url", "https://fir.im/" + mainAdapter.getCurrentFirInfo().name);
        intent.putExtra("password", mainAdapter.getCurrentFirInfo().password);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK || requestCode != 0)
            return;
        // webview 页面获取到的下载链接
        String url = data.getStringExtra("download_url");
        // 开始下载
        presenter.startDownload(url);
    }

    @Override
    public void startInstallIntent(Intent intent) {
        startActivity(intent);
    }
}
