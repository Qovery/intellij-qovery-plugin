/*
The MIT License (MIT) Copyright © 2016 Zalando SE, https://tech.zalando.com

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
documentation files (the “Software”), to deal in the Software without restriction, including without limitation
the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and
to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of
the Software.

THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.qovery.intellij.plugin.yaml;

import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.editor.EditorModificationUtil;
import com.qovery.intellij.plugin.completion.field.Field;
import com.qovery.intellij.plugin.util.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class YamlInsertFieldHandler implements InsertHandler<LookupElement> {

    private final Field field;

    public YamlInsertFieldHandler(final Field field) {
        this.field = field;
    }

    @Override
    public void handleInsert(@NotNull final InsertionContext context, @NotNull final LookupElement item) {
        if (!StringUtils.nextCharAfterSpacesAndQuotesIsColon(
                getStringAfterAutoCompletedValue(context))) {
            final String suffixWithCaret = field.getPlaceholderSuffix(getIndentation(context, item));
            final String suffixWithoutCaret = suffixWithCaret.replace(Field.PLACEHOLDER, "");
            EditorModificationUtil.insertStringAtCaret(
                    context.getEditor(), suffixWithoutCaret, false, true, getCaretIndex(suffixWithCaret));
        }
    }

    private int getCaretIndex(final String suffix) {
        return suffix.indexOf(Field.PLACEHOLDER);
    }

    private int getIndentation(final InsertionContext context, final LookupElement item) {
        final String stringBeforeAutoCompletedValue = getStringBeforeAutoCompletedValue(context, item);
        return StringUtils.getNumberOfSpacesInRowStartingFromEnd(stringBeforeAutoCompletedValue);
    }

    @NotNull
    private String getStringAfterAutoCompletedValue(final InsertionContext context) {
        return context.getDocument().getText().substring(context.getTailOffset());
    }

    @NotNull
    private String getStringBeforeAutoCompletedValue(
            final InsertionContext context, final LookupElement item) {
        return context
                .getDocument()
                .getText()
                .substring(0, context.getTailOffset() - item.getLookupString().length());
    }
}
