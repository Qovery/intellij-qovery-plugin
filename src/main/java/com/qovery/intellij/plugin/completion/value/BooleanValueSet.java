package com.qovery.intellij.plugin.completion.value;

import com.qovery.intellij.plugin.util.SetUtil;

import java.util.Set;

public class BooleanValueSet extends ValueSet {

    public BooleanValueSet(String path) {
        super(SetUtil.of("true", "false"), path);
    }
}
