/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.config;

import static java.lang.String.format;
import java.util.List;
import javax.annotation.Nullable;
import static org.apache.commons.lang3.ObjectUtils.firstNonNull;
import org.cmdbuild.cluster.ClusterConfiguration;
import static org.cmdbuild.cluster.ClusterConfiguration.CLUSTER_CONFIG_NAMESPACE;
import org.cmdbuild.cluster.NodeIdProviderExt;
import static org.cmdbuild.config.api.ConfigCategory.CC_ENV;
import org.cmdbuild.config.api.ConfigComponent;
import static org.cmdbuild.config.api.ConfigLocation.CL_FILE_ONLY;
import org.cmdbuild.config.api.ConfigValue;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.config.api.ConfigValue.TRUE;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.random.CmRandomUtils.randomId;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
@ConfigComponent(CLUSTER_CONFIG_NAMESPACE)
public class ClusterConfigurationImpl implements ClusterConfiguration, NodeIdProviderExt {

    @ConfigValue(key = CLUSTER_CONFIG_ENABLED, description = "enable cluster service", defaultValue = FALSE)
    private boolean isEnabled;

    @ConfigValue(key = "name", description = "cluster name; all cmdbuild instances with the same cluster name will try to connect and form a cluster", defaultValue = "CMDBuild-Cluster", category = CC_ENV)
    private String clusterName;

    @ConfigValue(key = CLUSTER_CONFIG_AUTO_RESTART, description = "restart cluster when node is found but not connected", defaultValue = TRUE, category = CC_ENV)
    private boolean autoRestart;

    @ConfigValue(key = CLUSTER_CONFIG_FAILURE_ACTION, description = "CMDBuild behavior when an ignite failure occurs, one of `restartignite`, `stopignite`, `stoptomcat` or `none`", defaultValue = "stopignite", category = CC_ENV)
    private String failureAction;

    @ConfigValue(key = "tcp.port", description = "default cluster tcp port", defaultValue = "47100", category = CC_ENV)
    private int tcpPort;

    @ConfigValue(key = CLUSTER_CONFIG_NODE_PORT, description = "local cluster node tcp port (override for this node); you usually need to change this only if you want to start a cluster with multiple nodes on the same host", defaultValue = "", location = CL_FILE_ONLY, category = CC_ENV)
    private Integer nodeTcpPort;

    @ConfigValue(key = CLUSTER_CONFIG_NODE_ADDR, description = "local cluster node tcp address; required if you have more than one address and whish to bind only to one of those", location = CL_FILE_ONLY, category = CC_ENV)
    private String nodeTcpAddr;

    @ConfigValue(key = CLUSTER_CONFIG_NODES, description = "cluster nodes, example: '10.0.0.1:47100,10.0.0.2:47100' or '10.0.0.1,10.0.0.2'", defaultValue = "", category = CC_ENV)
    private List<String> clusterNodes;

    @ConfigValue(key = "node.id", description = "this cluster node id (if not set, will use a generated node id)", defaultValue = "", location = CL_FILE_ONLY, category = CC_ENV)
    private String nodeId;

    @ConfigValue(key = "rpc.timeout", description = "cluster rpc timeout, in ms", defaultValue = "600000", category = CC_ENV)
    private Long rpcTimeout;

    @ConfigValue(key = "node.workDirectory", description = "local node work directory, used for persisted cluster data etc", category = CC_ENV, location = CL_FILE_ONLY)
    private String workDirectory;

    @ConfigValue(key = "node.workgroup", description = "local node workgroup (or work groups) for pinning of jobs processing", category = CC_ENV, location = CL_FILE_ONLY)
    private List<String> workgroup;

    private final String runtimeNodeId = format("%s/%s", ProcessHandle.current().pid(), randomId(4));

    @Override
    @Nullable
    public String getWorkDirectory() {
        return workDirectory;
    }

    @Override
    public List<String> getWorkgroups() {
        return workgroup;
    }

    @Override
    public String getClusterName() {
        return checkNotBlank(clusterName, "error: cluster name is null");
    }

    @Override
    public long getRpcTimeout() {
        return rpcTimeout;
    }

    @Override
    @Nullable
    public String getTcpAddr() {
        return nodeTcpAddr;
    }

    @Override
    public List<String> getClusterNodes() {
        return clusterNodes;
    }

    @Override
    public int getTcpPort() {
        return firstNonNull(nodeTcpPort, tcpPort);
    }

    @Override
    public boolean isClusterEnabled() {
        return isEnabled;
    }

    @Override
    public boolean autoRestartEnabled() {
        return autoRestart;
    }

    @Override
    public String failureAction() {
        return failureAction;
    }

    @Override
    public String getRuntimeInfo() {
        return runtimeNodeId;
    }

    @Override
    @Nullable
    public String getConfiguredNodeId() {
        return nodeId;
    }
}
