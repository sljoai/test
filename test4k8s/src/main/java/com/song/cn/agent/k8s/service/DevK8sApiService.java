package com.song.cn.agent.k8s.service;

import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.springframework.stereotype.Service;

/**
 * api调用服务的编写是项目的核心，包括初始化k8s对象，列出命令空间，列出节点3个功能
 */
@Service
public class DevK8sApiService {

    private static KubernetesClient kubernetesClient;

    private static Config config;

    /**
     * 初始化 -- 连接 k8s api server
     *
     * @return 初始化结果
     */
    public String init() {
        System.setProperty("kubeconfig", "./conf/admin.conf");
        String initResult = "Init Success";
        config = new ConfigBuilder()
                .withMasterUrl("https://192.168.80.129:6443")
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
    public NamespaceList listNamespace() {
        NamespaceList namespaceList;

        namespaceList = kubernetesClient.namespaces().list();

        return namespaceList;
    }

    /**
     * 列出集群包含的节点
     *
     * @return 返回节点列表
     */
    public NodeList listNode() {
        NodeList nodeList;

        nodeList = kubernetesClient.nodes().list();

        return nodeList;
    }

    /**
     * 手动创建Pod对象，并返回
     *
     * @param namespace
     * @param podName
     * @param containerName
     * @param imageName
     */
    public Pod createPod(String namespace,
                         String podName,
                         String containerName,
                         String imageName) {
        //TODO: 若namespace不存在，则创建
        // ObjectMeta 配置
        ObjectMeta objectMeta = new ObjectMetaBuilder()
                .withName(podName)
                .withNamespace(namespace)
                .build();

        //ContainerPort 端口配置
        ContainerPort containerPort = new ContainerPortBuilder()
                .withContainerPort(80)
                .withHostPort(8080)
                .build();

        //Container 配置
        Container container = new ContainerBuilder()
                .withName(containerName)
                .withImage("insight.registry.com:5000/" + namespace + "/" + imageName)
                .withPorts(containerPort)
                .build();

        //Spec 配置
        PodSpec podSpec = new PodSpecBuilder()
                .withContainers(container)
                .build();

        //Pod 配置
        Pod pod = new PodBuilder()
                .withApiVersion("v1")
                .withKind("Pod")
                .withMetadata(objectMeta)
                .withSpec(podSpec)
                .build();

        deletePod(namespace, podName);
        kubernetesClient.pods().create(pod);

        return pod;
    }


    /**
     * 删除Pod
     *
     * @param namespace 命名空间
     * @param podName   Pod名称
     * @return 被删除Pod详细信息
     */
    public Pod deletePod(String namespace, String podName) {
        Pod pod = null;
        int count = 0;
        while (true) {
            Pod oldPod = kubernetesClient.pods()
                    .inNamespace(namespace)
                    .withName(podName)
                    .get();
            if (oldPod == null || count > 10) {
                if (count != 0) {
                    System.out.println(" Pod has been Deleted");
                }
                break;
            }
            if (count == 0) {
                pod = oldPod;
                kubernetesClient.pods().inNamespace(namespace)
                        .withName(podName)
                        .delete();
            }
            count++;
        }
        return pod;
    }
}
