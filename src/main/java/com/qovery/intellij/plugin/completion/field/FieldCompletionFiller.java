package com.qovery.intellij.plugin.completion.field;

import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.qovery.intellij.plugin.completion.QoveryCompletionHelper;

import java.util.List;


public class FieldCompletionFiller {

    public static void fill(
            final QoveryCompletionHelper completionHelper,
            final CompletionResultSet completionResultSet,
            List<Field> fields) {
        fields.forEach(field -> addUnique(completionHelper, completionResultSet, field));
    }

    private static void addUnique(final QoveryCompletionHelper completionHelper,
                                  final CompletionResultSet completionResultSet,
                                  final Field field) {
        if (completionHelper.isUniqueKey(field.getName())) {
            completionResultSet.addElement(
                    create(field, completionHelper.createInsertFieldHandler(field)));
        }
    }

    private static LookupElementBuilder create(final Field field) {
        LookupElementBuilder lookupElementBuilder = LookupElementBuilder.create(field, field.getName());

        if (field.isRequired()) {
            lookupElementBuilder = lookupElementBuilder.bold();
        }

        return lookupElementBuilder;
    }

    private static LookupElementBuilder create(
            final Field field, final InsertHandler<LookupElement> insertHandler) {
        return create(field).withInsertHandler(insertHandler);
    }
}
