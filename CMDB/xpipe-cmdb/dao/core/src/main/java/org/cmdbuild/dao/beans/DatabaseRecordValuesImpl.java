package org.cmdbuild.dao.beans;

import java.util.Map;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

public class DatabaseRecordValuesImpl implements DatabaseRecordValues {

    private final Map<String, Object> map;

    public DatabaseRecordValuesImpl(Map<String, Object> map) {
        this.map = map(map).immutable();
    }

    @Override
    public Object get(String key) {
        return map.get(key);
    }

    @Override
    public Iterable<Map.Entry<String, Object>> getAttributeValues() {
        return map.entrySet();
    }

    @Override
    public Map<String, Object> toMap() {
        return map;
    }

}
