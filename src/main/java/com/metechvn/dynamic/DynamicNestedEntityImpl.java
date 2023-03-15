package com.metechvn.dynamic;

import org.jetbrains.annotations.NotNull;

import java.util.AbstractMap;
import java.util.Set;

public class DynamicNestedEntityImpl extends AbstractMap<String, Object> implements DynamicNestedEntity {


    @NotNull
    @Override
    public Set<Entry<String, Object>> entrySet() {
        return null;
    }
}
