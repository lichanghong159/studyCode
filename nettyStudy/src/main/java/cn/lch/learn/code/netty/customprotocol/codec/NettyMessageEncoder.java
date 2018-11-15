package cn.lch.learn.code.netty.customprotocol.codec;
import cn.lch.learn.code.netty.customprotocol.msg.Header;
import cn.lch.learn.code.netty.customprotocol.msg.NettyMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.io.IOException;
import java.io.UnsupportedEncodingException;


/**
 * <p>消息编码</p>
 *
 * @author lichanghong  create by  2018/11/14 13:47
 **/
public class NettyMessageEncoder extends MessageToByteEncoder<NettyMessage> {
    private static final byte[] LENGTH_PLACEHOLDER = new byte[4];
    MarshallingEncoder marshallingEncoder;
    public  NettyMessageEncoder() throws IOException {
        this.marshallingEncoder = new MarshallingEncoder();
    }
    @Override
    protected void encode(ChannelHandlerContext ctx, NettyMessage msg, ByteBuf out) throws Exception {
        if(msg == null || msg.getHeader()==null){
            throw new NullPointerException("需要编码的消息为空");
        }

        Header header = msg.getHeader();
        out.writeInt(header.getCrcCode());
        out.writeInt(header.getLength());
        out.writeLong(header.getSessionId());
        out.writeByte(header.getType());
        out.writeByte(header.getPriority());
        out.writeInt(header.getAttachment().size());
        header.getAttachment().forEach((key,value)->{
            try {
                byte[] keyArray = key.getBytes("UTF-8");
                out.writeInt(keyArray.length);
                out.readBytes(keyArray);
                marshallingEncoder.encode(value, out);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        if(msg.getBody() != null){
            marshallingEncoder.encode(msg.getBody(), out);
        }else{
            out.writeInt(0);
        }
        out.setInt(4, out.readableBytes() - 8);
    }

}
