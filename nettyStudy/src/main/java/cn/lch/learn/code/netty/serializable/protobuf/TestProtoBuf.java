package cn.lch.learn.code.netty.serializable.protobuf;

import com.google.protobuf.InvalidProtocolBufferException;

/**
 * <p></p>
 *
 * @author lichanghong  create by  2018/11/13 14:22
 **/
public class TestProtoBuf {

    private static byte [] encode (SubcribeReqProte.SubcribeReq req){
        return req.toByteArray();
    }

    private static SubcribeReqProte.SubcribeReq decode( byte [] body) throws InvalidProtocolBufferException {
        return SubcribeReqProte.SubcribeReq.parseFrom(body);
    }
    public static void main(String[] args) {
        SubcribeReqProte.SubcribeReq.Builder builder = SubcribeReqProte.SubcribeReq.newBuilder();
        builder.setAddress("测试地址");
        builder.setPreductName("测试商品");
        builder.setUserName("测试用户");
        builder.setSubReqID(100);
        SubcribeReqProte.SubcribeReq req = builder.build();
        System.out.println(req.toByteArray().length);
        System.out.println(req.toString());
    }
}
