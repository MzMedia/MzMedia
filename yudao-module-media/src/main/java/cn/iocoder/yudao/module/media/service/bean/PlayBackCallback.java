package cn.iocoder.yudao.module.media.service.bean;

public interface PlayBackCallback<T> {

    void call(PlayBackResult<T> msg);

}
