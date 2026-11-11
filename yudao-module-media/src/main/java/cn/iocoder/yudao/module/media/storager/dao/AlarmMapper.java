package cn.iocoder.yudao.module.media.storager.dao;

import cn.iocoder.yudao.module.media.service.bean.Alarm;
import cn.iocoder.yudao.module.media.service.bean.AlarmType;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AlarmMapper {

    @Select("<script>" +
            "SELECT " +
            " wa.*," +
            " coalesce(wdc.gb_device_id, wdc.device_id) as channelDeviceId, " +
            " coalesce(wdc.gb_name, wdc.name) as channelName " +
            " FROM mz_alarm wa " +
            " LEFT join mz_device_channel wdc " +
            " on wdc.id = wa.channel_id " +
            " WHERE 1=1" +
            "<if test='alarmType != null and alarmType.size() > 0'>" +
            " AND wa.alarm_type IN " +
            "<foreach collection='alarmType' item='item' open='(' separator=',' close=')'>" +
            "#{item}" +
            "</foreach>" +
            "</if>" +
            "<if test='beginTimeLong != null'> AND wa.alarm_time &gt;= #{beginTimeLong}</if>" +
            "<if test='endTimeLong != null'> AND wa.alarm_time &lt;= #{endTimeLong}</if>" +
            " ORDER BY wa.alarm_time DESC" +
            "</script>")
    List<Alarm> getAlarms(@Param("alarmType") List<AlarmType> alarmType,
                          @Param("beginTimeLong") Long beginTimeLong,
                          @Param("endTimeLong") Long endTimeLong);

    @Delete("<script>" +
            "DELETE FROM mz_alarm WHERE id IN " +
            "<foreach collection='ids' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    void deleteAlarms(@Param("ids") List<Long> ids);

    @Insert("<script>" +
            "INSERT INTO mz_alarm (channel_id, description, snap_path, record_path, longitude, latitude, alarm_type, alarm_time)" +
            " VALUES " +
            "<foreach collection='handlerCatchDataList' item='item' separator=','>" +
            "(#{item.channelId}, #{item.description}, #{item.snapPath}, #{item.recordPath}, #{item.longitude}, #{item.latitude}, #{item.alarmType}, #{item.alarmTime})" +
            "</foreach>" +
            "</script>")
    void insertAlarms(@Param("handlerCatchDataList") List<Alarm> handlerCatchDataList);

    @Select("SELECT snap_path FROM mz_alarm WHERE id = #{id}")
    String getSnapPathById(@Param("id") Long id);

    @Delete("<script>" +
            "DELETE FROM mz_alarm WHERE 1=1" +
            "<if test='alarmType != null and alarmType.size() > 0'>" +
            " AND alarm_type IN " +
            "<foreach collection='alarmType' item='item' open='(' separator=',' close=')'>" +
            "#{item}" +
            "</foreach>" +
            "</if>" +
            "<if test='beginTimeLong != null'> AND alarm_time &gt;= #{beginTimeLong}</if>" +
            "<if test='endTimeLong != null'> AND alarm_time &lt;= #{endTimeLong}</if>" +
            "</script>")
    int deleteAlarmsByCondition(@Param("alarmType") List<AlarmType> alarmType,
                                @Param("beginTimeLong") Long beginTimeLong,
                                @Param("endTimeLong") Long endTimeLong);
}
