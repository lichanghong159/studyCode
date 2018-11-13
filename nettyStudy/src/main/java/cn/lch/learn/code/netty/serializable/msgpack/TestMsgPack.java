package cn.lch.learn.code.netty.serializable.msgpack;

import org.msgpack.MessagePack;
import org.msgpack.template.Templates;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>messagePack序列化</p>
 *
 * @author lichanghong  create by  2018/11/13 11:35
 **/
public class TestMsgPack {
    public static void main(String[] args) throws IOException {
        List<String> src = new ArrayList<>();
        src.add("测试中");
        src.add("看看中文");
        src.add("能序列化吗？");
        src.add("我觉得可以");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(bos);
        os.writeObject(src);
        os.flush();
        os.close();
        byte [] b = bos.toByteArray();
        System.out.println("JDK序列化后的长度:"+b.length);
        MessagePack msgpack = new MessagePack();
       //编码
        byte [] raw =msgpack.write(src);
        System.out.println(raw.length);
        //解码
        List<String> dst = msgpack.read(raw, Templates.tList(Templates.TString));
        System.out.println(dst);
    }
}
