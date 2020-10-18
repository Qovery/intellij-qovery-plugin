package com.qovery.intellij.plugin.completion.domain;

import com.qovery.intellij.plugin.completion.Spec;

public class ApplicationSpec {
    public String name;

    @Spec.ValueProject
    public String project;

    @Spec.ValueList({
            "aws/us-east-2",
            "aws/us-east-1",
            "aws/us-west-1",
            "aws/us-west-2",
            "aws/ap-east-1",
            "aws/ap-south-1",
            "aws/ap-northeast-3",
            "aws/ap-northeast-2",
            "aws/ap-southeast-1",
            "aws/ap-southeast-2",
            "aws/ap-northeast-1",
            "aws/ca-central-1",
            "aws/cn-north-1",
            "aws/cn-northwest-1",
            "aws/eu-central-1",
            "aws/eu-west-1",
            "aws/eu-west-2",
            "aws/eu-west-3",
            "aws/eu-north-1",
            "aws/me-south-1",
            "aws/sa-east-1",
            "aws/us-gov-east-1",
            "aws/us-gov-west-1"})
    @Spec.KeyName("cloud_region")
    public String cloudRegion;

    @Spec.KeyName("publicly_accessible")
    public boolean publiclyAccessible;

    @Spec.ValueFileEndWith("Dockerfile")
    public String dockerfile;

    public String cpu;

    @Spec.ValueSuffixList({"GB", "MB"})
    public String ram;
}
