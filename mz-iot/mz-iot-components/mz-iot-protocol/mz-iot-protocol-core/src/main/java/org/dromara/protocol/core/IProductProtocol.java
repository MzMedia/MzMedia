package org.dromara.protocol.core;

public interface IProductProtocol extends IProtocol {

    void setHandler(IMessageHandler handler);

    void setCodecType(String codecType);

    String getCodecType();

    void setCodec(ICodec codec);

    ICodec getCodec();
}
