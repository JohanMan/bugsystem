package com.dc.ddureportreceiver;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ReportConnectioner {

	private ExecutorService listeningService = Executors.newSingleThreadExecutor();

	private ReportFrame frame;
	
	private ExecutorService downloadService = Executors.newSingleThreadExecutor();

	public void setReportLisetner(ReportFrame frame) {
		this.frame = frame;
	}

	public void startListener() {
		listeningService.execute(new Runnable() {
			public void run() {
				Socket socket = null;
				try {
					socket = new Socket();
					socket.connect(new InetSocketAddress("192.168.50.239", 11550));
					InputStream inputStream = socket.getInputStream();
					while (true) {
						read(inputStream);
					}
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					if (socket != null) {
						try {
							socket.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		});
	}

	private byte[] readData(InputStream inputStream, int length) {
		ByteBuffer byteBuffer = ByteBuffer.allocate(length);
		int readedSize = 0;
		while (readedSize < length) {
			try {
				byte[] bufferData = new byte[length - readedSize];
				readedSize = inputStream.read(bufferData);
				byteBuffer.put(bufferData);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		return byteBuffer.array();
	}

	private void read(InputStream inputStream) {
		byte[] dataLengthBytes = readData(inputStream, 4);
		int dataLength = Util.bytesToInt(dataLengthBytes);
		byte[] dataBytes = readData(inputStream, dataLength);
		String data = new String(dataBytes);
		parseAndDownload(data);
	}
	
	private void parseAndDownload(String logUrls) {
		boolean isContainsSplit = logUrls.contains("#");
		if (isContainsSplit) {
			String[] logUrlArr = logUrls.split("#");
			for (String logUrl : logUrlArr) {
				download(logUrl);
			}
		} else {
			download(logUrls);
		}
	}
	
	private void download(final String logUrl) {
		downloadService.execute(new Runnable() {
			public void run() {
				// TODO Auto-generated method stub
				String logPath = FileUtil.downloadFile(logUrl);
				System.out.println("log path : " + logPath);
				if (logPath != null) {
					if (frame != null) {
						frame.receiveReport(logPath);
					}
				}
			}
		});
	}

}
