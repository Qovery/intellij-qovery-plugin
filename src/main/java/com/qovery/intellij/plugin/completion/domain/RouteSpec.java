package com.qovery.intellij.plugin.completion.domain;

import com.qovery.intellij.plugin.completion.Spec;

import java.util.List;

public class RouteSpec {
    @Spec.KeyName("application_name")
    public String applicationName;

    @Spec.ListOf(value = String.class, primitive = true)
    public List<String> paths;
}
