package cn.lch.learn.code.netty.customprotocol.codec;

import io.netty.buffer.ByteBuf;
import org.jboss.marshalling.ByteInput;
import org.jboss.marshalling.Unmarshaller;

import java.io.IOException;

/**
 * <p></p>
 *
 * @author lichanghong  create by  2018/11/14 14:39
 **/
public class MarshallingDecoder {
    private final Unmarshaller unmarshaller;
    public MarshallingDecoder() throws IOException {
        unmarshaller = MarshallingFactory.buildUnMarshalling();
    }

    protected Object decode(ByteBuf in) throws Exception {
        int objectSize = in.readInt();
        ByteBuf buf = in.slice(in.readerIndex(), objectSize);
        ByteInput input = new ChannelBufferByteInput(buf);
        try {
            unmarshaller.start(input);
            Object obj = unmarshaller.readObject();
            unmarshaller.finish();
            in.readerIndex(in.readerIndex() + objectSize);
            return obj;
        } finally {
            unmarshaller.close();
        }
    }
}
