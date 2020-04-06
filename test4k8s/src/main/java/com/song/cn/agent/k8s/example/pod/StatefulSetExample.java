package com.song.cn.agent.k8s.example.pod;

import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.api.model.apps.StatefulSet;
import io.fabric8.kubernetes.api.model.apps.StatefulSetBuilder;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.utils.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * StatefulSet 操作
 */
public class StatefulSetExample {

    private static final Logger logger = LoggerFactory.getLogger(DeploymentExamples.class);


    public static void main(String[] args) {
        System.setProperty("kubeconfig", "./conf/insight.kubeconfig");
        String master = "https://192.168.80.129:6443/";
        Config config = new ConfigBuilder()
                .withMasterUrl(master)
                .build();
        String namespace = "default";
        String moduleName = "test";
        String roleName = "compute";
        int replicas = 2;

        //标签
        Map<String, String> labels = Collections.singletonMap("role", roleName);

        //资源
        Map<String, Quantity> resources = new HashMap<>();
        resources.put("memory", Quantity.parse("128Mi"));
        resources.put("cpu", Quantity.parse("100m"));

        try (KubernetesClient client = new DefaultKubernetesClient(config);) {
            //构建 headless service 元数据
            Service headless = new ServiceBuilder()
                    .withNewMetadata()
                    .withName(roleName)
                    .addToLabels(labels)
                    .endMetadata()
                    .withNewSpec()
                    .withClusterIP(null)
                    .withSelector(labels)
                    .endSpec()
                    .build();
            // 创建 Headless Service 资源
            Service service = client.services()
                    .inNamespace(namespace)
                    .createOrReplace(headless);

            Map<String, String> envs = new HashMap<>();
            envs.put("AUTH_IP", "192.168.80.129");
            envs.put("ZK_IP", "192.168.80.129");
            // 构建ConfigMap 元数据
            ConfigMap configMap = new ConfigMapBuilder()
                    .withNewMetadata()
                    .withName(moduleName)
                    .endMetadata()
                    .withData(envs)
                    .build();

            //创建 ConfigMap
            client.configMaps()
                    .inNamespace(namespace)
                    .createOrReplace(configMap);

            // 构建 StatefulSet 元数据
            StatefulSet statefulSet = new StatefulSetBuilder()
                    .withNewMetadata()
                    .withName(moduleName)
                    .endMetadata()
                    .withNewSpec()
                    .withServiceName(moduleName)
                    .withReplicas(replicas)
                    .withNewSelector()
                    .withMatchLabels(labels)
                    .endSelector()
                    .withNewTemplate()
                    .withNewMetadata()
                    .withName(moduleName)
                    .withLabels(labels)
                    .endMetadata()
                    .withNewSpec()
                    .withHostname(moduleName)
                    .withSubdomain(roleName)
                    .withRestartPolicy("Always")
                    .addNewContainer()
                    .withName(moduleName)
                    .withImage("insight.registry.com:5000/default/nginx:1.7.9")
                    .withImagePullPolicy("Always")
                    .withCommand(Lists.newArrayList("sh", "-c"))
                    .addNewArg("while true; do\n" +
                            "           echo -en '\\n';\n" +
                            "           printenv MY_CPU_REQUEST MY_CPU_LIMIT;\n" +
                            "           printenv MY_MEM_REQUEST MY_MEM_LIMIT;\n" +
                            "           sleep 3600;")
                    .withNewResources()
                    .withRequests(resources)
                    .withLimits(resources)
                    .endResources()
                    .addNewEnv()
                    .withNewName("MY_CPU_REQUEST")
                    .withNewValueFrom()
                    .withNewResourceFieldRef()
                    .withContainerName(moduleName)
                    .withResource("requests.cpu")
                    .endResourceFieldRef()
                    .endValueFrom()
                    .endEnv()
                    .addNewEnv()
                    .withNewName("MY_CPU_LIMIT")
                    .withNewValueFrom()
                    .withNewResourceFieldRef()
                    .withContainerName(moduleName)
                    .withResource("limits.cpu")
                    .endResourceFieldRef()
                    .endValueFrom()
                    .endEnv()
                    //TODO: REQUEST相关的环境变量待添加
                    .addNewEnv()
                    .withNewName("MY_POD_NAME")
                    .withNewValueFrom()
                    .withNewFieldRef()
                    .withFieldPath("metadata.name")
                    .endFieldRef()
                    .endValueFrom()
                    .endEnv()
                    //TODO: MY_POD_NAMESPACE 和 MY_POD_IP 相关环境变量待添加
                    .addNewEnv()
                    .withNewName("AUTH_IP")
                    .withNewValueFrom()
                    .withNewConfigMapKeyRef("AUTH_IP", roleName, true)
                    .endValueFrom()
                    .endEnv()
                    .addNewEnv()
                    .withNewName("ZK_IP")
                    .withNewValueFrom()
                    .withNewConfigMapKeyRef("ZK_IP", roleName, true)
                    .endValueFrom()
                    .endEnv()
                    .addNewVolumeMount()
                    .withName(moduleName)
                    .withMountPath("/opt/data/insight")
                    .endVolumeMount()
                    .endContainer()
                    .addNewVolume()
                    .withNewName(moduleName)
                    .withNewConfigMap()
                    .withNewName(moduleName)
                    .addNewItem()
                    .withKey("env")
                    .withNewPath("/opt/data/insight")
                    .endItem()
                    .endConfigMap()
                    .endVolume()
                    .endSpec()
                    .endTemplate()
                    .endSpec()
                    .build();
            client.apps()
                    .statefulSets()
                    .inNamespace(namespace)
                    .createOrReplace(statefulSet);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }

    }

    private static void log(String action, Object obj) {
        logger.info("{}: {}", action, obj);
    }

    private static void log(String action) {
        logger.info(action);
    }
}
