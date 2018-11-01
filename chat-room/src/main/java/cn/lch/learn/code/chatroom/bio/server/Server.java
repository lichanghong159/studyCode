package cn.lch.learn.code.chatroom.bio.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * <p>聊天室服务器</p>
 *
 * @author lichanghong  create by  2018/11/1 10:10
 **/
public class Server {
    /**
     * socketServer端口
     */
    private static int SERVER_PORT=8888;
    private static ExecutorService executorService =
            new ThreadPoolExecutor(10,100,10, TimeUnit.SECONDS,new LinkedBlockingQueue<Runnable>(1024));


    public static void main(String[] args) {
        startServer(SERVER_PORT);
    }
    public static void startServer(int port){
        ServerSocket serverSocket =null;
        try {
            serverSocket = new ServerSocket(port);
            System.out.println(String.format("服务器启动,监听端口:%d",port));
             while(true){
                 Socket socket = serverSocket.accept();
                 executorService.execute(new ProcessHandler(socket));
             }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(serverSocket!=null){
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

    }
}
