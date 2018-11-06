package cn.lch.learn.code.chatroom.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

/**
 * <p>Nio客户端</p>
 *
 * @author lichanghong  create by  2018/11/6 16:30
 **/
public class NioRoomClient {
    private Selector selector = null;
    static final int port = 8080;
    private Charset charset = Charset.forName("UTF-8");
    private SocketChannel sc = null;
    private String name = "";
    private static String USER_EXIST = "system message: user exist, please change a name";
    private static String USER_CONTENT_SPILIT = "#@#";

    public void init() throws IOException {
        //创建连接器
        selector = Selector.open();
        sc = SocketChannel.open(new InetSocketAddress("127.0.0.1",port));
        //设置非阻塞
        sc.configureBlocking(false);
        sc.register(selector, SelectionKey.OP_READ);
        //创建一个线程读取服务器端数据
        new Thread(new ClientThread()).start();
        //从控制台读取数据
        Scanner scan = new Scanner(System.in);
        while (scan.hasNextLine()){
            String line = scan.nextLine();
            if("".equals(line)){
                continue;
            }
            if("".equals(name)) {
                name = line;
                line = name+USER_CONTENT_SPILIT;
            } else {
                line = name+USER_CONTENT_SPILIT+line;
            }
            sc.write(charset.encode(line));
        }
    }
    private class ClientThread implements Runnable{
        @Override
        public void run() {
            try
            {
                while(true) {
                    int readyChannels = selector.select();
                    if(readyChannels == 0) {continue;}
                    //可以通过这个方法，知道可用通道的集合
                    Set<SelectionKey> selectedKeys = selector.selectedKeys();
                    Iterator<SelectionKey> keyIterator = selectedKeys.iterator();
                    while(keyIterator.hasNext()) {
                        SelectionKey sk = keyIterator.next();
                        keyIterator.remove();
                        processData(sk);
                    }
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        private void processData(SelectionKey sk) throws IOException {
            if(sk.isReadable())
            {
                //使用 NIO 读取 Channel中的数据，这个和全局变量sc是一样的，因为只注册了一个SocketChannel
                //sc既能写也能读，这边是读
                SocketChannel sc = (SocketChannel)sk.channel();

                ByteBuffer buff = ByteBuffer.allocate(1024);
                String content = "";
                while(sc.read(buff) > 0)
                {
                    buff.flip();
                    content += charset.decode(buff);
                }
                //若系统发送通知名字已经存在，则需要换个昵称
                if(USER_EXIST.equals(content)) {
                    name = "";
                }
                System.out.println(content);
                sk.interestOps(SelectionKey.OP_READ);
            }
        }
    }

    public static void main(String[] args) throws IOException {
        new NioRoomClient().init();
    }
}
