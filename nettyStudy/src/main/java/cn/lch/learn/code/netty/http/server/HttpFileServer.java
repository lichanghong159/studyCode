package cn.lch.learn.code.netty.http.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>http协议，文件服务</p>
 *
 * @author lichanghong  create by  2018/11/13 17:49
 **/
public class HttpFileServer {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private static final String default_url="/src/main/java/cn/lch/learn/code/netty";
    public void run(final int port,final String url){
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline=ch.pipeline();
                        pipeline.addLast(new HttpRequestDecoder())
                                .addLast(new HttpObjectAggregator(65536))
                                .addLast(new HttpResponseEncoder())
                                .addLast(new ChunkedWriteHandler())
                                .addLast("fileServerHandler",new HttpFileServerHandler(url));
                        }
                    });
            ChannelFuture future =b .bind(port).sync();
            logger.info("HTTP 文件目录服务器启动，网址是：http://127.0.0.1:{}{}",port,url);
            future.channel().closeFuture().sync();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        int port = 8080;
        String url =default_url;
        new HttpFileServer().run(port, url);
    }
}
