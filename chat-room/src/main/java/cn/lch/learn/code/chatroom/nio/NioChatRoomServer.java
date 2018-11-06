package cn.lch.learn.code.chatroom.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * <p>Nio聊天室</p>
 *
 * @author lichanghong  create by  2018/11/6 15:37
 **/
public class NioChatRoomServer {
    /**
     * 选择器
     */
    private Selector selector = null;
    /**
     * 服务器通道
     */
    private ServerSocketChannel serverSocketChannel;
    /**
     * 端口
     */
    private static final  int port = 8080;
    /**
     * 通讯编码
     */
    private Charset charset = Charset.forName("UTF-8");
    /**
     * 用来记录在线人数及昵称
     */
    private static Set<String> userNameSet = new ConcurrentSkipListSet<>();

    /**
     * 用来判断服务器是否停止
     */
    private static volatile boolean isRun = true;

    private static String USER_EXIST = "system message: user exist, please change a name";

    //相当于自定义协议格式，与客户端协商好
    private static String USER_CONTENT_SPILIT = "#@#";

    /**
     * 初始化
     */
    public void init() throws IOException {
        //创建选择器
        selector = Selector.open();
        //创建服务器通道
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(port));

        //以非阻塞方式运行
        serverSocketChannel.configureBlocking(false);

        //注册到选择器上，监听事件
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("服务器开启，监听端口:"+port);
        while (isRun){
            int readyChannels = selector.select();
            if(readyChannels==0){continue;}
            Set<SelectionKey> keySet = selector.selectedKeys();
            Iterator<SelectionKey> iterator = keySet.iterator();
            while (iterator.hasNext()){
                SelectionKey sk = iterator.next();
                iterator.remove();
                processConn(sk);
            }

        }
    }

    /**
     * 处理连接
     * @param sk
     */
    private void processConn(SelectionKey sk) throws IOException {
        //处理连接事件
        if(sk.isAcceptable()){
            SocketChannel sc = serverSocketChannel.accept();
            //非阻塞模式
            sc.configureBlocking(false);
            //注册选择器，并设置为读取模式，收到一个连接请求，然后起一个SocketChannel，并注册到selector上，之后这个连接的数据，就由这个SocketChannel处理
            sc.register(selector, SelectionKey.OP_READ);
            //将此对应的channel设置为准备接受其他客户端请求
            sk.interestOps(SelectionKey.OP_ACCEPT);
            System.out.println("接收到客户端连接:" + sc.getRemoteAddress());
            ByteBuffer byteBuffer = charset.encode("请输入自己的名字:");
            sc.write(byteBuffer);
        }
        //处理可读事件
        if(sk.isReadable()){
            //返回该SelectionKey对应的 Channel，其中有数据需要读取
            SocketChannel sc = (SocketChannel) sk.channel();
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            StringBuilder sb = new StringBuilder();
            while (sc.read(buffer)>0){
                buffer.flip();
                sb.append(charset.decode(buffer));
            }
            System.out.println("Server is listening from client " + sc.getRemoteAddress() + " data rev is: " + sb);
            //将此对应的channel设置为准备下一次接受数据
            sk.interestOps(SelectionKey.OP_READ);
            if(sb.length() > 0)
            {
                String[] arrayContent = sb.toString().split(USER_CONTENT_SPILIT);
                //注册用户
                if(arrayContent != null && arrayContent.length ==1) {
                    String name = arrayContent[0];
                    if(userNameSet.contains(name)) {
                        sc.write(charset.encode(USER_EXIST));

                    } else {
                        userNameSet.add(name);
                        int num = onlineNum(selector);
                        String message = "welcome "+name+" to chat room! Online numbers:"+num;
                        broadCast(selector, null, message);
                    }
                }
                //注册完了，发送消息
                else if(arrayContent != null && arrayContent.length >1){
                    String name = arrayContent[0];
                    String message = sb.substring(name.length()+USER_CONTENT_SPILIT.length());
                    message = name + " : " + message;
                    if(userNameSet.contains(name)) {
                        //不回发给发送此内容的客户端
                        broadCast(selector, sc, message);
                    }
                }
            }
        }
    }

    private void broadCast(Selector selector, SocketChannel except, String message) throws IOException {
        //广播数据到所有的SocketChannel中
        for(SelectionKey key : selector.keys())
        {
            Channel targetchannel = key.channel();
            //如果except不为空，不回发给发送此内容的客户端
            if(targetchannel instanceof SocketChannel && targetchannel!=except)
            {
                SocketChannel dest = (SocketChannel)targetchannel;
                dest.write(charset.encode(message));
            }
        }
    }

    private int onlineNum(Selector selector) {
        int res = 0;
        for(SelectionKey key : selector.keys())
        {
            Channel targetchannel = key.channel();

            if(targetchannel instanceof SocketChannel)
            {
                res++;
            }
        }
        return res;
    }

    /**
     * 关闭
     * @throws IOException
     */
    public void close() throws IOException {
        if(serverSocketChannel!=null){
            serverSocketChannel.close();
        }
        if(selector!=null){
            selector.close();
        }
    }

    public static void main(String[] args) {
        try {
            new NioChatRoomServer().init();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
