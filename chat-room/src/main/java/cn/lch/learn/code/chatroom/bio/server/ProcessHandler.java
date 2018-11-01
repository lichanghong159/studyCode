package cn.lch.learn.code.chatroom.bio.server;

import java.io.*;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p></p>
 *
 * @author lichanghong  create by  2018/11/1 14:16
 **/
public class ProcessHandler implements Runnable {
    /**
     * 用来记录客户端连接记录
     */
    private static Map<String, Socket> clientMap = new ConcurrentHashMap<>();
    private Socket socket;

    public ProcessHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        if (socket == null) {
            return;
        }
        String clientIdentity = String.format("%s:%d", socket.getInetAddress().getHostAddress(), socket.getPort());
        BufferedReader br = null;
        PrintWriter pw = null;
        try {
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),true);
            clientMap.put(clientIdentity, socket);
            sendMsg2Client(String.format("欢迎[%s]进入聊天室！当前聊天室有[%s]人", clientIdentity, clientMap.size()));


            while (true) {
                String line = br.readLine();
                System.out.println(String.format("收到客户端[%s]:%s", clientIdentity, line));
                sendMsg2Client(String.format("用户[%s]:%s", clientIdentity, line));
                if ("bye".equalsIgnoreCase(line)) {
                    break;
                }

            }
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (pw != null) {
                pw.close();
            }
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    clientMap.remove(clientIdentity);
                    sendMsg2Client(String.format("用户[%s]离开聊天室,当前还有[%s]人在", clientIdentity,clientMap.size()));
                }
            }
        }
    }

    /**
     * 向其他客户端推送消息
     *
     * @param msg            推送的消息
     */
    private void sendMsg2Client(String msg) {
        if (clientMap.size() == 0) {
            return;
        }
        clientMap.values().stream().forEach(clientSocket -> {
            try {
                PrintWriter pw = new PrintWriter(clientSocket.getOutputStream(), true);
                pw.println(msg);
                pw.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
