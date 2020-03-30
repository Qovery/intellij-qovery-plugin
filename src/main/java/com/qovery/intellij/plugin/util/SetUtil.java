package com.qovery.intellij.plugin.util;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class SetUtil {
    @SafeVarargs
    public static <T> Set<T> of(T ...elem) {
        Set<T> set = new HashSet<>();
        Collections.addAll(set, elem);
        return set;
    }
}
