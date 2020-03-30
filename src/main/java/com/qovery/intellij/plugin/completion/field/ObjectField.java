package com.qovery.intellij.plugin.completion.field;

import org.apache.commons.lang.StringUtils;

import java.util.List;

public class ObjectField extends Field {

    private final List<Field> children;

    public ObjectField(String name, boolean required, String path, List<Field> children) {
        super(name, required, path);
        this.children = children;
    }

    @Override
    public String getPlaceholderSuffix(final int indentation) {
        return ":\n" + printYamlChildren(indentation + 2);
    }

    @Override
    public String getComplete(final int indentation) {
        final String indentationPadding = StringUtils.repeat(" ", indentation);
        return indentationPadding + getName() + getPlaceholderSuffix(indentation);
    }


    private String printYamlChildren(final int indentation) {
        if (children.isEmpty()) {
            return StringUtils.repeat(" ", indentation) + PLACEHOLDER;
        }

        final StringBuilder sb = new StringBuilder();

        for (final Field field : children) {
            sb.append(field.getComplete(indentation)).append("\n");
        }

        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }

        return sb.toString();
    }

    public List<Field> getChildren() {
        return children;
    }

}
