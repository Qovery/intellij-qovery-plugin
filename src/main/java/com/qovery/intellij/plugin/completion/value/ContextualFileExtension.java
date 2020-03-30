package com.qovery.intellij.plugin.completion.value;

import com.qovery.intellij.plugin.completion.QoveryCompletionHelper;

import java.util.Set;

public class ContextualFileExtension implements ContextualValueSet {

    private final String endWith;

    public ContextualFileExtension(String endWith) {
        this.endWith = endWith;
    }

    @Override
    public Set<String> offers(QoveryCompletionHelper completionHelper) {
        return completionHelper.getFilesEndingWith(endWith);
    }
}
