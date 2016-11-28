package com.dc.ddureportreceiver;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class FileUtil {

	public static final String LOG_PATH = "F:/fengyihuan/log/";
	
	public static void openLogFile(String logFilePath) {
		try {
			java.awt.Desktop.getDesktop().open(new File(logFilePath));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String downloadFile(String httpUrl) {
		String savePath = LOG_PATH + httpUrl.substring(12);
		httpUrl = "http://192.168.50.239:8080" + httpUrl;
		System.err.println(httpUrl);
        int byteread = 0;
        InputStream inputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            URL url = new URL(httpUrl);
            URLConnection connection = url.openConnection();
            inputStream = connection.getInputStream();
            fileOutputStream = new FileOutputStream(savePath);
            byte[] buffer = new byte[1204];
            while ((byteread = inputStream.read(buffer)) != -1) {
            	fileOutputStream.write(buffer, 0, byteread);
            }
            fileOutputStream.flush();
            return savePath;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return httpUrl;
        } catch (IOException e) {
            e.printStackTrace();
            return httpUrl;
        } finally {
        	try {
        		if (fileOutputStream != null) {
        			fileOutputStream.close();
    			}
        		if (inputStream != null) {
					inputStream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
    }
	
}
