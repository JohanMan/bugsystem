package com.dc.ddu.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.dc.ddu.data.cache.UserCache;
import com.dc.ddu.model.User;
import com.dc.ddu.ui.service.CrashService;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * Created by Johan on 2016/11/23.
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {

    private static final CrashHandler INSTANCE = new CrashHandler();

    private Thread.UncaughtExceptionHandler defaultCrashHandler;

    private Context context;

    private CrashHandler() {

    }

    public static CrashHandler getInstance() {
        return INSTANCE;
    }

    public void init(Context context) {
        this.context = context;
        defaultCrashHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (ex == null) {
            defaultCrashHandler.uncaughtException(thread, ex);
            return;
        }
        PrintStream printStream = null;
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            printStream = new PrintStream(byteArrayOutputStream);
            ex.printStackTrace(printStream);
            byte[] data = byteArrayOutputStream.toByteArray();
            String logContent = new String(data);
            startLogService(logContent);
            System.exit(1);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            FileUtil.closeSilently(printStream);
        }
    }

    private void startLogService(String logContent) {
        Intent intent = new Intent(CrashService.CRASH_SERVICE_ACTION);
        intent.putExtra(CrashService.FILED_MODE, CrashService.MODE_UPLOAD);
        User user = UserCache.getUser();
        if (user != null) {
            intent.putExtra(CrashService.FILED_USER, user.mobile);
        }
        intent.putExtra(CrashService.FILED_CONTENT, logContent);
        PendingIntent restartIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, restartIntent);
    }

}
