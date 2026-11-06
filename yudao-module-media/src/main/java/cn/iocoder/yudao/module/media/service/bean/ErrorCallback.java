package cn.iocoder.yudao.module.media.service.bean;

public interface ErrorCallback<T> {

    void run(int code, String msg, T data);
}
