package cn.lch.learn.code.netty.customprotocol.msg;

/**
 * <p> 通讯消息</p>
 *
 * @author lichanghong  create by  2018/11/14 13:42
 **/
public final class NettyMessage {
    /**
     * 消息头
     */
    private Header header;
    /**
     * 消息头
     */
    private Object body;

    public final Header getHeader() {
        return header;
    }

    public final void setHeader(Header header) {
        this.header = header;
    }

    public final Object getBody() {
        return body;
    }

    public final void setBody(Object body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "NettyMessage{" +
                "header=" + header +
                ", body=" + body +
                '}';
    }
}
