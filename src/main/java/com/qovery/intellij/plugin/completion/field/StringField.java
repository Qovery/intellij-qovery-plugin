package com.qovery.intellij.plugin.completion.field;

import org.apache.commons.lang.StringUtils;

public class StringField extends Field {

    public StringField(String name, boolean required, String path) {
        super(name, required, path);
    }

    @Override
    public String getPlaceholderSuffix(final int indentation) {
        return ": " + PLACEHOLDER;
    }

    @Override
    public String getComplete(final int indentation) {
        final String leftPadding = StringUtils.repeat(" ", indentation);
        return leftPadding + getName() + ": " + PLACEHOLDER;
    }
}
