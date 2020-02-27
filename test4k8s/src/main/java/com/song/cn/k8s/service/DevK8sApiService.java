package com.song.cn.k8s.service;

import io.fabric8.kubernetes.api.model.NamespaceList;
import io.fabric8.kubernetes.api.model.NodeList;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;

/**
 * api调用服务的编写是项目的核心，包括初始化k8s对象，列出命令空间，列出节点3个功能
 */
public class DevK8sApiService {

    private static KubernetesClient kubernetesClient;

    private static Config config;

    /**
     * 初始化 -- 连接 k8s api server
     *
     * @return 初始化结果
     */
    public static String init() {
        String initResult = "Init Success";
        config = new ConfigBuilder()
                .withMasterUrl("http://192.168.80.130:8080")
                .build();
        try {
            kubernetesClient = new DefaultKubernetesClient(config);
        } catch (Exception e) {
            initResult = "Init failed";
            System.out.println(e.getMessage());
        }
        System.out.println(initResult);
        return initResult;
    }

    /**
     * 获取所有的命令空间
     *
     * @return 返回所有命名空间列表
     */
    public static NamespaceList listNamespace() {
        NamespaceList namespaceList;

        namespaceList = kubernetesClient.namespaces().list();

        return namespaceList;
    }

    /**
     * 列出集群包含的节点
     *
     * @return 返回节点列表
     */
    public static NodeList listNode() {
        NodeList nodeList;

        nodeList = kubernetesClient.nodes().list();

        return nodeList;
    }
}
