package org.dromara.network.client.message;

import lombok.Data;

@Data
//json`[{"key": "name", "value": "John", "remark": "名字"}, {"key": "age", "value": 25, "remark": "年龄"}]`
public class Item {
    private String key;
    private Object value;
    private String remark;
}
