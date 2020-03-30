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
import com.intellij.util.text.CharArrayUtil;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

public class YamlInsertValueHandler implements InsertHandler<LookupElement> {

    private static final char SINGLE_QUOTE = '\'';
    private static final char DOUBLE_QUOTE = '"';

    private static final char[] RESERVED_YAML_CHARS = {
            ':', '{', '}', '[', ']', ',', '&', '*', '#', '?', '|', '-', '<', '>', '=', '!', '%', '@', '`'
    };

    @Override
    public void handleInsert(
            @NotNull final InsertionContext insertionContext,
            @NotNull final LookupElement lookupElement) {
        if (shouldUseQuotes(lookupElement)) {
            final boolean hasDoubleQuotes = hasStartingOrEndingQuoteOfType(insertionContext, lookupElement);

            if (hasDoubleQuotes) {
                handleEndingQuote(insertionContext, DOUBLE_QUOTE);
                handleStartingQuote(insertionContext, lookupElement, DOUBLE_QUOTE);
            } else {
                handleEndingQuote(insertionContext, SINGLE_QUOTE);
                handleStartingQuote(insertionContext, lookupElement, SINGLE_QUOTE);
            }
        }
    }

    private boolean shouldUseQuotes(final LookupElement lookupElement) {
        return StringUtils.containsAny(lookupElement.getLookupString(), RESERVED_YAML_CHARS);
    }

    private boolean hasStartingOrEndingQuoteOfType(
            final InsertionContext insertionContext,
            final LookupElement lookupElement) {
        final int caretOffset = insertionContext.getEditor().getCaretModel().getOffset();
        final int startOfLookupStringOffset = caretOffset - lookupElement.getLookupString().length();

        final boolean hasStartingQuote = hasStartingQuote(
                insertionContext,
                startOfLookupStringOffset,
                YamlInsertValueHandler.DOUBLE_QUOTE);

        final boolean hasEndingQuote = hasEndingQuote(
                insertionContext,
                caretOffset,
                YamlInsertValueHandler.DOUBLE_QUOTE);

        return hasStartingQuote || hasEndingQuote;
    }

    private boolean hasEndingQuote(
            final InsertionContext insertionContext, final int caretOffset, final char quoteType) {
        final CharSequence chars = insertionContext.getDocument().getCharsSequence();

        return CharArrayUtil.regionMatches(chars, caretOffset, String.valueOf(quoteType));
    }

    private boolean hasStartingQuote(
            final InsertionContext insertionContext,
            final int startOfLookupStringOffset,
            final char quoteType) {
        return insertionContext.getDocument().getText().charAt(startOfLookupStringOffset - 1)
                == quoteType;
    }

    private void handleStartingQuote(
            final InsertionContext insertionContext,
            final LookupElement lookupElement,
            final char quoteType) {
        final int caretOffset = insertionContext.getEditor().getCaretModel().getOffset();
        final int startOfLookupStringOffset = caretOffset - lookupElement.getLookupString().length();

        final boolean hasStartingQuote =
                hasStartingQuote(insertionContext, startOfLookupStringOffset, quoteType);

        if (!hasStartingQuote) {
            insertionContext
                    .getDocument()
                    .insertString(startOfLookupStringOffset, String.valueOf(quoteType));
        }
    }

    private void handleEndingQuote(final InsertionContext insertionContext, final char quoteType) {
        final int caretOffset = insertionContext.getEditor().getCaretModel().getOffset();

        final boolean hasEndingQuote = hasEndingQuote(insertionContext, caretOffset, quoteType);

        if (!hasEndingQuote) {
            insertionContext.getDocument().insertString(caretOffset, String.valueOf(quoteType));
        }
    }
}
