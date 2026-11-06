package cn.iocoder.yudao.module.media.media.abl.bean.hook;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OnRecordProgressABLHookParam extends OnRecordMp4ABLHookParam {
    private Integer currentFileDuration;
    private Integer totalVideoDuration;
}
