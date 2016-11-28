package com.dc.bug;

import com.dc.controller.AccountController;
import com.dc.utils.ToolUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Created by Johan on 2016/11/25.
 * 集成spring mvc
 */
@Component
public class BugSocket {

    private static Logger logger = LogManager.getLogger(AccountController.class.getName());

    private List<Socket> socketList = new ArrayList<>();

    private ExecutorService listenerService = Executors.newSingleThreadExecutor();

    private List<String> pathList = new ArrayList<>();

    private static boolean isStop = true;

    @PostConstruct
    public void start() {
        logger.info("@PostConstrut....");
        if (!isStop) {
            return;
        }

        listenerService.execute(new Runnable() {
            @Override
            public void run() {
                ServerSocket serverSocket = null;
                try {
                    isStop = false;
                    serverSocket = new ServerSocket(11550);
                    logger.info("开监听-----------------------------------");
                    while (!isStop) {
                        Socket socket = serverSocket.accept();
                        logger.info("接收到Socket : " + socket.getRemoteSocketAddress());
                        socketList.add(socket);
                        checkSocket();
                        send();
                    }
                    closeSocket();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        serverSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void checkSocket() {
        Iterator<Socket> iterator = socketList.iterator();
        while (iterator.hasNext()) {
            Socket socket = iterator.next();
            try {
                socket.sendUrgentData(0xff);
            } catch (IOException e) {
                logger.info("socket (" + socket.getRemoteSocketAddress() + ") is close");
                try {
                    socket.close();
                    iterator.remove();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    private void closeSocket() {
        Iterator<Socket> iterator = socketList.iterator();
        while (iterator.hasNext()) {
            Socket socket = iterator.next();
            try {
                socket.close();
                iterator.remove();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    public void sendReport(String logFilePath) {
        logger.info("log path : " + logFilePath);
        pathList.add(logFilePath);
        send();
    }

    private void send() {
        if (socketList.size() == 0) return;
        Iterator<Socket> iterator = socketList.iterator();
        String logPaths = null;
        while (iterator.hasNext()) {
            Socket socket = iterator.next();
            logger.info("send Socket : " + socket.getRemoteSocketAddress());
            try {
                socket.sendUrgentData(0xff);
                OutputStream outputStream = socket.getOutputStream();
                if (pathList.size() > 0) {
                    StringBuilder logBuilder = new StringBuilder();
                    for (String logPath : pathList) {
                        logBuilder.append(logPath).append("#");
                    }
                    logBuilder.deleteCharAt(logBuilder.length() - 1);
                    logPaths = logBuilder.toString();
                }
                logger.info("send log path : " + logPaths);
                if (logPaths == null || "".equals(logPaths)) break;
                int logPathsLength = logPaths.length();
                byte[] logPathsBytes = ToolUtil.combineTowBytes(ToolUtil.intToByte(logPathsLength), logPaths.getBytes());
                outputStream.write(logPathsBytes);
                pathList.clear();
            } catch (IOException e) {
                logger.info("send socket (" + socket.getRemoteSocketAddress() + ") is close");
                try {
                    socket.close();
                    iterator.remove();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    @PreDestroy
    public void stop() {
        logger.info("@PreDestroy.....");
        isStop = true;
        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress("localhost", 11550));
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
