/*
 * Copyright 2013-2018 Lilinfeng.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.lch.learn.code.netty.customprotocol.codec;

import cn.lch.learn.code.netty.customprotocol.struct.Header;
import cn.lch.learn.code.netty.customprotocol.struct.NettyMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;



/**
 * @author Lilinfeng
 * @date 2014年3月15日
 * @version 1.0
 */
public class NettyMessageDecoder extends LengthFieldBasedFrameDecoder {

    MarshallingDecoder marshallingDecoder;

    public NettyMessageDecoder(int maxFrameLength, int lengthFieldOffset,
	    int lengthFieldLength) throws IOException {
	super(maxFrameLength, lengthFieldOffset, lengthFieldLength);
	marshallingDecoder = new MarshallingDecoder();
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in)
	    throws Exception {
	ByteBuf frame = (ByteBuf) super.decode(ctx, in);
	if (frame == null) {
	    return null;
	}

	NettyMessage message = new NettyMessage();
	Header header = new Header();
	header.setCrcCode(frame.readInt());
	header.setLength(frame.readInt());
	header.setSessionID(frame.readLong());
	header.setType(frame.readByte());
	header.setPriority(frame.readByte());
	if (frame.readableBytes() > 4) {
	    message.setBody(marshallingDecoder.decode(frame));
	}
	message.setHeader(header);
	return message;
    }
}
