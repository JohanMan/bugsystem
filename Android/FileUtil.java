package com.dc.ddu.utils;

import android.os.Environment;

import com.dc.ddu.DDUApplication;
import com.dc.ddu.data.cache.UserCache;
import com.dc.ddu.model.User;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Johan on 2016/10/24.
 */
public class FileUtil {

    private static final String PROJECT_DOWNLOAD_PATH = "download";
    private static final String PROJECT_RECEIVE_PATH = "receive";
    private static final String PROJECT_SOUND_PATH = "sound";
    private static final String PROJECT_LOG_PATH = "log";

    private static final String CACHE_NET = "net";
    private static final String CACHE_IMG = "img";
    private static final String CACHE_RECORD = "record";

    public static String getDiskCacheDir(String type) {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || !Environment.isExternalStorageRemovable()) {
            cachePath = DDUApplication.getDDUApplicationContext().getExternalCacheDir().getPath();
        } else {
            cachePath = DDUApplication.getDDUApplicationContext().getCacheDir().getPath();
        }
        cachePath = cachePath + File.separator + type;
        File dir = new File(cachePath + File.separator + type);
        if (dir.exists() && dir.isDirectory()) {
            return cachePath;
        }
        dir.mkdirs();
        return cachePath;
    }

    public static String getNetCachePath() {
        return getDiskCacheDir(CACHE_NET);
    }

    public static String getImgCachePath() {
        return getDiskCacheDir(CACHE_IMG);
    }

    public static String getRecordCachePath() {
        return getDiskCacheDir(CACHE_RECORD);
    }

    public static String getProjectPath() {
        String projectPath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || !Environment.isExternalStorageRemovable()) {
            projectPath = DDUApplication.getDDUApplicationContext().getExternalFilesDir(null).getPath() ;
        } else {
            projectPath = DDUApplication.getDDUApplicationContext().getFilesDir().getPath();
        }
        User user = UserCache.getUser();
        projectPath = projectPath + File.separator + user.mobile + "_" + user.identity;
        File dir = new File(projectPath + File.separator + user.mobile + "_" + user.identity);
        if (dir.exists() && dir.isDirectory()) {
            return projectPath;
        }
        dir.mkdirs();
        return projectPath;
    }

    public static String getExternalName(String fileName) {
        int dotIndex = fileName.lastIndexOf(".");
        if (dotIndex == -1) return fileName;
        if (dotIndex == fileName.length() - 1) return "";
        return fileName.substring(dotIndex + 1);
    }

    public static String getReceiveSoundPath() {
        String receiveSoundPath = getProjectPath() + File.separator + PROJECT_RECEIVE_PATH + File.separator + PROJECT_SOUND_PATH;
        File dir = new File(receiveSoundPath);
        if (dir.exists() && dir.isDirectory()) {
            return receiveSoundPath;
        }
        dir.mkdirs();
        return receiveSoundPath;
    }

    public static String getDownloadPath() {
        String downloadPath = getProjectPath() + File.separator + PROJECT_DOWNLOAD_PATH;
        File dir = new File(downloadPath);
        if (dir.exists() && dir.isDirectory()) {
            return downloadPath;
        }
        dir.mkdirs();
        return downloadPath;
    }

    public static String getLogPath() {
        String logPath = getProjectPath() + File.separator + PROJECT_LOG_PATH;
        File dir = new File(logPath);
        if (dir.exists() && dir.isDirectory()) {
            return logPath;
        }
        dir.mkdirs();
        return logPath;
    }

    public static void createDir(String dirPath) {
        if (dirPath == null) return;
        File dir = new File(dirPath);
        if (dir.exists() && dir.isDirectory()) {
            return;
        }
        dir.mkdirs();
    }

    public static void deleteFile(String filePath) {
        if (filePath == null) return;
        File file = new File(filePath);
        if (file.exists() && file.isFile()) {
            file.delete();
        }
    }

    public static synchronized String savePictureFromInputStream(InputStream inputStream) {
        String fileName = getImgCachePath() + File.separator + Util.getCurrentServerTime() + ".jpg";
        if (saveFileFromInputStream(inputStream, fileName)) {
            return fileName;
        } else {
            return null;
        }
    }

    public static synchronized String saveSoundFromInputStream(InputStream inputStream) {
        String fileName = getRecordCachePath() + File.separator + Util.getCurrentServerTime() + ".3gp";
        if (saveFileFromInputStream(inputStream, fileName)) {
            return fileName;
        } else {
            return null;
        }
    }

    public static boolean saveFileFromInputStream(InputStream inputStream, String filePath) {
        if (inputStream == null || filePath == null) return false;
        byte[] buf = new byte[512];
        int len;
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(new File(filePath));
            while ((len = inputStream.read(buf)) != -1) {
                fos.write(buf, 0, len);
            }
            fos.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            closeSilently(inputStream);
            closeSilently(fos);
        }
    }

    public static String saveLog(String logUser, String logData) {
        String logName = logUser + "-" + Util.getCurrentServerTime() + ".txt";
        String logPath = getLogPath() + File.separator + logName;
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(new File(logPath));
            StringBuilder logContentBuild = new StringBuilder();
            logContentBuild.append("日志时间 : ").append(Util.formatTime("yyyy-MM-dd HH:mm;ss", Util.getCurrentServerTime())).append("\n")
                    .append("机型 : ").append(android.os.Build.MODEL).append("\n")
                    .append("用户 : ").append(logUser).append("\n")
                    .append(logData);
            fos.write(logContentBuild.toString().getBytes());
            return logPath;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeSilently(fos);
        }
        return null;
    }

    public static void closeSilently(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
