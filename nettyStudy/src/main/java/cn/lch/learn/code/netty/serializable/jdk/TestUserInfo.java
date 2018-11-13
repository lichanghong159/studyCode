package cn.lch.learn.code.netty.serializable.jdk;

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.msgpack.MessagePack;
import org.msgpack.template.Templates;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;

/**
 * <p></p>
 *
 * @author lichanghong  create by  2018/11/13 10:44
 **/
public class TestUserInfo {
    public static void main(String[] args) throws IOException {
        UserInfo userInfo = new UserInfo();
        userInfo.buildUserId(1000).buildUserName("测试数据");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(bos);
        os.writeObject(userInfo);
        os.flush();
        os.close();
        byte [] b = bos.toByteArray();
        System.out.println("JDK序列化后的长度:"+b.length);
        bos.close();
        byte [] bytes = userInfo.encode();
        System.out.println("字节数据序列化后的长度:"+bytes.length);
        MessagePack msgpack = new MessagePack();
        //编码
        byte [] raw =msgpack.write(userInfo);
        System.out.println("msg:"+raw.length);
        UserInfo userInfo1=   msgpack.read(raw, UserInfo.class);
        System.out.println("userInfo1 :"+userInfo1);
        ByteBuffer byteBuffer = ByteBuffer.allocate(bytes.length);
        byteBuffer.put(bytes);
        byteBuffer.flip();
        System.out.println(userInfo.decode(byteBuffer));
        new LengthFieldBasedFrameDecoder(65535,0,2,0,2);
    }
}
