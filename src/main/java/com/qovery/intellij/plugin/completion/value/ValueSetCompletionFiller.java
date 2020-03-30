package com.qovery.intellij.plugin.completion.value;

import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.qovery.intellij.plugin.completion.QoveryCompletionHelper;

import java.util.List;

public abstract class ValueSetCompletionFiller {

    public static void fill(final QoveryCompletionHelper completionHelper,
                            final CompletionResultSet completionResultSet,
                            List<ValueSet> values) {
        values.forEach(value -> addValue(completionHelper, completionResultSet, value));
    }

    private static void addValue(final QoveryCompletionHelper completionHelper,
                                 final CompletionResultSet completionResultSet,
                                 final ValueSet value) {
        value.getProposals().forEach(val -> {
            if (completionHelper.isUniqueArrayStringValue(val)) {
                completionResultSet.addElement(
                        create(val, completionHelper.createInsertValueHandler()));
            }
        });

    }

    private static LookupElementBuilder create(
            final String value, final InsertHandler<LookupElement> insertHandler) {
        return LookupElementBuilder.create(value).withInsertHandler(insertHandler);
    }
}

