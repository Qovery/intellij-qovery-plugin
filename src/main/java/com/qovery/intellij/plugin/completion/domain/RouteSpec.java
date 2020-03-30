package com.qovery.intellij.plugin.completion.domain;

import com.qovery.intellij.plugin.completion.Spec;

import java.util.List;

public class RouteSpec {
    @Spec.ListOf(String.class)
    public List<String> paths;

    @Spec.KeyName("application_name")
    public String applicationName;
}
