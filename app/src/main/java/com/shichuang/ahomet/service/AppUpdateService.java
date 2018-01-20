package com.shichuang.ahomet.service;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.NotificationCompat;
import android.util.Log;


import com.shichuang.ahomet.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * helper methods.
 */
public class AppUpdateService extends IntentService {
    private static final String ACTION_UPDATE = "com.shichuang.ahomet.service.action.update";
    private static final String EXTRA_URL = "com.shichuang.ahomet.service.extra.url";
    private static final String EXTRA_FILE_NAME = "com.shichuang.ahomet.service.extra.file.name";
    private static final String DOWNLOAD_DIRECTORY = Environment.getExternalStorageDirectory().getAbsolutePath()
            + "/com.shichuang.ahomet/download";
    private boolean isRunning = false;
    private NotificationManager updateNotificationManager;
    private NotificationCompat.Builder mBuilder;
    private static OnProgressListener mProgressListener;
    //定义notification实用的ID
    private static final int NO_3 = 0x3;

    public interface OnProgressListener {
        void onProgress(int progress);

        void onSuccess(boolean isSuccess);
    }

    public AppUpdateService() {
        super("AppUpdateService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_UPDATE.equals(action)) {
                final String downloadUrl = intent.getStringExtra(EXTRA_URL);
                final String fileName = intent.getStringExtra(EXTRA_FILE_NAME);
                startDownload(downloadUrl, fileName);
            }
        }
    }

    @Override
    public void onDestroy() {
        mProgressListener = null;
        super.onDestroy();
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startUpdate(Context context, String downloadUrl, String fileName,
                                   OnProgressListener pregressListener) {
        mProgressListener = pregressListener;
        Intent intent = new Intent(context, AppUpdateService.class);
        intent.setAction(ACTION_UPDATE);
        intent.putExtra(EXTRA_URL, downloadUrl);
        intent.putExtra(EXTRA_FILE_NAME, fileName);
        context.startService(intent);
    }

    @SuppressLint("NewApi")
    private void startDownload(String downloadUrl, String fileName) {
        if (isRunning) {
            return;
        }
        isRunning = true;
        initRemoteView(); // 初始化状态栏
        try {
            boolean isSuccess = downloadUpdateFile(downloadUrl, fileName);
            if (mProgressListener != null) {
                mProgressListener.onSuccess(isSuccess);
            }
            if (isSuccess) {
                Log.d("test", "success");
                File apkFile = new File(DOWNLOAD_DIRECTORY, fileName + ".apk");
                if (!apkFile.exists()) {
                    return;
                }
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (Build.VERSION.SDK_INT >= 24) {
                    //参数1 上下文, 参数2 Provider主机地址 和配置文件中保持一致   参数3  共享的文件
                    Uri apkUri = FileProvider.getUriForFile(this, "com.shichuang.ahomet.fileprovider", apkFile);
                    //添加这一句表示对目标应用临时授权该Uri所代表的文件
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
                } else {
                    intent.setDataAndType(Uri.parse("file://" + apkFile.toString()),
                            "application/vnd.android.package-archive");
                }
                startActivity(intent);
                try {
                    updateNotificationManager.cancel(NO_3);
                } catch (Exception ex) {
                }
            } else {
                Notification notification = new Notification.Builder(AppUpdateService.this)
                        .setContentTitle(getString(R.string.app_name)).setContentText("下载失败")
                        .setSmallIcon(R.mipmap.ic_logo).build();
                updateNotificationManager.notify(NO_3, notification);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 初始化状态栏进度条
     */
    private void initRemoteView() {
        mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setSmallIcon(R.mipmap.ic_logo);
        mBuilder.setContentTitle("下载");
        mBuilder.setContentText("正在下载");
        updateNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        updateNotificationManager.notify(NO_3, mBuilder.build());
        mBuilder.setProgress(100, 0, false);
    }

    /**
     * 下载文件
     *
     * @param downloadUrl
     * @param fileName
     * @return
     * @throws Exception
     */
    private boolean downloadUpdateFile(String downloadUrl, String fileName) {
        try {
            int downloadCount = 0;
            int currentSize = 0;
            long totalSize = 0;
            int updateTotalSize = 0;
            boolean result = false;
            HttpURLConnection httpConnection = null;
            InputStream is = null;
            FileOutputStream fos = null;
            // 创建下载目录
            File downloadFilePath = new File(DOWNLOAD_DIRECTORY);
            if (!downloadFilePath.exists()) {
                downloadFilePath.mkdirs();
                Log.d("test", "创建文件夹成功");
            }
            // 输出文件地址
            File temp = new File(downloadFilePath, fileName + ".apk");
//            if (temp.getParentFile().isDirectory()) {
//                temp.getParentFile().mkdirs();
//            }
            if (temp.createNewFile()) {
                Log.d("test", "创建文件成功");
            }
            try {
                URL url = new URL(downloadUrl);
                httpConnection = (HttpURLConnection) url.openConnection();
                httpConnection.setRequestProperty("User-Agent", "PacificHttpClient");
                if (currentSize > 0) {
                    httpConnection.setRequestProperty("RANGE", "bytes=" + currentSize + "-");
                }
                httpConnection.setConnectTimeout(20000);
                httpConnection.setReadTimeout(120000);
                updateTotalSize = httpConnection.getContentLength();
                if (httpConnection.getResponseCode() == 404) {
                    throw new Exception("fail!");
                }
                is = httpConnection.getInputStream();
                fos = new FileOutputStream(temp, false);
                byte buffer[] = new byte[4096];
                int readsize = 0;
                while ((readsize = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, readsize);
                    totalSize += readsize; // 为了防止频繁的通知导致应用吃紧，百分比增加10才通知一次
                    if ((downloadCount == 0) || (int) (totalSize * 100 / updateTotalSize) - 1 > downloadCount) {
                        downloadCount += 1;
                        try {
                            int progress = (int) totalSize * 100 / updateTotalSize;
                            mBuilder.setProgress(100, progress, false);
                            updateNotificationManager.notify(NO_3, mBuilder.build());
                            //下载进度提示
                            mBuilder.setContentText("下载" + progress + "%");
                            if (mProgressListener != null) {
                                mProgressListener.onProgress((int) totalSize * 100 / updateTotalSize);
                            }
                        } catch (Exception ex) {
                        }
                    }
                }
            } catch (Exception e) {
                Log.d("test", "Exception:" + e.getMessage());
            } finally {
                if (httpConnection != null) {
                    httpConnection.disconnect();
                }
                if (is != null) {
                    is.close();
                }
                if (fos != null) {
                    fos.close();
                }
                result = updateTotalSize > 0 && updateTotalSize == totalSize;
                if (!result) { // 下载失败或者为下载完成
                    new File(DOWNLOAD_DIRECTORY).delete();
                }
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
