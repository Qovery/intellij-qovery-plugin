package com.qovery.intellij.plugin.completion.domain;

import com.qovery.intellij.plugin.completion.Spec;

public class StorageSpec {
    public String name;

    @Spec.ValueList({"slow_hdd", "hdd", "sdd", "fast_sdd"})
    public String type;

    @Spec.ValueSuffixList({"GB", "TB"})
    public String size;

    @Spec.KeyName("mount_point")
    public String mountPoint;
}
