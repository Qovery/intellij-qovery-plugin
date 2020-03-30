package com.qovery.intellij.plugin.completion.domain;

import com.qovery.intellij.plugin.completion.Spec;

public class DatabaseSpec {
    @Spec.ValueList({"postgresql", "mysql", "mongodb"})
    @Spec.KeyRequired
    public String type;

    @Spec.ValueDatabaseVersion
    public String version;

    @Spec.KeyRequired
    public String name;
}
