package cn.lch.learn.code.chatroom.bio.client;

import java.io.*;
import java.net.Socket;

/**
 * <p>客户端</p>
 *
 * @author lichanghong  create by  2018/11/1 14:11
 **/
public class Client {
    private static int SERVER_PORT = 8888;
    private static String SERVER_IP = "127.0.0.1";
    public static void main(String[] args) {
        Socket socket = null;
        BufferedReader br = null;

        try {
            //客户端socket指定服务器的地址和端口号
            socket = new Socket(SERVER_IP, SERVER_PORT);
            System.out.println("Socket=" + socket);
            new Thread(new ClientHandler(socket)).start();
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String line =null;
            while (( line = br.readLine())!=null ) {
                System.out.println(line);
            }
            System.out.println("客户端停止");
        } catch (Exception e) {
            e.printStackTrace();
        }  finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(socket!=null){
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
