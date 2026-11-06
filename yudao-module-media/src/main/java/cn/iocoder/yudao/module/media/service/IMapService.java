package cn.iocoder.yudao.module.media.service;

import cn.iocoder.yudao.module.media.vmanager.bean.MapConfig;
import cn.iocoder.yudao.module.media.vmanager.bean.MapModelIcon;

import java.util.List;

public interface IMapService {

    List<MapConfig> getConfig();

    List<MapModelIcon> getModelList();
}
