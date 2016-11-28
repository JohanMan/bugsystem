package com.dc.ddu.ui.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.dc.ddu.data.net.HttpCall;
import com.dc.ddu.utils.FileUtil;

import java.io.File;

/**
 * Created by Johan on 2016/11/23.
 */
public class CrashService extends Service {

    public static final String CRASH_SERVICE_ACTION = "com.dc.ddu.bug";

    public static final int MODE_START = 1;
    public static final int MODE_UPLOAD = 2;
    public static final int MODE_STOP = 3;

    public static final String FILED_MODE = "mode_field";
    public static final String FILED_USER = "user_field";
    public static final String FILED_CONTENT = "content_field";

    private boolean isReadyToDeath;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int mode = intent.getIntExtra(FILED_MODE, MODE_STOP);
        switch (mode) {
            case MODE_START :
                File logDir = new File(FileUtil.getLogPath());
                if (logDir.exists() && logDir.isDirectory()) {
                    File[] logFiles = logDir.listFiles();
                    for (File logFile : logFiles) {
                        uploadLog(logFile.getAbsolutePath());
                    }
                }
                break;
            case MODE_UPLOAD :
                String logContent = intent.getStringExtra(FILED_CONTENT);
                String logUser = intent.getStringExtra(FILED_USER);
                Log.e(getClass().getName(), "崩溃日志 : " + logContent);
                if (logContent == null) break;
                String logPath = FileUtil.saveLog(logUser, logContent);
                Log.e(getClass().getName(), "log path : " + logPath);
                if (logPath != null) {
                    uploadLog(logPath);
                }
                isReadyToDeath = true;
                break;
            case MODE_STOP :
                stopSelf();
                break;
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void uploadLog(final String logPath) {
        HttpCall.Builder builder = new HttpCall.Builder();
		// 上传
        builder.upload("account/updateBugFile")
                .addFiles("bugFile", logPath);
        builder.build().request(new HttpCall.NetResponse<Object>() {
            @Override
            public void onSuccess(Object response) {
                Log.e(getClass().getName(), "上传log成功");
                FileUtil.deleteFile(logPath);
                if (isReadyToDeath) {
                    stopSelf();
                }
            }
            @Override
            public void onFail(String errCode, String errMsg) {
                Log.e(getClass().getName(), "上传log出错 : " + errCode + " | " + errMsg);
                if (isReadyToDeath) {
                    stopSelf();
                }
            }
        });
    }

}
