package com.cashow.autodownloadfir.main;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.widget.Toast;

import com.cashow.autodownloadfir.main.model.FirInfoList;
import com.google.gson.Gson;

import java.io.File;

public class MainPresenter implements MainContact.Presenter {
    Context context;
    MainContact.View mView;

    private long mDownloadedFileID = -1;
    private boolean mIsUpdating;

    public MainPresenter(Context context, MainContact.View mView) {
        this.context = context;
        this.mView = mView;
    }

    @Override
    public void startDownload(String url) {
        if (mIsUpdating) {
            return;
        }
        mIsUpdating = true;
        Toast.makeText(context, "开始下载安装包", Toast.LENGTH_LONG).show();
        removePreviousAPK();
        beginDownload(url);
        registerInstallHandler();
    }

    /**
     * 从 SharedPreferences 里读取之前保存过的 fir 链接
     */
    @Override
    public FirInfoList getSavedInfoList() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("infolist", Context.MODE_PRIVATE);
        String firLinkListStr = sharedPreferences.getString("infolist", "");
        if (TextUtils.isEmpty(firLinkListStr)) {
            return new FirInfoList();
        } else {
            FirInfoList firInfoList = new Gson().fromJson(firLinkListStr, FirInfoList.class);
            return firInfoList;
        }
    }

    /**
     * 清空之前下载的安装包
     */
    private void removePreviousAPK() {
        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File[] previousAPKs = dir.listFiles((dir1, name) -> name.contains("auto_download_fir")
                && name.contains(".apk"));
        if (previousAPKs == null) {
            return;
        }
        for (File file : previousAPKs) {
            //noinspection ResultOfMethodCallIgnored 不用关心是否删除
            file.delete();
        }
    }

    /**
     * 将当前的 fir 链接保存到 SharedPreferences 里
     * @param firInfoList
     */
    @Override
    public void saveInfoList(FirInfoList firInfoList) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("infolist", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("infolist", new Gson().toJson(firInfoList));
        editor.commit();
    }

    private void beginDownload(String url) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "fir_download.apk");

        DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        mDownloadedFileID = manager.enqueue(request);
    }

    private void doInstall(File file) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            // 由于没有在Activity环境下启动Activity,设置下面的标签
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (Build.VERSION.SDK_INT >= 24) { //判读版本是否在7.0以上
                //参数1 上下文, 参数2 Provider主机地址 和配置文件中保持一致   参数3  共享的文件
                Uri apkUri =
                        FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file);
                //添加这一句表示对目标应用临时授权该Uri所代表的文件
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
            } else {
                intent.setDataAndType(Uri.fromFile(file),
                        "application/vnd.android.package-archive");
            }
            mView.startInstallIntent(intent);
        } catch (Exception e) {
            // 讲道理不应该走到这里
            e.printStackTrace();
            Toast.makeText(context, "新版本已经下载好了~", Toast.LENGTH_SHORT).show();
        }
    }

    private void registerInstallHandler() {
        context.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (mDownloadedFileID == -1) {
                    return;
                }
                // Grabs the Uri for the file that was downloaded.
                DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                DownloadManager.Query query = new DownloadManager.Query();
                query.setFilterById(mDownloadedFileID);
                Cursor cursor = downloadManager.query(query);
                if (cursor.moveToFirst()) {
                    int downloadStatus = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                    String downloadLocalUri = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                    if ((downloadStatus == DownloadManager.STATUS_SUCCESSFUL) && downloadLocalUri != null) {
                        doInstall(new File(Uri.parse(downloadLocalUri).getPath()));
                    }
                }
                cursor.close();
                mDownloadedFileID = -1;
                mIsUpdating = false;
            }
        }, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }
}
