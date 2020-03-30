package com.qovery.intellij.plugin.completion.field;

import org.apache.commons.lang.StringUtils;

public class ArrayField extends Field {

    public ArrayField(String name, boolean required, String path) {
        super(name, required, path);
    }

    @Override
    public String getPlaceholderSuffix(final int indentation) {
        final String nextLineIndentationPadding = StringUtils.repeat(" ", indentation + 2);
        return ":\n" + nextLineIndentationPadding + "- " + PLACEHOLDER;
    }

    @Override
    public String getComplete(final int indentation) {
        final String indentationPadding = StringUtils.repeat(" ", indentation);
        return indentationPadding + getName() + getPlaceholderSuffix(indentation);
    }
}
