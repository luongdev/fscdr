package com.metechvn.jsoncdr.entities;

import com.metechvn.es.DynamicIndex;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class JsonCdr extends DynamicIndex implements Map<String, Object> {

    @Field(type = FieldType.Object, name = "properties")
    private final Map<String, Object> json = new HashMap<>();

    @Override
    public int size() {
        return json.size();
    }

    @Override
    public boolean isEmpty() {
        return json.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return json.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return json.containsValue(value);
    }

    @Override
    public Object get(Object key) {
        return json.get(key);
    }

    @Nullable
    @Override
    public Object put(String key, Object value) {
        return json.put(key, value);
    }

    @Override
    public Object remove(Object key) {
        return json.remove(key);
    }

    @Override
    public void putAll(@NotNull Map<? extends String, ?> m) {
        json.putAll(m);
    }

    @Override
    public void clear() {
        json.clear();
    }

    @NotNull
    @Override
    public Set<String> keySet() {
        return json.keySet();
    }

    @NotNull
    @Override
    public Collection<Object> values() {
        return json.values();
    }

    @NotNull
    @Override
    public Set<Entry<String, Object>> entrySet() {
        return json.entrySet();
    }
}
