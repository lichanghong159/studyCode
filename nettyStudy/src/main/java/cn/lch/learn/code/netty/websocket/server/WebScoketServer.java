package cn.lch.learn.code.netty.websocket.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>websocket服务器代码</p>
 *
 * @author lichanghong  create by  2018/11/9 15:31
 **/
public class WebScoketServer {
    private static   Logger logger = LoggerFactory.getLogger(WebScoketServer.class);
    /**
     * 服务器地址
     */
    private static final String HOST = "127.0.0.1";
    /**
     * 监听端口
     */
    private static final int PORT = 8080;
    /**
     * boss线程数量
     */
    private static final int BOSS_THREAD_SIZE = Runtime.getRuntime().availableProcessors()*2;
    /**
     * worker线程数量
     */
    private static final int WORKER_THREAD_SIZE = 100;
    /**
     * boss线程
     */
    private static final EventLoopGroup BOSS_GROUP = new NioEventLoopGroup(BOSS_THREAD_SIZE);

    /**
     * worker线程
     */
    private static final EventLoopGroup WORKER_GROUP = new NioEventLoopGroup(WORKER_THREAD_SIZE);

    private static final String WEBSOCKET_PATH = "/websocket";
    public static void start(){
        logger.info("服务器启动中......" );
        ServerBootstrap bootstrap = new ServerBootstrap();
        try {
            bootstrap.group(BOSS_GROUP, WORKER_GROUP)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new HttpServerCodec());
                            pipeline.addLast(new HttpObjectAggregator(65536));
                            pipeline.addLast(new WebSocketServerCompressionHandler());
                            pipeline.addLast(new WebSocketServerProtocolHandler(WEBSOCKET_PATH, null, true));
                            pipeline.addLast(new WebSocketIndexPageHandler(WEBSOCKET_PATH));
                            pipeline.addLast(new WebSocketServerHandler());
                        }
                    });
            ChannelFuture future = bootstrap.bind(HOST, PORT).sync();
            logger.info("服务器启动成功，监听:{}:{}", HOST,PORT);
            future.channel().closeFuture().sync();
            logger.warn("服务器停止");
        }catch (Exception e){
            e.printStackTrace();
            logger.error("服务器异常,{}", e);
        }finally {
            //钩子方法，优雅关闭
            BOSS_GROUP.shutdownGracefully();
            WORKER_GROUP.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        start();
    }
}
