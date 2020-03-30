package com.qovery.intellij.plugin.completion.value;

import com.qovery.intellij.plugin.completion.QoveryCompletionHelper;
import com.qovery.intellij.plugin.util.SetUtil;

import java.util.Set;

public class ContextualProject implements ContextualValueSet {

    @Override
    public Set<String> offers(QoveryCompletionHelper completionHelper) {
        return SetUtil.of(completionHelper.getProjectName());
    }
}
