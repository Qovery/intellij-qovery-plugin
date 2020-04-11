package com.qovery.intellij.plugin.completion.field;

import org.apache.commons.lang.StringUtils;
import org.jetbrains.yaml.YAMLTextUtil;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ArrayObjectField extends Field {

    private final List<Field> children;

    public ArrayObjectField(String name, boolean required, String path, List<Field> children) {
        super(name, required, path);
        this.children = children;
    }

    @Override
    public String getPlaceholderSuffix(final int indentation) {
        return ":\n" + printYamlChildren(indentation + (indentation == 0 ? 2 : 0));
    }

    @Override
    protected void fillYamlSnippet(HashMap<String, Object> snippet) {
        ArrayList<Object> objects = new ArrayList<>();
        HashMap<String, Object> children = new HashMap<>();

        for (final Field field : this.children) {
            field.fillYamlSnippet(children);
        }

        objects.add(children);

        snippet.put(getName(), objects);
    }

    private String printYamlChildren(final int indentation) {
        if (children.isEmpty()) {
            return StringUtils.repeat(" ", indentation) + "- " + PLACEHOLDER;
        }

        ArrayList<Object> objects = new ArrayList<>();
        HashMap<String, Object> snippetOfArray = new HashMap<>();

        int i = 0 ;
        for (final Field field : children) {
            field.fillYamlSnippet(snippetOfArray);
        }

        objects.add(snippetOfArray);

        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setPrettyFlow(true);

        String beforeIndent = new Yaml(options).dump(objects);
        String afterIndent = YAMLTextUtil.indentText(beforeIndent, indentation);

        return afterIndent;
    }
}
