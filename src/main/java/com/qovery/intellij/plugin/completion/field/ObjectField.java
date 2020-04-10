package com.qovery.intellij.plugin.completion.field;

import org.apache.commons.lang.StringUtils;
import org.jetbrains.yaml.YAMLTextUtil;
import org.jetbrains.yaml.YAMLUtil;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.util.HashMap;
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
    public void fillYamlSnippet(HashMap<String, Object> snippet) {
        HashMap<String, Object> childrenMap = new HashMap<>();
        for (final Field field : children) {
            field.fillYamlSnippet(childrenMap);
        }

        snippet.put(getName(), childrenMap);
    }


    private String printYamlChildren(final int indentation) {
        if (children.isEmpty()) {
            return StringUtils.repeat(" ", indentation) + PLACEHOLDER;
        }

        HashMap<String, Object> snippet = new HashMap<>();

        for (final Field field : children) {
            field.fillYamlSnippet(snippet);
        }


        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setPrettyFlow(true);

        return YAMLTextUtil.indentText(new Yaml(options).dump(snippet), indentation);
    }

    public List<Field> getChildren() {
        return children;
    }

}
