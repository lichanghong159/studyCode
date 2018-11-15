package cn.lch.learn.code.netty.timeapp.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * <p></p>
 *
 * @author lichanghong  create by  2018/11/12 14:41
 **/
public class TimeServerHandler extends SimpleChannelInboundHandler<Object> {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf =(ByteBuf) msg;
        byte [] bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);
        logger.info(new String(bytes,"utf-8"));
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        ctx.close();
    }
}
