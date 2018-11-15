package cn.lch.learn.code.netty.customprotocol.codec;

import io.netty.buffer.ByteBuf;
import org.jboss.marshalling.ByteOutput;

import java.io.IOException;

/**
 * <p></p>
 *
 * @author lichanghong  create by  2018/11/14 14:25
 **/
public class ChannelBufferByteOutput implements ByteOutput {
    private final ByteBuf buffer;

    /**
     * Create a new instance which use the given {@link ByteBuf}
     */
    public ChannelBufferByteOutput(ByteBuf buffer) {
        this.buffer = buffer;
    }
    @Override
    public void write(int b) throws IOException {
        buffer.writeByte(b);
    }

    @Override
    public void write(byte[] b) throws IOException {
        buffer.writeBytes(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        buffer.writeBytes(b, off, len);
    }

    @Override
    public void close() throws IOException {

    }

    @Override
    public void flush() throws IOException {

    }
    /**
     * Return the {@link ByteBuf} which contains the written content
     *
     */
    ByteBuf getBuffer() {
        return buffer;
    }
}
