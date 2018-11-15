package cn.lch.learn.code.netty.timeapp.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p></p>
 *
 * @author lichanghong  create by  2018/11/12 14:26
 **/
public class TimeServer {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    public void bind(int port){
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup);
        b.channel(NioServerSocketChannel.class)
                //设置最大连接数
                .option(ChannelOption.SO_BACKLOG, 1024)
                .childHandler(new ChildChannelHandler());
            /**
             *   绑定端口，同步等待执行成功
             *   为了防止，端口还没有启动，导致程序异常。
             */

           ChannelFuture f = b.bind(port).sync();
            logger.info("服务器启动..." );
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            //优雅关闭
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
    private class ChildChannelHandler extends ChannelInitializer<SocketChannel> {

        @Override
        protected void initChannel(SocketChannel ch) throws Exception {
            ChannelPipeline pipeline = ch.pipeline();
            ByteBuf delimiter = Unpooled.wrappedBuffer("$_".getBytes());
            pipeline.addLast(new LineBasedFrameDecoder(1024));
            pipeline.addLast(new DelimiterBasedFrameDecoder(1024,delimiter));
            pipeline.addLast(new StringDecoder());
            pipeline.addLast(new TimeServerHandler());
        }
    }

    public static void main(String[] args) {
        new TimeServer().bind(8888);
    }
}
