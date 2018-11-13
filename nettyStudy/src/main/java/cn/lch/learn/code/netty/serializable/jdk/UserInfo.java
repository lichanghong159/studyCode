package cn.lch.learn.code.netty.serializable.jdk;

import org.msgpack.annotation.Message;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

/**
 * <p>jdk默认的序列化</p>
 *
 * @author lichanghong  create by  2018/11/13 10:31
 **/
@Message
public class UserInfo implements Serializable {
    private static final long serialVersionUID = -5246270404617866799L;
    private int userId;
    private String userName;

    /**
     * 建造者模式，链式编程
     * @param userName
     * @return
     */
    public UserInfo buildUserName(String userName){
        this.userName = userName;
        return this;
    }

    public UserInfo buildUserId(int userId){
        this.userId = userId;
        return this;
    }

    public final int getUserId() {
        return userId;
    }

    public final void setUserId(int userId) {
        this.userId = userId;
    }

    public final String getUserName() {
        return userName;
    }

    public final void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "userId=" + userId +
                ", userName='" + userName + '\'' +
                '}';
    }

    public byte[] encode(){
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        byte [] value = this.userName.getBytes();
        buffer.putInt(value.length);
        buffer.put(value);
        buffer.putInt(this.userId);
        buffer.flip();
        //手动释放，协助gc回收
        value = null;
        byte [] result = new byte[buffer.remaining()];
        buffer.get(result);
        return result;
    }

    public UserInfo decode(ByteBuffer buffer){
        int length = buffer.getInt();
        byte [] value = new byte[length];
        buffer.get(value);
        UserInfo userInfo = new UserInfo();
        userInfo.setUserName(new String(value, Charset.forName("UTF-8")));
        userInfo.setUserId(buffer.getInt());
        value = null;
        return userInfo;

    }
}
