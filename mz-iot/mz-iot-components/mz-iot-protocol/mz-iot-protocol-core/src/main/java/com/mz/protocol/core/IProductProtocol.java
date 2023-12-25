package com.mz.protocol.core;

public interface IProductProtocol extends IProtocol {

    void setHandler(IMessageHandler handler);

    void setCodec(ICodec codec);

    ICodec getCodec();
}
