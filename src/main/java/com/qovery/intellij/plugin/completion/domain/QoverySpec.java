package com.qovery.intellij.plugin.completion.domain;

import com.qovery.intellij.plugin.completion.Spec;

import java.util.List;

/**
 * Based on https://github.com/Qovery/qovery-cli/blob/master/util/qovery_yml.go and the documentation.
 */
public class QoverySpec {
    @Spec.KeyRequired
    public ApplicationSpec application;

    @Spec.ListOf(DatabaseSpec.class)
    public List<DatabaseSpec> databases;

    @Spec.ListOf(RouterSpec.class)
    public List<RouterSpec> routers;

//    Currently not in the doc of Qovery
//    @Spec.ListOf(BrokerSpec.class)
//    public List<BrokerSpec> brokers;
}
