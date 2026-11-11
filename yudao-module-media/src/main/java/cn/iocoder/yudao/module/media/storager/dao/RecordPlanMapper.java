package cn.iocoder.yudao.module.media.storager.dao;

import cn.iocoder.yudao.module.media.service.bean.RecordPlan;
import cn.iocoder.yudao.module.media.service.bean.RecordPlanItem;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface RecordPlanMapper {

    @Insert(" <script>" +
            "INSERT INTO mz_record_plan (" +
            " name," +
            " snap," +
            " create_time," +
            " update_time) " +
            "VALUES (" +
            " #{name}," +
            " #{snap}," +
            " #{createTime}," +
            " #{updateTime})" +
            " </script>")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    void add(RecordPlan plan);

    @Insert(" <script>" +
            "INSERT INTO mz_record_plan_item (" +
            "start_time," +
            "stop_time, " +
            "week_day," +
            "plan_id) " +
            "VALUES" +
            "<foreach collection='planItemList' index='index' item='item' separator=','> " +
            "(#{item.startTime}, #{item.stopTime}, #{item.weekDay},#{planId})" +
            "</foreach> " +
            " </script>")
    void batchAddItem(@Param("planId") int planId, List<RecordPlanItem> planItemList);

    @Select("select * from mz_record_plan where  id = #{planId}")
    RecordPlan get(@Param("planId") Integer planId);

    @Select(" <script>" +
            " SELECT wrp.*, (select count(1) from mz_device_channel where record_plan_id = wrp.id) AS channelCount\n" +
            " FROM mz_record_plan wrp where  1=1" +
            " <if test='query != null'> AND (name LIKE concat('%',#{query},'%') escape '/' )</if> " +
            " </script>")
    List<RecordPlan> query(@Param("query") String query);

    @Update("UPDATE mz_record_plan SET update_time=#{updateTime}, name=#{name}, snap=#{snap} WHERE id=#{id}")
    void update(RecordPlan plan);

    @Delete("DELETE FROM mz_record_plan WHERE id=#{planId}")
    void delete(@Param("planId") Integer planId);

    @Select("select * from mz_record_plan_item where  plan_id = #{planId}")
    List<RecordPlanItem> getItemList(@Param("planId") Integer planId);

    @Delete("DELETE FROM mz_record_plan_item WHERE plan_id = #{planId}")
    void cleanItems(@Param("planId") Integer planId);

    @Select(" <script>" +
            " select wdc.id from mz_device_channel wdc left join mz_record_plan_item wrpi on wrpi.plan_id = wdc.record_plan_id " +
            " where  wrpi.week_day = #{week} and wrpi.start_time &lt;= #{index} and wrpi.stop_time &gt;= #{index} group by wdc.id" +
            " </script>")
    List<Integer> queryRecordIng(@Param("week") int week, @Param("index") int index);
}
