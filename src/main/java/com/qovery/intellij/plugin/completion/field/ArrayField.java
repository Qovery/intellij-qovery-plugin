package com.qovery.intellij.plugin.completion.field;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;

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
    protected void fillYamlSnippet(HashMap<String, Object> snippet) {
        ArrayList<String> s = new ArrayList<>();
        s.add("");
        snippet.put(getName(), s);
    }
}
