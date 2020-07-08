package com.qovery.intellij.plugin.completion.value;

import com.qovery.intellij.plugin.completion.QoveryCompletionHelper;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class ContextualSuffix implements ContextualValueSet {

    private final String[] suffixList;
    private final String path;

    public ContextualSuffix(String[] value, String path) {
        this.suffixList = value;
        this.path = path;
    }

    @Override
    public Set<String> offers(QoveryCompletionHelper completionHelper) {
        String[] split = path.split("\\.");

        Optional<String> value = completionHelper.getValueOfKeyFromParentMapping(split[split.length - 1]);

        return value.filter(e -> !e.endsWith("TB") && !e.endsWith("GB"))
                .map(v -> Arrays.stream(suffixList).
                map(end -> v.trim() + end).collect(Collectors.toSet())).
                orElse(new HashSet<>());
    }
}
