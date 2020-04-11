package com.qovery.intellij.plugin.completion.field;

import java.util.HashMap;

public abstract class Field {
    public static final String PLACEHOLDER = "<PH>";

    private final String name;
    private final boolean required;
    private final String path;


    Field(final String name, final boolean required, String path) {
        this.name = name;
        this.required = required;
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public abstract String getPlaceholderSuffix(final int indentation);

    protected abstract void fillYamlSnippet(HashMap<String, Object> snippet);

    public boolean isRequired() {
        return required;
    }

    public String getPath() {
        return path;
    }
}
