package com.cashow.autodownloadfir.main;

import android.content.Intent;

import com.cashow.autodownloadfir.main.model.FirInfoList;

public interface MainContact {
    interface View {
        void startInstallIntent(Intent intent);
    }
    interface Presenter {

        void startDownload(String url);

        FirInfoList getSavedInfoList();

        void saveInfoList(FirInfoList firInfoList);
    }
}
