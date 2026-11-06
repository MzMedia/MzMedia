package cn.iocoder.yudao.module.media.streamProxy.dao.provider;

import cn.iocoder.yudao.module.media.common.enums.ChannelDataType;

import java.util.Map;

public class StreamProxyProvider {

    public String getBaseSelectSql(){
        return "SELECT " +
                " st.*, " +
                ChannelDataType.STREAM_PROXY +  " as data_type, " +
                " st.id as data_device_id, " +
                " wdc.*, " +
                " wdc.id as gb_id" +
                " FROM wvp_stream_proxy st " +
                " LEFT join wvp_device_channel wdc " +
                " on wdc.data_type = 3 and st.id = wdc.data_device_id ";
    }

    public String select(Map<String, Object> params ){
        return getBaseSelectSql() + " WHERE st.id = #{id}";
    }

    public String selectForPushingInMediaServer(Map<String, Object> params ){
        return getBaseSelectSql() + " WHERE st.pulling=true and st.media_server_id=#{mediaServerId} order by st.create_time desc";
    }

    public String selectOneByAppAndStream(Map<String, Object> params ){
        return getBaseSelectSql() + " WHERE st.app=#{app} AND st.stream=#{stream} order by st.create_time desc";
    }

    public String selectAll(Map<String, Object> params ){
        StringBuilder sqlBuild = new StringBuilder();
        sqlBuild.append(getBaseSelectSql());
        sqlBuild.append(" WHERE 1=1 ");
        if (params.get("query") != null) {
            sqlBuild.append(" AND (")
                    .append(" st.app LIKE concat('%',#{query},'%') escape '/'")
                    .append(" OR st.stream LIKE concat('%',#{query},'%') escape '/'")
                    .append(" OR wdc.gb_device_id LIKE concat('%',#{query},'%') escape '/'")
                    .append(" OR wdc.gb_name LIKE concat('%',#{query},'%') escape '/'")
                    .append(" )")
            ;
        }
        Object pulling = params.get("pulling");
        if (pulling != null) {
            if ((Boolean) pulling) {
                sqlBuild.append(" AND st.pulling=1 ");
            }else {
                sqlBuild.append(" AND st.pulling=0 ");
            }
        }
        if (params.get("mediaServerId") != null) {
            sqlBuild.append(" AND st.media_server_id=#{mediaServerId}");
        }
        sqlBuild.append(" order by st.create_time desc");
        return sqlBuild.toString();
    }
}
