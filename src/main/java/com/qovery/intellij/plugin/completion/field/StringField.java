package com.qovery.intellij.plugin.completion.field;

import java.util.HashMap;

public class StringField extends Field {

    public StringField(String name, boolean required, String path) {
        super(name, required, path);
    }

    @Override
    public String getPlaceholderSuffix(final int indentation) {
        return ": " + PLACEHOLDER;
    }

    @Override
    protected void fillYamlSnippet(HashMap<String, Object> snippet) {
        snippet.put(getName(), "");
    }
}
