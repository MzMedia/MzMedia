package cn.iocoder.yudao.module.media.media.abl.bean.hook;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OnRecordMp4ABLHookParam extends ABLHookParam{
    private String fileName;
    private String startTime;
    private String endTime;
    private long fileSize;
}
