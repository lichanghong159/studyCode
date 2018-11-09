package cn.lch.learn.code.netty.websocket.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p></p>
 *
 * @author lichanghong  create by  2018/11/9 15:50
 **/
public class WebSocketServerHandler extends SimpleChannelInboundHandler<WebSocketFrame> {
    private static Logger logger = LoggerFactory.getLogger(WebSocketServerHandler.class);
    private static Map<String,ChannelHandlerContext> aliveClients = new ConcurrentHashMap<>();

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
       //todo 心跳监测
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        logger.info("监测到客户端连接:{}", ctx);
        String clientId = getClientId(ctx);
        aliveClients.put(clientId, ctx);
        String msg = String.format("系统提示:欢迎[%s]加入",clientId);
        sendMsgAll(msg);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame msg) throws Exception {
        String clientId = getClientId(ctx);
        if (msg instanceof TextWebSocketFrame) {
            String request = ((TextWebSocketFrame) msg).text();
        logger.info("接收客户端： {} 发送的消息:{}", clientId, request);
            sendMsgAll(String.format("%s 说:%s",clientId,request));
        }else{
            String message = "unsupported frame type: " + msg.getClass().getName();
            throw new UnsupportedOperationException(message);
        }
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        String clientId = getClientId(ctx);
        aliveClients.remove(clientId);
        String msg = String.format("系统提示:[%s]离开",clientId);
        sendMsgAll(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        String clientId = getClientId(ctx);
        aliveClients.remove(clientId);
        String msg = String.format("系统提示:[%s]异常退出",clientId);
        sendMsgAll(msg);
    }

    /**
     * 获取客户端身份
     * @param ctx
     * @return
     */
    private String getClientId(ChannelHandlerContext ctx){
        InetSocketAddress address = (InetSocketAddress )ctx.channel().remoteAddress();
        String key = String.format("%s:%s",address.getAddress().getHostAddress(), address.getPort());
        return key;
    }

    /**
     * 发送消息给所有已连接的客户端
     * @param msg
     */
    private static void sendMsgAll(String msg){
        /**
         * 将消息推送给所有在线的客户端
         */
        aliveClients.values().stream().forEach(ctx->{
            WebSocketFrame frame = new TextWebSocketFrame(msg);
            ctx.channel().writeAndFlush(frame);
        });
    }


}
