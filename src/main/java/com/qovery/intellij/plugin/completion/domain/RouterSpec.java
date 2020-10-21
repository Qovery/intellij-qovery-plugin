package com.qovery.intellij.plugin.completion.domain;

import com.qovery.intellij.plugin.completion.Spec;

import java.util.List;

public class RouterSpec {
    public String name;

    @Spec.ListOf(RouteSpec.class)
    public List<RouteSpec> routes;

    @Spec.KeyName("custom_domains") @Spec.ListOf(CustomDomainSpec.class)
    public List<CustomDomainSpec> customDomains;


}
