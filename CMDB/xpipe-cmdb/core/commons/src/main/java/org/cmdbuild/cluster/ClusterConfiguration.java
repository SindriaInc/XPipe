/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.cluster;

import java.util.List;
import javax.annotation.Nullable;

public interface ClusterConfiguration extends NodeIdProvider {

    final static String CLUSTER_CONFIG_NAMESPACE = "org.cmdbuild.cluster",
            CLUSTER_CONFIG_NAMESPACE_PREFIX = CLUSTER_CONFIG_NAMESPACE + ".",
            CLUSTER_CONFIG_ENABLED = "enabled",
            CLUSTER_CONFIG_AUTO_RESTART = "auto.restart",
            CLUSTER_CONFIG_FAILURE_ACTION = "failure.action",
            CLUSTER_CONFIG_NODE_PORT = "node.tcp.port",
            CLUSTER_CONFIG_NODE_ADDR = "node.tcp.addr",
            CLUSTER_CONFIG_NODES = "nodes";

    boolean isClusterEnabled();

    boolean autoRestartEnabled();

    String failureAction();

    @Nullable
    String getTcpAddr();

    List<String> getClusterNodes();

    int getTcpPort();

    String getClusterName();

    long getRpcTimeout();

    @Nullable
    String getWorkDirectory();

    List<String> getWorkgroups();

    default boolean isSingleNode() {
        return !isClusterEnabled() || getClusterNodes().size() == 1;
    }

}
