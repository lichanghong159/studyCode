package cn.lch.learn.code.netty.customprotocol.codec;

import org.jboss.marshalling.*;

import java.io.IOException;

/**
 * <p>Marshalling工厂类 </p>
 *
 * @author lichanghong  create by  2018/11/14 14:14
 **/
public final class MarshallingFactory {
    /**
     * 获取MarshallingDecoder
     * @return
     */
    public static Unmarshaller buildUnMarshalling() throws IOException{
        final MarshallerFactory marshallerFactory = Marshalling
                .getProvidedMarshallerFactory("serial");
        final MarshallingConfiguration configuration = new MarshallingConfiguration();
        configuration.setVersion(5);
        final Unmarshaller unmarshaller = marshallerFactory
                .createUnmarshaller(configuration);
        return unmarshaller;
    }
    /**
     * 获取MarshallingDecoder
     * @return
     */
    public static Marshaller buildMarshalling() throws IOException {
        final MarshallerFactory marshallerFactory = Marshalling
                .getProvidedMarshallerFactory("serial");
        final MarshallingConfiguration configuration = new MarshallingConfiguration();
        configuration.setVersion(5);
        Marshaller marshaller = marshallerFactory
                .createMarshaller(configuration);
        return marshaller;
    }

}
