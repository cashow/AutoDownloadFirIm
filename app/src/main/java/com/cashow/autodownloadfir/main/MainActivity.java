package com.cashow.autodownloadfir.main;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;

import com.cashow.autodownloadfir.R;
import com.cashow.autodownloadfir.main.model.FirInfo;
import com.cashow.autodownloadfir.main.model.FirInfoList;
import com.cashow.autodownloadfir.webview.WebviewActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements MainContact.View {
    @BindView(R.id.layout_info_list)
    ViewGroup layoutInfoList;
    @BindView(R.id.checkbox_auto_download)
    CheckBox checkboxAutoDownload;

    /**
     * 目前聚焦的 view
     */
    private View currentInfoView;

    private MainPresenter presenter;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        context = getApplicationContext();
        presenter = new MainPresenter(context, this);

        restoreInfoList();
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
        layoutInfoList.removeAllViews();
        FirInfoList firInfoList = presenter.getSavedInfoList();
        if (firInfoList == null || firInfoList.firInfoList.size() == 0) {
            layoutInfoList.addView(getFirInfoView(new FirInfo("", true, "")));
        } else {
            for (FirInfo firInfo : firInfoList.firInfoList) {
                layoutInfoList.addView(getFirInfoView(firInfo));
            }
        }
        checkboxAutoDownload.setChecked(firInfoList.isAutoDownload);
        layoutInfoList.post(() -> {
            if (firInfoList.isAutoDownload) {
                startWebviewActivity();
            }
        });
    }

    /**
     * 将 FirInfo 转成 View
     */
    private View getFirInfoView(FirInfo firInfo) {
        View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.layout_fir_info, layoutInfoList, false);
        EditText edittextName = view.findViewById(R.id.edittext_name);
        EditText edittextPassword = view.findViewById(R.id.edittext_password);
        View imageDelete = view.findViewById(R.id.image_delete);

        edittextName.setText(firInfo.name);
        edittextName.getBackground().mutate().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
        edittextName.setOnFocusChangeListener((v, hasFocus) -> {
            view.setSelected(hasFocus);
            if (hasFocus) {
                currentInfoView = view;
            }
        });

        edittextPassword.setText(firInfo.password);
        edittextPassword.getBackground().mutate().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
        edittextPassword.setOnFocusChangeListener((v, hasFocus) -> {
            view.setSelected(hasFocus);
            if (hasFocus) {
                currentInfoView = view;
            }
        });

        view.setOnClickListener(v -> edittextName.requestFocus());
        imageDelete.setOnClickListener(v -> layoutInfoList.removeView(view));
        if (firInfo.isFocused) {
            view.setSelected(true);
            edittextName.post(() -> edittextName.requestFocus());
        }
        return view;
    }

    @OnClick(R.id.button)
    void onNextClick() {
        startWebviewActivity();
    }

    @OnClick(R.id.button_new_link)
    void onNewLinkClick() {
        FirInfo firInfo = new FirInfo("", true, "");
        layoutInfoList.addView(getFirInfoView(firInfo));
    }

    private FirInfoList getCurrentInfoList() {
        FirInfoList firInfoList = new FirInfoList();
        for (int i = 0; i < layoutInfoList.getChildCount(); i++) {
            View view = layoutInfoList.getChildAt(i);
            EditText edittextName = view.findViewById(R.id.edittext_name);
            EditText edittextPassword = view.findViewById(R.id.edittext_password);
            String info = edittextName.getEditableText().toString();
            String password = edittextPassword.getEditableText().toString();
            if (!TextUtils.isEmpty(info)) {
                firInfoList.firInfoList.add(new FirInfo(info, (currentInfoView == view), password));
            }
        }
        firInfoList.isAutoDownload = checkboxAutoDownload.isChecked();
        return firInfoList;
    }

    /**
     * 打开 webview 页面
     */
    private void startWebviewActivity() {
        EditText editTextName = currentInfoView.findViewById(R.id.edittext_name);
        EditText editTextPassword = currentInfoView.findViewById(R.id.edittext_password);

        Intent intent = new Intent(this, WebviewActivity.class);
        intent.putExtra("url", "https://fir.im/" + editTextName.getEditableText().toString());
        intent.putExtra("password", editTextPassword.getEditableText().toString());
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
