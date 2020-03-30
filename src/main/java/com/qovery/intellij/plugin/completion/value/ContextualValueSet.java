package com.qovery.intellij.plugin.completion.value;

import com.qovery.intellij.plugin.completion.QoveryCompletionHelper;

import java.util.Set;

@FunctionalInterface
public interface ContextualValueSet {

    Set<String> offers(QoveryCompletionHelper completionHelper);

}
